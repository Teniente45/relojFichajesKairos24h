package com.example.relojfichajeskairos24h

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
    }

    override fun onDisabled(context: Context, intent: Intent) {
    }
}