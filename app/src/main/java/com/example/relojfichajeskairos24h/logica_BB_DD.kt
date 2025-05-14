package com.example.relojfichajeskairos24h

import com.example.relojfichajeskairos24h.BuildURL

import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject
import android.util.Log

// Clase SQLiteHelper para gestionar la base de datos local fichajes_pendientes
class FichajesSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "fichajes_pendientes", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crea la tabla 'informado' para almacenar fichajes no informados al servidor
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS informado (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                xFichaje TEXT,
                cTipFic TEXT,
                fFichaje TEXT,
                hFichaje TEXT,
                L_INFORMADO TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Elimina y vuelve a crear la tabla si hay cambios en la versión de la BBDD
        db.execSQL("DROP TABLE IF EXISTS informado")
        onCreate(db)
    }

    // Inserta un fichaje en la tabla sólo si L_INFORMADO == "N" y xFichaje no está vacío
    fun insertarSiEsInformadoNo(json: JSONObject) {
        val lInformado = json.optString("L_INFORMADO", "")
        val xFichaje = json.optString("xFichaje", "")

        Log.d("SQLite", "Revisando si se puede insertar: L_INFORMADO=$lInformado, xFichaje=$xFichaje")

        if (lInformado == "N" && xFichaje.isNotEmpty()) {
            val values = ContentValues().apply {
                put("xFichaje", xFichaje)
                put("cTipFic", json.optString("cTipFic", ""))
                put("fFichaje", json.optString("fFichaje", ""))
                put("hFichaje", json.optString("hFichaje", ""))
                put("L_INFORMADO", lInformado)
            }
            writableDatabase.insert("informado", null, values)
        }
    }
}

// Cliente HTTP y handler para ejecución periódica
private val client = OkHttpClient()
private val handler = Handler(Looper.getMainLooper())
private const val INTERVALO_REINTENTO = 10_000L // Intervalo de reintento: 10 segundos

// Inicia un bucle que cada 10 segundos reintenta enviar los fichajes no informados al servidor
fun iniciarReintentosAutomaticos(context: Context) {
    val dbHelper = FichajesSQLiteHelper(context)

    val tarea = object : Runnable {
        override fun run() {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM informado", null)

            while (cursor.moveToNext()) {
                // Extrae cada columna del registro pendiente
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val xFichaje = cursor.getString(cursor.getColumnIndexOrThrow("xFichaje"))
                val cTipFic = cursor.getString(cursor.getColumnIndexOrThrow("cTipFic"))
                val fFichaje = cursor.getString(cursor.getColumnIndexOrThrow("fFichaje"))
                val hFichaje = cursor.getString(cursor.getColumnIndexOrThrow("hFichaje"))

                Log.d("ReintentoFichaje", "Preparando reenvío de fichaje con ID=$id")

                // Construye la URL de fichaje usando la plantilla y los datos del registro
                val url = BuildURL.HOST + BuildURL.ACTION +
                        "&xFichaje=$xFichaje&cTipFic=$cTipFic&fFichaje=$fFichaje&hFichaje=$hFichaje"

                Log.d("ReintentoFichaje", "Invocando URL: $url")

                val request = Request.Builder().url(url).build()

                // Ejecuta la petición HTTP en segundo plano
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Si falla la conexión, no se hace nada (se reintentará después)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let { body ->
                            Log.d("ReintentoFichaje", "Respuesta recibida: $body")
                            try {
                                val json = JSONObject(body)
                                val lInformado = json.optString("L_INFORMADO", "")
                                // Si el servidor marca el fichaje como informado (L_INFORMADO distinto de "N"), se elimina
                                if (lInformado != "N") {
                                    Log.d("ReintentoFichaje", "L_INFORMADO != N → Eliminando ID=$id de la tabla informado")
                                    dbHelper.writableDatabase.delete("informado", "id = ?", arrayOf(id.toString()))
                                }
                            } catch (_: Exception) {
                                // Si falla el parseo, se ignora y se intentará de nuevo
                            }
                        }
                    }
                })
            }

            cursor.close()
            // Reprograma la ejecución para dentro de 10 segundos
            handler.postDelayed(this, INTERVALO_REINTENTO)
        }
    }

    // Inicia el primer ciclo de reintento
    handler.postDelayed(tarea, INTERVALO_REINTENTO)
}