package com.example.relojfichajeskairos24h

import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var campoTexto: EditText
    //private lateinit var dbHelper: EstructuraDB // Base de datos local

    // Variable que almacena el número ingresado
    private val stringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Inicializar vistas y base de datos
        campoTexto = findViewById(R.id.campoTexto)
        val btnEntrada = findViewById<Button>(R.id.btn_entrada)
        val btnSalida = findViewById<Button>(R.id.btn_salida)
        // dbHelper = EstructuraDB(this) // Inicialización de la base de datos
        val btnBorrarTeclado = findViewById<Button>(R.id.btnBorrarTeclado)

        // Botones numéricos
        listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        ).forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (stringBuilder.length < 4) {
                    stringBuilder.append(it.tag ?: (it as Button).text.toString())
                    campoTexto.setText(stringBuilder.toString())
                    animarBoton(it as Button)
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

    // Funcion encargada de enviar los fichajes al servidor
    private fun manejarCodigoEntradaSalida(codigo: String, tipo: String) {
        codigo.toIntOrNull()?.let {
            if (hayConexionInternet()) {
                val url = BuildURL.setfichaje
                    .replace("cEmpCppExt=", "cEmpCppExt=${URLEncoder.encode(it.toString(), "UTF-8")}")
                    .replace("cTipFic=", "cTipFic=${URLEncoder.encode(tipo, "UTF-8")}")
                enviarFichajeAServidor(url)
            } else {
                Log.d("FichajeApp", "No hay conexión. Fichaje guardado localmente.")
            }
        } ?: Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show()
    }

    // Comprobar conexión a internet
    private fun hayConexionInternet(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
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
        scaleX.interpolator = LinearInterpolator()
        scaleY.interpolator = LinearInterpolator()
        alpha.interpolator = LinearInterpolator()
        scaleX.start()
        scaleY.start()
        alpha.start()
    }

    // Reiniciar el temporizador de inactividad
    private fun resetearInactividad() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            borrarCampoTexto()
        }, 5000)
    }

    // Función para borrar el campo de texto
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
                        val mensaje = if (respuesta.message.isNullOrBlank())
                            "Fichaje ${respuesta.cTipFic} correcto a las ${respuesta.hFichaje}"
                        else "Error: ${respuesta.message}"

                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                        Logs.registrar(this, mensaje)
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión al fichar", Toast.LENGTH_SHORT).show()
                    Logs.registrar(this, "Error de conexión al fichar: ${e.localizedMessage}")
                }
            }
        }.start()
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
