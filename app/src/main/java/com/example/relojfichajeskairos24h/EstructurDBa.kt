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
        const val DATABASE_NAME = "fichajes_empresa.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "cppfictmp"

        // Nombres de columnas
        const val COLUMN_X_FICTMP = "X_FICTMP"
        const val COLUMN_EMP_X_EMPLEADO = "EMP_X_EMPLEADO"
        const val COLUMN_C_DISPOSITIVO = "C_DISPOSITIVO"
        const val COLUMN_C_TIPFIC = "C_TIPFIC"
        const val COLUMN_F_FICHAJE = "F_FICHAJE"
        const val COLUMN_L_INFORMADO = "L_INFORMADO"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DB", "onCreate ejecutado") // Asegúrate de que esto se loguea en Logcat.
        val CREATE_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            $COLUMN_X_FICTMP INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_EMP_X_EMPLEADO INTEGER NOT NULL,
            $COLUMN_C_DISPOSITIVO VARCHAR(20) DEFAULT NULL,
            $COLUMN_C_TIPFIC VARCHAR(7) NOT NULL,
            $COLUMN_F_FICHAJE VARCHAR(50) DEFAULT NULL,
            $COLUMN_L_INFORMADO CHAR(1) DEFAULT 'N',
            FOREIGN KEY ($COLUMN_EMP_X_EMPLEADO) REFERENCES cppempleados (X_EMPLEADO)
        );
    """
        db?.execSQL(CREATE_TABLE_QUERY)
        Log.d("DB", "Tabla $TABLE_NAME creada o ya existente")
    }

    fun insertarFichaje(empXEmpleado: String, cDispositivo: String?, cTipfic: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMP_X_EMPLEADO, empXEmpleado)
            put(COLUMN_C_DISPOSITIVO, cDispositivo)
            put(COLUMN_C_TIPFIC, cTipfic)
            put(COLUMN_F_FICHAJE, obtenerFechaHoraActual())
            put(COLUMN_L_INFORMADO, "N")
        }

        // Insertar en la tabla
        val result = db.insert(TABLE_NAME, null, values)
        if (result == -1L) {
            Log.e("DB", "Error al insertar el registro en la tabla $TABLE_NAME")
        } else {
            Log.d("DB", "Registro insertado correctamente en $TABLE_NAME con ID: $result")
        }
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DB", "Actualizando base de datos de versión $oldVersion a $newVersion")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // Elimina la tabla antigua
        onCreate(db) // Crea la tabla nueva
    }

    // Función para obtener la fecha y hora actual
    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }

    // Método opcional para leer todos los fichajes no informados
    fun obtenerFichajesNoInformados(): List<Map<String, String>> {
        val db = this.readableDatabase
        val listaFichajes = mutableListOf<Map<String, String>>()

        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_L_INFORMADO = 'N'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val registro = mapOf(
                    COLUMN_X_FICTMP to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_X_FICTMP)),
                    COLUMN_EMP_X_EMPLEADO to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMP_X_EMPLEADO)),
                    COLUMN_C_DISPOSITIVO to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_C_DISPOSITIVO)),
                    COLUMN_C_TIPFIC to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_C_TIPFIC)),
                    COLUMN_F_FICHAJE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_F_FICHAJE))
                )
                listaFichajes.add(registro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaFichajes
    }
}
