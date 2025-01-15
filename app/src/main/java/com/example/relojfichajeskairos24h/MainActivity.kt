package com.example.relojfichajeskairos24h

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Inicializa TextToSpeech
        tts = TextToSpeech(this, this)

        // Referencias
        val campoTexto: EditText = findViewById(R.id.campoTexto)
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
                hablarTexto("¡Buenos días!")
            }
        }

        // Acción para el botón Salida
        btnSalida.setOnClickListener {
            if (campoTexto.text.isEmpty()) {
                mostrarMensajeError(cuadroEmergente, "¡Por favor, ingresa datos antes de continuar!")
            } else {
                hablarTexto("¡Buen trabajo!")
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

    // Método para convertir texto a voz
    private fun hablarTexto(texto: String) {
        if (::tts.isInitialized) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // Implementación del OnInitListener
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val idioma = Locale.getDefault()
            val result = tts.setLanguage(idioma)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Aquí puedes manejar el caso en que el idioma no esté soportado
            }
        }
    }

    // Liberar recursos de TextToSpeech
    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
