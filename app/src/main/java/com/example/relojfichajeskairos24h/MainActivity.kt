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
    private val handler = Handler(Looper.getMainLooper())
    private val tiempoInactividad: Long = 5000 // 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)

        // Inicializa TextToSpeech
        tts = TextToSpeech(this, this)

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
                reiniciarTemporizador(handler) // Reinicia el temporizador al interactuar
            }
        }

        // Acción para el botón Borrar
        btnBorrar.setOnClickListener {
            campoTexto.text.clear()
            aplicarAnimacion(btnBorrar) // Animación para el botón Borrar
            reiniciarTemporizador(handler) // Reinicia el temporizador al interactuar
        }

        // Acción para el botón Entrada
        btnEntrada.setOnClickListener {
            val codigoUsuario = campoTexto.text.toString()
            if (codigoUsuario.isEmpty()) {
                mostrarMensajeError(cuadroEmergente, "¡Por favor, ingresa datos antes de continuar!")
            } else {
                // Crear una instancia de EstructuraDB y llamar a insertarFichaje
                val dbHelper = EstructuraDB(this)
                dbHelper.insertarFichaje(codigoUsuario, "ENTRADA") // EN para Entrada
                hablarTexto("¡Entrada correcta!")
            }
            aplicarAnimacion(btnEntrada) // Animación para el botón Entrada
            reiniciarTemporizador(handler) // Reinicia el temporizador al interactuar
        }

        // Acción para el botón Salida
        btnSalida.setOnClickListener {
            val codigoUsuario = campoTexto.text.toString()
            if (codigoUsuario.isEmpty()) {
                mostrarMensajeError(cuadroEmergente, "¡Por favor, ingresa datos antes de continuar!")
            } else {
                // Crear una instancia de EstructuraDB y llamar a insertarFichaje
                val dbHelper = EstructuraDB(this)
                dbHelper.insertarFichaje(codigoUsuario, "SALIDA") // SA para Salida
                hablarTexto("¡Salida correcta!")
            }
            aplicarAnimacion(btnSalida) // Animación para el botón Salida
            reiniciarTemporizador(handler) // Reinicia el temporizador al interactuar
        }

    }

    // Método para aplicar animación de escala a los botones
    private fun aplicarAnimacion(boton: Button) {
        val animacionEscalaX = ObjectAnimator.ofFloat(boton, "scaleX", 1f, 1.2f, 1f)
        val animacionEscalaY = ObjectAnimator.ofFloat(boton, "scaleY", 1f, 1.2f, 1f)

        animacionEscalaX.duration = 400 // Duración de 0.4 segundos
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

    // Método para reiniciar el temporizador de inactividad
    private fun reiniciarTemporizador(handler: Handler) {
        // Elimina cualquier tarea pendiente (si existe)
        handler.removeCallbacksAndMessages(null)

        // Establece una nueva tarea que vaciará el campo de texto después de 5 segundos
        handler.postDelayed({
            val campoTexto: EditText = findViewById(R.id.campoTexto)
            campoTexto.text.clear()
        }, tiempoInactividad)
    }
}
