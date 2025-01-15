package com.example.relojfichajeskairos24h

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
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
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6),
            findViewById(R.id.btn7),
            findViewById(R.id.btn8),
            findViewById(R.id.btn9)
        )

        // Botón Borrar
        val btnBorrar: Button = findViewById(R.id.btnBorrarTeclado)

        // Acción para los botones numéricos
        botonesNumericos.forEach { boton ->
            boton.setOnClickListener {
                val numero = boton.text.toString()
                campoTexto.append(numero)

                // Aplicar animación al botón pulsado
                aplicarAnimacion(boton)
            }
        }

        // Acción para borrar el campo de texto completo
        btnBorrar.setOnClickListener {
            campoTexto.setText("") // Limpia todo el texto

            // Aplicar animación al botón borrar
            aplicarAnimacion(btnBorrar)
        }
    }

    // Método para aplicar animación de escala a los botones
    private fun aplicarAnimacion(boton: Button) {
        val animacionEscalaX = ObjectAnimator.ofFloat(boton, "scaleX", 1f, 1.2f, 1f)
        val animacionEscalaY = ObjectAnimator.ofFloat(boton, "scaleY", 1f, 1.2f, 1f)

        animacionEscalaX.duration = 400 // Duración de 0.4 segundos
        animacionEscalaY.duration = 400

        animacionEscalaX.interpolator = AccelerateDecelerateInterpolator()
        animacionEscalaY.interpolator = AccelerateDecelerateInterpolator()

        animacionEscalaX.start()
        animacionEscalaY.start()
    }
}
