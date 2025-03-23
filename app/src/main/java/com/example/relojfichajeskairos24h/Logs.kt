package com.example.relojfichajeskairos24h

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object Logs {

    /**
     * Guarda un mensaje de log con marca de tiempo en un archivo local.
     * Cada día se guarda en un archivo diferente con el nombre: log_yyyy-MM-dd.txt
     */
    fun registrar(context: Context, mensaje: String) {
        try {
            val fechaActual = Date()
            val formatoHora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatoArchivo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val timeStamp = formatoHora.format(fechaActual)
            val nombreArchivo = "log_${formatoArchivo.format(fechaActual)}.txt"
            val logLine = "$timeStamp - $mensaje\n"

            val logFile = File(context.filesDir, nombreArchivo)

            FileWriter(logFile, true).use { writer ->
                writer.append(logLine)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}