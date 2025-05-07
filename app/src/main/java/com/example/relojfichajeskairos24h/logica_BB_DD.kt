package com.example.relojfichajeskairos24h

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BaseDeDatosHelper(context: Context) :
    SQLiteOpenHelper(context, "envios_pendientes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pendientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT NOT NULL,
                fecha TEXT,
                estado TEXT DEFAULT 'pendiente'
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pendientes")
        onCreate(db)
    }

    fun insertarFichajeFallido(url: String): Long {
        val db = writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val valores = ContentValues().apply {
            put("url", url)
            put("fecha", fechaActual)
            put("estado", "pendiente")
        }

        val resultado = db.insert("pendientes", null, valores)
        db.close()
        return resultado
    }

    fun obtenerPendientes(): List<Map<String, String>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, String>>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM pendientes WHERE estado = 'pendiente'", null)

        if (cursor.moveToFirst()) {
            do {
                val item = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")).toString(),
                    "url" to cursor.getString(cursor.getColumnIndexOrThrow("url")),
                    "fecha" to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    "estado" to cursor.getString(cursor.getColumnIndexOrThrow("estado"))
                )
                lista.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarPorId(id: Int): Int {
        val db = writableDatabase
        val filasAfectadas = db.delete("pendientes", "id = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas
    }

    fun eliminarTodos(): Int {
        val db = writableDatabase
        val filasAfectadas = db.delete("pendientes", null, null)
        db.close()
        return filasAfectadas
    }

    fun reenviarPendientes(context: Context, callbackEnvio: (url: String) -> Boolean) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM pendientes WHERE estado = 'pendiente'", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))

                val enviado = callbackEnvio(url)

                if (enviado) {
                    db.delete("pendientes", "id = ?", arrayOf(id.toString()))
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }
}