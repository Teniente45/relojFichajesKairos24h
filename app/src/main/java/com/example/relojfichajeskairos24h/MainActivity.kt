package com.example.relojfichajeskairos24h

import android.animation.ObjectAnimator
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var campoTexto: EditText
    private lateinit var mensajeDinamico: TextView
    private var mediaPlayer: MediaPlayer? = null

    private val stringBuilder = StringBuilder()
    private val duracionMensajeMs = 10000L

    companion object {
        val COLOR_INCORRECTO = "#DC143C".toColorInt()
        val COLOR_CORRECTO = "#4F8ABA".toColorInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        campoTexto = findViewById(R.id.campoTexto)
        mensajeDinamico = findViewById(R.id.mensajeDinamico)
        val btnEntrada = findViewById<Button>(R.id.btn_entrada)
        val btnSalida = findViewById<Button>(R.id.btn_salida)
        val btnBorrarTeclado = findViewById<Button>(R.id.btnBorrarTeclado)

        listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        ).forEach { id ->
            findViewById<Button>(id).setOnClickListener { view ->
                val button = view as? Button ?: return@setOnClickListener
                if (stringBuilder.length < 4) {
                    stringBuilder.append(button.tag ?: button.text.toString())
                    campoTexto.setText(stringBuilder.toString())
                    animarBoton(button)
                    resetearInactividad()
                }
            }
        }

        btnBorrarTeclado.setOnClickListener {
            borrarCampoTexto()
            resetearInactividad()
        }

        btnEntrada.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), "ENTRADA")
            borrarCampoTexto()
            resetearInactividad()
        }

        btnSalida.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), "SALIDA")
            borrarCampoTexto()
            resetearInactividad()
        }

        resetearInactividad()
    }

    private fun mostrarMensajeDinamico(texto: String, color: Int, nombreAudio: String? = null) {
        mensajeDinamico.text = texto
        mensajeDinamico.setTextColor(color)
        mensajeDinamico.textSize = 25f
        mensajeDinamico.visibility = View.VISIBLE

        nombreAudio?.let { reproducirAudio(it) }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            mensajeDinamico.visibility = View.GONE
        }, duracionMensajeMs)
    }

    private fun manejarCodigoEntradaSalida(codigo: String, tipo: String) {
        codigo.toIntOrNull()?.let {
            if (hayConexionInternet()) {
                val url = BuildURL.setfichaje
                    .replace("cEmpCppExt=", "cEmpCppExt=${URLEncoder.encode(it.toString(), "UTF-8")}")
                    .replace("cTipFic=", "cTipFic=${URLEncoder.encode(tipo, "UTF-8")}")
                enviarFichajeAServidor(url)
            } else {
                mostrarMensajeDinamico("No estás conectado a Internet", COLOR_INCORRECTO, "no_internet")
                Log.d("FichajeApp", "No hay conexión. Fichaje guardado localmente.")
            }
        } ?: mostrarMensajeDinamico("Código no válido", COLOR_INCORRECTO)
    }

    @Suppress("DEPRECATION")
    private fun hayConexionInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun obtenerHoraActual(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private fun animarBoton(button: Button) {
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.5f, 1f)
        val alpha = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f, 1f)
        scaleX.duration = 400
        scaleY.duration = 400
        alpha.duration = 400
        val interpolator = LinearInterpolator()
        scaleX.interpolator = interpolator
        scaleY.interpolator = interpolator
        alpha.interpolator = interpolator
        scaleX.start()
        scaleY.start()
        alpha.start()
    }

    private fun resetearInactividad() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            borrarCampoTexto()
        }, duracionMensajeMs)
    }

    private fun borrarCampoTexto() {
        campoTexto.setText("")
        stringBuilder.clear()
    }

    private fun enviarFichajeAServidor(url: String) {
        Thread {
            try {
                val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                }

                connection.inputStream.use { stream ->
                    val responseText = stream.bufferedReader().use { it.readText() }
                    Log.d("FichajeApp", "Respuesta del servidor: $responseText")

                    val respuesta = Gson().fromJson(responseText, RespuestaFichaje::class.java)

                    runOnUiThread {
                        val codigoEnviado = url.substringAfter("cEmpCppExt=").substringBefore("&")
                        val esFichajeCorrecto = respuesta.message.isNullOrBlank()
                        val tipo = respuesta.cTipFic?.uppercase()

                        val mensajeVisual = if (esFichajeCorrecto)
                            "($codigoEnviado) $tipo correcta a las ${respuesta.hFichaje}h"
                        else
                            "($codigoEnviado) Fichaje Incorrecto"

                        val nombreAudio = when {
                            !esFichajeCorrecto -> "codigo_incorrecto"
                            tipo == "ENTRADA" -> "fichaje_de_entrada"
                            tipo == "SALIDA" -> "fichaje_de_salida_correcto"
                            else -> null
                        }

                        val color = if (esFichajeCorrecto) COLOR_CORRECTO else COLOR_INCORRECTO
                        mostrarMensajeDinamico(mensajeVisual, color, nombreAudio)

                        Logs.registrar(this, mensajeVisual)
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    val codigoEnviado = url.substringAfter("cEmpCppExt=").substringBefore("&")
                    val errorMsgVisual = "($codigoEnviado) Error de conexión al fichar"
                    mostrarMensajeDinamico(errorMsgVisual, COLOR_INCORRECTO, "no_internet")
                    Logs.registrar(this, "$errorMsgVisual: ${e.localizedMessage}")
                }
            }
        }.start()
    }

    private fun reproducirAudio(nombreArchivo: String) {
        val resId = resources.getIdentifier(nombreArchivo, "raw", packageName)
        if (resId != 0) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer?.start()
        } else {
            Log.e("Audio", "No se encontró el archivo de audio: $nombreArchivo")
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}

data class RespuestaFichaje(
    val code: Int,
    val message: String?,
    val xFichaje: String?,
    val cTipFic: String?,
    val fFichaje: String?,
    val hFichaje: String?
)