package com.example.relojfichajeskairos24h

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

// Este BroadcastReceiver se activa al reiniciar el dispositivo.
// Lanza automáticamente la MainActivity para mantener el modo quiosco.
class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo reiniciado. Iniciando app automáticamente.")
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launchIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                    // No se puede activar directamente el modo quiosco aquí porque no tenemos una Activity activa.
                    Log.d("BootReceiver", "Modo quiosco pendiente de activar desde MainActivity.")
                }
            }
        }
    }
}