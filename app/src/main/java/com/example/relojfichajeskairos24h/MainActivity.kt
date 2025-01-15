package com.example.relojfichajeskairos24h

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Referencias
        val campoTexto: EditText = findViewById(R.id.campoTexto)
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
        val btnBorrar: Button = findViewById(R.id.btnBorrarTeclado)
        val btnEntrada: Button = findViewById(R.id.btn_entrada)
        val btnSalida: Button = findViewById(R.id.btn_salida)
        val cuadroEmergente: TextView = findViewById(R.id.cuadroEmergente)

        // Acción para los botones numéricos
        botonesNumericos.forEach { boton ->
            boton.setOnClickListener {
                val numero = boton.text.toString()
                campoTexto.append(numero)
                aplicarAnimacion(boton)
            }
        }

        // Acción para el botón Borrar
        btnBorrar.setOnClickListener {
            campoTexto.text.clear()
        }

        // Acción para el botón Entrada
        btnEntrada.setOnClickListener {
            if (campoTexto.text.isEmpty()) {
                mostrarMensajeError(cuadroEmergente, "¡Por favor, ingresa datos antes de continuar!")
            } else {
                val horaActual = obtenerHoraActual()
                mostrarCuadroEmergente(
                    cuadroEmergente,
                    "¡Hola usuario, has entrado a las $horaActual! ¡Que pases un buen día!"
                )
            }
        }

        // Acción para el botón Salida
        btnSalida.setOnClickListener {
            if (campoTexto.text.isEmpty()) {
                mostrarMensajeError(cuadroEmergente, "¡Por favor, ingresa datos antes de continuar!")
            } else {
                val horaActual = obtenerHoraActual()
                mostrarCuadroEmergente(
                    cuadroEmergente,
                    "¡Buen trabajo usuario, has salido a las $horaActual! ¡A descansar!"
                )
            }
        }
    }

    // Método para aplicar animación de escala a los botones
    private fun aplicarAnimacion(boton: Button) {
        val animacionEscalaX = ObjectAnimator.ofFloat(boton, "scaleX", 1f, 1.2f, 1f)
        val animacionEscalaY = ObjectAnimator.ofFloat(boton, "scaleY", 1f, 1.2f, 1f)

        animacionEscalaX.duration = 400
        animacionEscalaY.duration = 400

        val animacionSet = AnimatorSet()
        animacionSet.playTogether(animacionEscalaX, animacionEscalaY)
        animacionSet.start()
    }

    // Método para mostrar el cuadro emergente con animación
    private fun mostrarCuadroEmergente(cuadro: TextView, mensaje: String) {
        cuadro.text = mensaje
        cuadro.alpha = 0f
        cuadro.scaleX = 0f
        cuadro.scaleY = 0f
        cuadro.visibility = TextView.VISIBLE

        val animacionMostrarX = ObjectAnimator.ofFloat(cuadro, "scaleX", 0f, 1f)
        val animacionMostrarY = ObjectAnimator.ofFloat(cuadro, "scaleY", 0f, 1f)
        val animacionTransparencia = ObjectAnimator.ofFloat(cuadro, "alpha", 0f, 1f)

        val animacionSetMostrar = AnimatorSet()
        animacionSetMostrar.playTogether(animacionMostrarX, animacionMostrarY, animacionTransparencia)
        animacionSetMostrar.duration = 400

        animacionSetMostrar.start()

        // Ocultar el cuadro después de 4 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            val animacionOcultarX = ObjectAnimator.ofFloat(cuadro, "scaleX", 1f, 0f)
            val animacionOcultarY = ObjectAnimator.ofFloat(cuadro, "scaleY", 1f, 0f)
            val animacionTransparenciaOcultar = ObjectAnimator.ofFloat(cuadro, "alpha", 1f, 0f)

            val animacionSetOcultar = AnimatorSet()
            animacionSetOcultar.playTogether(animacionOcultarX, animacionOcultarY, animacionTransparenciaOcultar)
            animacionSetOcultar.duration = 400

            animacionSetOcultar.start()
            animacionSetOcultar.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    cuadro.visibility = TextView.GONE
                }
            })
        }, 4000)
    }

    // Método para mostrar un mensaje de error temporalmente
    private fun mostrarMensajeError(cuadro: TextView, mensaje: String) {
        cuadro.text = mensaje
        cuadro.alpha = 1f
        cuadro.scaleX = 1f
        cuadro.scaleY = 1f
        cuadro.visibility = TextView.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            cuadro.visibility = TextView.GONE
        }, 2000)
    }

    // Método para obtener la hora actual
    private fun obtenerHoraActual(): String {
        val formato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}
