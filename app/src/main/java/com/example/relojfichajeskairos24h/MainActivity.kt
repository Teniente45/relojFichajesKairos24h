package com.example.relojfichajeskairos24h

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textToSpeech: TextToSpeech
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var campoTexto: EditText
    private lateinit var mensajeDinamico: TextView

    // Variable que almacena el número ingresado
    private val stringBuilder = StringBuilder()
    // Const para la duración de los mensajes emergentes
    private val duracionMensajeMs = 10000L
    // Colores del mensaje de alerta si es un error o acierto
    companion object {
        const val COLOR_INCORRECTO = Color.RED
        const val COLOR_CORRECTO = Color.BLUE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Lee los mensajes de TextView en alto
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale("es", "ES") // Español
                textToSpeech.voices.forEach {
                    Log.d("VocesDisponibles", "Nombre: ${it.name}, Locale: ${it.locale}")
                }
            }
        }

        // Inicializar vistas y base de datos
        campoTexto = findViewById(R.id.campoTexto)
        mensajeDinamico = findViewById(R.id.mensajeDinamico)
        val btnEntrada = findViewById<Button>(R.id.btn_entrada)
        val btnSalida = findViewById<Button>(R.id.btn_salida)
        val btnBorrarTeclado = findViewById<Button>(R.id.btnBorrarTeclado)
        // dbHelper = EstructuraDB(this) // Inicialización de la base de datos

        // Botones numéricos
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

        // Botón borrar
        btnBorrarTeclado.setOnClickListener {
            borrarCampoTexto()
            resetearInactividad()
        }

        // Botón entrada
        btnEntrada.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), "ENTRADA")
            borrarCampoTexto()
            resetearInactividad()
        }

        // Botón salida
        btnSalida.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), "SALIDA")
            borrarCampoTexto()
            resetearInactividad()
        }

        // Iniciar el temporizador de inactividad
        resetearInactividad()
    }

    private fun mostrarMensajeDinamico(texto: String, color: Int, textoParaVoz: String = texto) {
        mensajeDinamico.text = texto
        mensajeDinamico.setTextColor(color)
        mensajeDinamico.textSize = 20f
        mensajeDinamico.visibility = View.VISIBLE

        // Leer solo el mensaje deseado
        textToSpeech.speak(textoParaVoz, TextToSpeech.QUEUE_FLUSH, null, null)

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            mensajeDinamico.visibility = View.GONE
        }, duracionMensajeMs)
    }

    // Funcion encargada de enviar los fichajes al servidor
    private fun manejarCodigoEntradaSalida(codigo: String, tipo: String) {
        codigo.toIntOrNull()?.let {
            if (hayConexionInternet()) {
                val url = BuildURL.setfichaje
                    .replace("cEmpCppExt=", "cEmpCppExt=${URLEncoder.encode(it.toString(), "UTF-8")}")
                    .replace("cTipFic=", "cTipFic=${URLEncoder.encode(tipo, "UTF-8")}")
                enviarFichajeAServidor(url)
            } else {
                // Mostrar mensaje de error sin conexión
                mostrarMensajeDinamico("No estás conectado a Internet", COLOR_INCORRECTO)
                Log.d("FichajeApp", "No hay conexión. Fichaje guardado localmente.")
            }
        } ?: mostrarMensajeDinamico("Código no válido", COLOR_INCORRECTO)
    }

    // Comprobar conexión a internet
    @Suppress("DEPRECATION")
    private fun hayConexionInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Obtener la hora actual en formato HH:mm:ss
    private fun obtenerHoraActual(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    // Animar el botón con un efecto de agrandar y transparencia
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

    // Reiniciar el temporizador de inactividad
    private fun resetearInactividad() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            borrarCampoTexto()
        }, duracionMensajeMs)
    }

    // Función para borrar el campo de texto
    private fun borrarCampoTexto() {
        campoTexto.setText("")
        stringBuilder.clear()
    }

    // Funcion que se encarga de enviar los fichajes al servidor y obtener respuesta
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

                        val voces = textToSpeech.voices
                        val vozMasculina = voces.find { it.locale.language == "es" && it.name.contains("std-m", ignoreCase = true) }
                        val vozFemenina = voces.find { it.locale.language == "es" && it.name.contains("std-f", ignoreCase = true) }

                        if (respuesta.cTipFic.equals("ENTRADA", ignoreCase = true)) {
                            if (vozMasculina != null) {
                                textToSpeech.voice = vozMasculina
                                textToSpeech.setPitch(1.0f)
                                Log.d("VozUsada", "Usando voz masculina real: ${vozMasculina.name}")
                            } else {
                                val voz = voces.find { it.name == "es-ES-SMTf00" }
                                voz?.let { textToSpeech.voice = it }
                                textToSpeech.setPitch(0.75f)
                                Log.d("VozUsada", "Simulando voz masculina con pitch grave")
                            }
                        } else if (respuesta.cTipFic.equals("SALIDA", ignoreCase = true)) {
                            if (vozFemenina != null) {
                                textToSpeech.voice = vozFemenina
                                textToSpeech.setPitch(1.0f)
                                Log.d("VozUsada", "Usando voz femenina real: ${vozFemenina.name}")
                            } else {
                                val voz = voces.find { it.name == "es-ES-SMTf00" }
                                voz?.let { textToSpeech.voice = it }
                                textToSpeech.setPitch(1.25f)
                                Log.d("VozUsada", "Simulando voz femenina con pitch agudo")
                            }
                        }

                        val mensajeVisual = if (respuesta.message.isNullOrBlank())
                            "($codigoEnviado) ${respuesta.cTipFic} correcta a las ${respuesta.hFichaje}h"
                        else "($codigoEnviado) Fichaje Incorrecto"

                        val mensajeVoz = if (respuesta.message.isNullOrBlank())
                            "${respuesta.cTipFic} correcta a las ${respuesta.hFichaje}horas"
                        else "Fichaje Incorrecto"

                        val color = if (respuesta.message.isNullOrBlank()) COLOR_CORRECTO else COLOR_INCORRECTO
                        mostrarMensajeDinamico(mensajeVisual, color, mensajeVoz)

                        Logs.registrar(this, mensajeVisual)
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    val codigoEnviado = url.substringAfter("cEmpCppExt=").substringBefore("&")
                    val errorMsgVisual = "($codigoEnviado) Error de conexión al fichar"
                    val errorMsgVoz = "Error de conexión al fichar"
                    mostrarMensajeDinamico(errorMsgVisual, COLOR_INCORRECTO, errorMsgVoz)
                    Logs.registrar(this, "$errorMsgVisual: ${e.localizedMessage}")
                }
            }
        }.start()
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}

// Clases de datos
data class RespuestaFichaje(
    val code: Int,
    val message: String?,
    val xFichaje: String?,
    val cTipFic: String?,
    val fFichaje: String?,
    val hFichaje: String?
)