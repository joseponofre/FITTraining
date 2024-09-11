package com.joseponofre.fittraining

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.joseponofre.fittraining.databinding.ActivityInstruccionesBinding


// Clase Instrucciones donde se muestran las instrucciones de los ejercicios.
class Instrucciones : AppCompatActivity() {

    private lateinit var binding: ActivityInstruccionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstruccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibe un intent de la activity anterior (Ejercicios) con un objeto "ejercicios" y muestra su imagen, nombre e instrucciones.

        val ejercicio: EjerciciosDataClass? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ejercicio", EjerciciosDataClass::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ejercicio")
        }

        if (ejercicio != null) {

            // Muestra el video del ejercicio
            val videoUri = Uri.parse("android.resource://com.joseponofre.fittraining/" + ejercicio.videoEjercicio)
            binding.vvEjercicio.setVideoURI(videoUri)

            // Configura el VideoView para que reproduzca el video en bucle
            binding.vvEjercicio.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }

            // Controles de reproducción
            binding.vvEjercicio.setMediaController(MediaController(this))
            binding.vvEjercicio.requestFocus()

            // Auto-iniciar el video
            binding.vvEjercicio.start()


            // Muestra el nombre del ejercicio y las instrucciones
            binding.tvNombreEjercicio.text = ejercicio.nombreEjercicio
            binding.tvInstrucciones.text = ejercicio.instruccionesEjercicio
        }
        // al pulsar sobre la flecha de "atrás", abrirá la activity anterior Ejercicios.
        binding.ivAtras.setOnClickListener {
            startActivity(Intent(this, Ejercicios::class.java))
        }
    }
}