package com.example.relojfichajeskairos24h

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object Logs {

    fun registrar(context: Context, mensaje: String) {
        try {
            val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logLine = "$timeStamp - $mensaje\n"
            val logFile = File(context.filesDir, "fichajes_log.txt")
            val writer = FileWriter(logFile, true)
            writer.append(logLine)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}