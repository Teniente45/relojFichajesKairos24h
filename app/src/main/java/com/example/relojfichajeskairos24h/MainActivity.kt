package com.example.relojfichajeskairos24h

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Referencia al campo de texto
        val campoTexto: EditText = findViewById(R.id.campoTexto)

        // Botones del teclado numérico
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

        // Botón Borrar
        val btnBorrar: Button = findViewById(R.id.btnBorrarTeclado)

        // Botón OK
        val btnOk: Button = findViewById(R.id.btnOk)

        // Acción para los botones numéricos
        botonesNumericos.forEach { boton ->
            boton.setOnClickListener {
                val numero = boton.text.toString()
                campoTexto.append(numero)
            }
        }

        // Acción para borrar un carácter
        btnBorrar.setOnClickListener {
            val textoActual = campoTexto.text.toString()
            if (textoActual.isNotEmpty()) {
                campoTexto.setText(textoActual.substring(0, textoActual.length - 1))
            }
        }

        // Acción para el botón OK
        btnOk.setOnClickListener {
            // Aquí puedes manejar lo que debe ocurrir al presionar OK
            // Por ejemplo, podrías mostrar un mensaje o realizar una operación
        }
    }
}
