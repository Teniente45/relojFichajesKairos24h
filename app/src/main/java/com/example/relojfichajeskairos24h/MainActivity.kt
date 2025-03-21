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
    // private lateinit var dbHelper: EstructuraDB // Base de datos local

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

        // Botones numéricos
        val botonesNumericos = listOf(
            findViewById<Button>(R.id.btn0),
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7),
            findViewById<Button>(R.id.btn8),
            findViewById<Button>(R.id.btn9)
        )

        val btnBorrarTeclado = findViewById<Button>(R.id.btnBorrarTeclado)

        // Leer empleados desde JSON
        val empleados = leerEmpleadosDesdeJSON()

        // Configurar botones numéricos
        for ((index, button) in botonesNumericos.withIndex()) {
            button.setOnClickListener {
                if (stringBuilder.length < 4) { // Limitar a 4 dígitos
                    stringBuilder.append(index)
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
            manejarCodigoEntradaSalida(stringBuilder.toString(), empleados, "ENTRADA")
            /*borrarCampoTexto()
            resetearInactividad()

             */
        }
        // Botón salida
        btnSalida.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), empleados, "SALIDA")
            /*borrarCampoTexto()
            resetearInactividad()

             */
        }
        // Iniciar el temporizador de inactividad
        resetearInactividad()
    }

    // Función para manejar códigos de entrada y salida
    private fun manejarCodigoEntradaSalida(
        codigo: String,
        empleados: List<Empleado>, // ya no se usará
        tipo: String
    ) {
        val codigoInt = codigo.toIntOrNull()
        if (codigoInt != null) {
            val cTipfic = tipo
            if (hayConexionInternet()) {
                val codigoEncoded = URLEncoder.encode(codigoInt.toString(), "UTF-8")
                val tipoEncoded = URLEncoder.encode(cTipfic, "UTF-8")

                val url = BuildURL.setfichaje
                    .replace("cEmpCppExt=", "cEmpCppExt=$codigoEncoded")
                    .replace("cTipFic=", "cTipFic=$tipoEncoded")

                enviarFichajeAServidor(url)
            } else {
                Log.d("FichajeApp", "No hay conexión. Fichaje guardado localmente.")
            }
        } else {
            Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show()
        }
    }

    // Comprobar conexión a internet
    private fun hayConexionInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Obtener la hora actual en formato HH:mm:ss
    private fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatoHora.format(Date())
    }

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

    // Leer empleados desde el archivo JSON
    private fun leerEmpleadosDesdeJSON(): List<Empleado> {
        return try {
            val inputStream = assets.open("codigos.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val empleadosResponse = gson.fromJson(reader, EmpleadosResponse::class.java)
            empleadosResponse.empleados
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar los empleados", Toast.LENGTH_LONG).show()
            emptyList()
        }
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
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                val responseText = connection.inputStream.bufferedReader().use { it.readText() }

                Log.d("FichajeApp", "Respuesta del servidor: $responseText")

                val gson = Gson()
                val respuesta = gson.fromJson(responseText, RespuestaFichaje::class.java)

                runOnUiThread {
                    if (respuesta.message.isNullOrBlank()) {
                        Toast.makeText(this, "Fichaje ${respuesta.cTipFic} correcto a las ${respuesta.hFichaje}", Toast.LENGTH_LONG).show()
                        Logs.registrar(this, "Fichaje ${respuesta.cTipFic ?: "DESCONOCIDO"}: ${respuesta.message ?: "Correcto"} a las ${respuesta.hFichaje ?: "?"}")
                    } else {
                        Toast.makeText(this, "Error: ${respuesta.message}", Toast.LENGTH_LONG).show()
                        Logs.registrar(this, "Fichaje ${respuesta.cTipFic ?: "DESCONOCIDO"}: ${respuesta.message ?: "Correcto"} a las ${respuesta.hFichaje ?: "?"}")
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
data class Empleado(
    val X_EMPLEADO: Int,
    val D_EMPLEADO: String
)

data class EmpleadosResponse(
    val empleados: List<Empleado>
)

data class RespuestaFichaje(
    val code: Int,
    val message: String?,
    val xFichaje: String?,
    val cTipFic: String?,
    val fFichaje: String?,
    val hFichaje: String?
)
