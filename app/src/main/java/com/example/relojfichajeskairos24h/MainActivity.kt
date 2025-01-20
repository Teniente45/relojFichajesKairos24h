package com.example.relojfichajeskairos24h

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var campoTexto: EditText

    // Variable que almacena el número ingresado
    private val stringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Inicializar vistas
        campoTexto = findViewById(R.id.campoTexto)
        val btnEntrada = findViewById<Button>(R.id.btn_entrada)
        val btnSalida = findViewById<Button>(R.id.btn_salida)

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
                if (stringBuilder.length < 6) { // Limitar a 6 dígitos
                    stringBuilder.append(index)
                    campoTexto.setText(stringBuilder.toString())
                    // Animación al presionar el botón
                    animarBoton(button)
                    resetearInactividad()  // Reiniciar temporizador
                }
            }
        }

        // Botón borrar
        btnBorrarTeclado.setOnClickListener {
            borrarCampoTexto()  // Borrar todo el contenido del campo de texto
            resetearInactividad()  // Reiniciar temporizador
        }

        // Botón entrada
        btnEntrada.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), empleados, "Entrada")
            borrarCampoTexto()  // Borrar el campo de texto después de la acción
            resetearInactividad()  // Reiniciar temporizador
        }

        // Botón salida
        btnSalida.setOnClickListener {
            manejarCodigoEntradaSalida(stringBuilder.toString(), empleados, "Salida")
            borrarCampoTexto()  // Borrar el campo de texto después de la acción
            resetearInactividad()  // Reiniciar temporizador
        }

        // Iniciar el temporizador de inactividad
        resetearInactividad()
    }

    // Función para manejar códigos de entrada y salida
    private fun manejarCodigoEntradaSalida(
        codigo: String,
        empleados: List<Empleado>,
        tipo: String
    ) {
        val codigoInt = codigo.toIntOrNull()
        if (codigoInt != null) {
            if (comprobarCodigoEmpleado(codigoInt, empleados)) {
                val horaActual = obtenerHoraActual()  // Obtener la hora actual
                val mensaje = "$tipo registrada correctamente a las $horaActual"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, ingrese un código válido", Toast.LENGTH_SHORT).show()
        }
    }

    // Obtener la hora actual en formato HH:mm:ss
    private fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatoHora.format(Date())
    }

    // Animar el botón con un efecto de agrandar y transparencia
    private fun animarBoton(button: Button) {
        // Animación para agrandar el botón
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.5f, 1f)
        val alpha = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f, 1f)

        // Duración de la animación
        scaleX.duration = 400
        scaleY.duration = 400
        alpha.duration = 400

        // Interpolador para un movimiento suave
        scaleX.interpolator = LinearInterpolator()
        scaleY.interpolator = LinearInterpolator()
        alpha.interpolator = LinearInterpolator()

        // Iniciar las animaciones
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

    // Comprobar si el código ingresado es válido
    private fun comprobarCodigoEmpleado(codigo: Int, empleados: List<Empleado>): Boolean {
        return empleados.any { it.X_EMPLEADO == codigo }
    }

    // Reiniciar el temporizador de inactividad
    private fun resetearInactividad() {
        // Detener cualquier ejecución previa y reiniciar el temporizador
        handler.removeCallbacksAndMessages(null)

        // Volver a programar el borrado del campo de texto después de 5 segundos
        handler.postDelayed({
            borrarCampoTexto()  // Borra el contenido del campo de texto
        }, 5000) // 5000 milisegundos = 5 segundos
    }

    // Función para borrar el campo de texto
    private fun borrarCampoTexto() {
        campoTexto.setText("") // Borra el contenido del campo de texto
        stringBuilder.clear()  // Borra el contenido de la variable que almacena el número
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
