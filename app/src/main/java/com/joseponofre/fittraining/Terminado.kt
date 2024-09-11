package com.joseponofre.fittraining

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.ActivityTerminadoBinding
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

// Activity Terminado que se muestra tras finalizar un entrenamiento. Extiende de la clase Navigation (para navegar entre las distintas activitys).
class Terminado : Navigation() {

    private lateinit var binding: ActivityTerminadoBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mediaPlayer: MediaPlayer

    override fun getLayoutId(): Int {
        return R.layout.activity_terminado
    }

    private var numEjercicios: Int = 0
    private var duracionTotal: Int = 0
    private var totalCalorias: Int = 0
    private lateinit var diaEntrenamiento: String
    private lateinit var horaEntrenamiento: String
    private lateinit var horaEntreno: LocalTime
    private var usuarioActual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityTerminadoBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root)

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId)
        }

        // Muestra la imagen de "Terminado"
        Glide.with(this).load(R.drawable.img_finalizado).into(binding.imgTerminado)

        // Recibe el intente desde la activity Entrenamiento con la información del entrenamiento realizado (día, hora duración, nº de ejercicios, total de calorías y el usuario que ha realizado el entrenamiento).
        val nuevoEntrenamiento = intent
        diaEntrenamiento = nuevoEntrenamiento.getStringExtra("diaEntrenamiento").toString()
        horaEntrenamiento = nuevoEntrenamiento.getStringExtra("horaEntrenamiento").toString()
        duracionTotal = nuevoEntrenamiento.getIntExtra("duracionEntrenamiento", 1)
        numEjercicios = nuevoEntrenamiento.getIntExtra("numEjercicios", 1)
        totalCalorias = nuevoEntrenamiento.getIntExtra("totalCalorias", 6)
        usuarioActual = nuevoEntrenamiento.getIntExtra("idUsuario", 0)


        // Recoge la fecha del entrenamiento y la transforma al formato requerido
        val formatoOriginal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val diaEntrenamientoDate = formatoOriginal.parse(diaEntrenamiento)
        val nuevoFormato = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.getDefault())

        // Formatea la fecha en el formato necesario para poder insertarla en la bbdd
        val fechaEntreno = nuevoFormato.format(diaEntrenamientoDate!!)
            .let { fechaFormateada -> nuevoFormato.parse(fechaFormateada) }

        horaEntreno = LocalTime.parse(horaEntrenamiento)

        // crea una instancia de la bbdd para poder guardar el entrenamiento que acaba de finalizar con sus datos (fecha, hora, duración, nº ejercicios, total calorías y usuario)
        val dbEntrenamiento = FITTrainingSQLite(this)
        val entrenamiento = EntrenamientosDataClass(
            fechaEntreno!!,
            horaEntreno,
            duracionTotal,
            numEjercicios,
            totalCalorias,
            usuarioActual
        )
        dbEntrenamiento.insertarNuevoEntrenamiento(entrenamiento)

        // Muestra en un cardview los datos del entrenamiento que acaba terminar.
        binding.tvFecha.text = getString(R.string.day, diaEntrenamiento)
        binding.tvHora.text = getString(R.string.hour, horaEntrenamiento)
        binding.tvNumEjercicios.text = getString(R.string.exercises_, numEjercicios.toString())
        binding.tvDuracion.text = getString(R.string.minutes, duracionTotal.toString())
        binding.tvKcal.text = getString(R.string.kcal, totalCalorias.toString())

        // Reproduce el sonido de "Terminado" con aplausos
        mediaPlayer = MediaPlayer.create(this, R.raw.s_terminado_aplausos)
        mediaPlayer.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        // Libera el MediaPlayer cuando ya no se necesite
        mediaPlayer.release()
    }

}