package com.example.relojfichajeskairos24h

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class EstructuraDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "fichajes"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "fictmp"

        // Nombres de columnas
        const val COLUMN_X_FICTMP = "X_FICTMP"
        const val COLUMN_C_EMPCPPEXT = "C_EMPCPPEXT"
        const val COLUMN_C_TIPFIC = "C_TIPFIC"
        const val COLUMN_F_FICHAJE = "F_FICHAJE"
        const val COLUMN_L_INFORMADO = "L_INFORMADO"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DB", "onCreate ejecutado") // Asegúrate de que esto se loguea en Logcat.
        val CREATE_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            $COLUMN_X_FICTMP INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_C_EMPCPPEXT VARCHAR(6) NOT NULL,
            $COLUMN_C_TIPFIC VARCHAR(2) NOT NULL,
            $COLUMN_F_FICHAJE DATETIME NOT NULL,
            $COLUMN_L_INFORMADO VARCHAR(1) NOT NULL DEFAULT 'N'
        );
    """
        db?.execSQL(CREATE_TABLE_QUERY)
    }


    fun insertarFichaje(cEmpcppext: String, cTipfic: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_C_EMPCPPEXT, cEmpcppext)
            put(COLUMN_C_TIPFIC, cTipfic)
            put(COLUMN_F_FICHAJE, obtenerFechaHoraActual())
            put(COLUMN_L_INFORMADO, "N")
        }
        val result = db.insert(TABLE_NAME, null, values)
        Log.d("DB", "Resultado de inserción: $result")
        db.close()
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si necesitas actualizar la base de datos (migraciones), puedes hacerlo aquí
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Función para obtener la fecha y hora actual
    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}
