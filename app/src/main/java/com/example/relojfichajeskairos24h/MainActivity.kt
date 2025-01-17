package com.example.relojfichajeskairos24h

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa el botón y campo de texto
        val codigoEditText = findViewById<EditText>(R.id.codigoEditText)
        val comprobarButton = findViewById<Button>(R.id.comprobarButton)

        // Obtener lista de empleados desde el archivo JSON
        val empleados = leerEmpleadosDesdeJSON()

        // Configurar la acción del botón
        comprobarButton.setOnClickListener {
            // Obtener el código ingresado por el usuario
            val codigoUsuario = codigoEditText.text.toString().toIntOrNull()

            if (codigoUsuario != null) {
                // Verificar si el código existe
                if (comprobarCodigoEmpleado(codigoUsuario, empleados)) {
                    // Si el código es válido
                    Toast.makeText(this, "Código válido", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el código es incorrecto
                    Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un código válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para leer el archivo JSON desde los assets
    private fun leerEmpleadosDesdeJSON(): List<Empleado> {
        val inputStream = assets.open("codigos.json")
        val reader = InputStreamReader(inputStream)
        val gson = Gson()
        val empleadosResponse = gson.fromJson(reader, EmpleadosResponse::class.java)
        return empleadosResponse.empleados
    }

    // Función para comprobar si el código de empleado es válido
    private fun comprobarCodigoEmpleado(codigo: Int, empleados: List<Empleado>): Boolean {
        return empleados.any { it.X_EMPLEADO == codigo }
    }
}

// Clase de datos para representar un empleado
data class Empleado(
    val X_EMPLEADO: Int,
    val D_EMPLEADO: String
)

// Clase que mapea la estructura completa de la respuesta JSON
data class EmpleadosResponse(
    val empleados: List<Empleado>
)
