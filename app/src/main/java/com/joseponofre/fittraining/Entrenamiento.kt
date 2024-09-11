package com.joseponofre.fittraining

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.joseponofre.fittraining.databinding.ActivityEntrenamientoBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class Entrenamiento : AppCompatActivity() {

    private lateinit var binding: ActivityEntrenamientoBinding

    private var duracionTotal: Int = 0
    private var duracionTotalMinutos: Int = 0
    private var duracionEjercicios: Int = 0
    private var duracionDescanso: Int = 0
    private var numEjercicios: Int = 0
    private var numSecuencias: Int = 0
    private var secuenciaActual: Int = 0
    private var isEjercicioActivo = true
    private var isTimerRunning = true
    private var timer: CountDownTimer? = null
    private var tiempoInicial: Int = 0
    private var tiempoPausado: Long = 0
    private lateinit var intentEjercicio: Intent
    private lateinit var ejerciciosList: ArrayList<EjerciciosDataClass>
    private var totalCalorias: Int = 0
    private lateinit var fechaHoraActual: LocalDateTime
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtiene los parámetros pasados desde la Activity Entrenar, con la duración total (del entrenamiento), la duración de los ejercicios y la duración de los descansos
        val duracion = intent
        duracionTotal = duracion.getIntExtra("duracionTotal", 10) * 1000
        duracionEjercicios = duracion.getIntExtra("duracionEjercicios", 40) * 1000
        duracionDescanso = duracion.getIntExtra("duracionDescanso", 20) * 1000

        @Suppress("RemoveRedundantCallsOfConversionMethods")
        // Calcula el número total de ejercicios que tendrá el entrenamiento a partir de las duraciones seleccionadas. Suma más +1 para evitar obtener 0 ejercicios (el entrenamiento tendrá "al menos" 1 ejercicio).
        numEjercicios = ((duracionTotal * 60) / (duracionEjercicios + duracionDescanso)).toInt() + 1
        // numSecuencias será el número total de ejercicios más el número total de descansos, o dicho de otra manera el numEjercicios multiplicado por 2 (-1 porque el último descanso no se muestra, tras el último ejercicio se muestra "Terminado").
        // numSecuencias servirá para controlar si ya se ha terminado el entrenamiento o quedan más ejercicios que mostrar.
        numSecuencias = (numEjercicios * 2) - 1

        // Al hacer click sobre el botón Pausar llama a pauseTimer (pausará el timer)
        binding.btPausar.setOnClickListener {
            pauseTimer()
        }
        // Al hacer click sobre el botón Reset llama a resetTimer (reseteará el timer)
        binding.btReset.setOnClickListener {
            resetTimer()
        }
        // Al hacer click sobre el botón Reanudar llama a resumeTimer (reanudará el timer por donde lo dejó)
        binding.btReanudar.setOnClickListener {
            resumeTimer()
        }

        // Al hacer click sobre el botón Cancelar, abrirá un AlertDialog que preguntará al usuario si desea "Abandonar el entrenamiento" o "Reanudar el entrenamiento"
        binding.btCancelar.setOnClickListener {
            timer?.cancel()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Cancelar el entrenamiento?")
            builder.setMessage("¿Estás seguro/a de que quieres cancelar y abandonar el entrenamiento? El progreso actual NO se guardará.")
            // Si hace click sobre "Abandonar entrenamiento", se terminará la activity (finish) y abrirá la activity Entrenar
            builder.setPositiveButton("Abandonar entrenamiento") { _, _ ->
                finish()
                // Abre la activity Entrenar
                startActivity(Intent(this, Entrenar::class.java))
            }
            // Si hace click sobre "Reanudar entrenamiento", el AlertDialog se cierra y llama a resumeTimer (reanuda el timer por donde lo dejó).
            builder.setNegativeButton("Reanudar entrenamiento") { dialog, _ ->
                dialog.dismiss()
                resumeTimer()
            }
            builder.show()

        }

        // Crea una instancia de FITTrainingSQLite para obtener la lista de ejercicios desde la base de datos
        val fitTrainingSQLite = FITTrainingSQLite(this)
        ejerciciosList = ArrayList(fitTrainingSQLite.getListaEjercicios())
        // LLama a la función generarIntentEjercicioAleatorio y a mostrarEjercicio, lo que mostrará un ejercicio de manera aleatoria de la bbdd
        intentEjercicio = generarIntentEjercicioAleatorio(ejerciciosList)
        mostrarEjercicio(intentEjercicio)
        // Actualiza el timer
        updateCountDownTimer()
        // Inicialmente, tiempoInicial será igual a la duración de los ejercicios seleccionada (el entrenamiento siempre comienza por un ejercicio, no por un descanso)
        tiempoInicial = duracionEjercicios
        // Configura la progressbar según la duración de los ejercicios
        binding.progressBar.max = duracionEjercicios
        binding.progressBar.progress = duracionEjercicios
        // LLama a startTimer (pone en marcha el timer) con el tiempoInicial que es igual a duracionEjercicios
        startTimer(tiempoInicial.toLong())

    } // Termina el onCreate

    // función startTimer que pone en marcha el timer (cuenta atrás)
    private fun startTimer(tiempoInicio: Long) {
        timer = object : CountDownTimer(tiempoInicio, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                // la variable tiempoInicial se actualizará a cada segundo que pase con el timer en marcha (esto será importante luego)
                tiempoInicial = millisUntilFinished.toInt()
                // actualiza el timer (cuenta atrás)
                updateCountDownTimer()
            }

            override fun onFinish() {
                // Cuando termina el timer (onFininsh), suma 1 a secuenciaActual
                secuenciaActual++
                // Pone el isTimerRunning a false (porque se ha terminado el tiempo)
                isTimerRunning = false
                // Si secuenciaAcual es mayor o igual que numSecuencias (que venía condicionado por la duración seleccionada por el usuario), significa que el entrenamiento ha terminado y llama a finEjericio.
                if (secuenciaActual >= numSecuencias) {
                    finEjercicio()
                } else {
                    // Si el entrenamiento no ha terminado...
                    if (isEjercicioActivo) {
                        // Cambia isEjercicioActivo a false, lo que significa que toca un descanso (venimos de hacer un ejercicio)
                        isEjercicioActivo = false
                        // El tiempo con el que empezará de nuevo el timer será el seleccionado por el usuario para los descansos, porque ahora toca un descanso
                        tiempoInicial = duracionDescanso
                        // Actualiza la progressbar a la duración del descanso
                        binding.progressBar.max = duracionDescanso
                        binding.progressBar.progress = duracionDescanso
                        // Llama a mostrarDescanso (que mostrará la imagen y texto correspondiente al descanso
                        mostrarDescanso()
                    } else {
                        // Si el entrenamiento no ha terminado...
                        // Cambia isEjercicioActivo a true, lo que significa que toca un ejercicio (venimos de hacer un descanso)
                        isEjercicioActivo = true
                        // El tiempo con el que empezará de nuevo el timer será el seleccionado por el usuario para los ejercicios, porque ahora toca un ejercicio
                        tiempoInicial = duracionEjercicios
                        // Actualiza la progressbar a la duración del descanso
                        binding.progressBar.max = duracionEjercicios
                        binding.progressBar.progress = duracionEjercicios
                        // genera un ejercicio aleatorio de la lista de ejercicios y muestra dicho ejercicio
                        intentEjercicio = generarIntentEjercicioAleatorio(ejerciciosList)
                        mostrarEjercicio(intentEjercicio)
                    }


                    // Iniciar el temporizador para el nuevo intervalo
                    startTimer(tiempoInicial.toLong())
                }
            }
        }.start()
        // Al comenzar el timer...
        // Cambiamos isTimerRunning a true
        isTimerRunning = true
        // El botón Reset se vuelve invisible
        binding.btReset.visibility = View.INVISIBLE
        // El botón Reanudar se vuelve invisible
        binding.btReanudar.visibility = View.INVISIBLE
        // El botón Cancelar se vuelve invisible
        binding.btCancelar.visibility = Button.INVISIBLE
    }
    // función que pausa el timer
    private fun pauseTimer() {
        // cancela el objeto timer (pausa el timer).
        timer?.cancel()
        // Cambia isTimerRunning a false
        isTimerRunning = false
        // Inicializa la variable tiempoPausado que será igual a tiempoInicial (que ya no valdrá lo mismo que inicialmente, porque se actualiza a cada segundo que pasa con el timer en marcha)
        tiempoPausado = tiempoInicial.toLong()
        // Hace visible el botón Reset, Reanudar y Cancelar; hace invisible el botón Pausar
        binding.btReset.visibility = View.VISIBLE
        binding.btReanudar.visibility = View.VISIBLE
        binding.btPausar.visibility = View.INVISIBLE
        binding.btCancelar.visibility = Button.VISIBLE

    }
    // función que resetea el timer
    private fun resetTimer() {
        // Si el ejercicio está activo (toca ejercicio y no descanso)...
        if (isEjercicioActivo) {
            // El tiempo inicial del timer será igual a la duración elegida por el usuario para los ejercicios
            tiempoInicial = duracionEjercicios
            // Llama a startTimer con el tiempoInicial actualizado a la duraciónEjercicios inicial elegida por el usuario para los ejercicios
            startTimer((tiempoInicial.toLong()))
        } else {
            // Si el ejercicio NO está activo (toca descanso y no ejercicio...)
            // El tiempo inicial del timer será igual a la duración elegida por el usuario para los descansos
            tiempoInicial = duracionDescanso
            // Llama a startTimer con el tiempoInicial actualizado a la duraciónDescanso inicial elegida por el usuario para los ejercicios
            startTimer(tiempoInicial.toLong())
        }
        // Hace visible el botón Pausar; hace invisible los botones Reset, Reanudar y Cancelar.
        binding.btReset.visibility = View.INVISIBLE
        binding.btReanudar.visibility = View.INVISIBLE
        binding.btPausar.visibility = View.VISIBLE
        binding.btCancelar.visibility = Button.INVISIBLE
    }
    // función que reanuda el timer
    private fun resumeTimer() {
        // Al reanudar el timer, lo hacer por el segundo en el que se quedó
        startTimer(tiempoPausado)
        // Hace visible el botón Pausar; hace invisibles los botones Reset, Reanudar y Cancelar.
        binding.btReset.visibility = View.INVISIBLE
        binding.btReanudar.visibility = View.INVISIBLE
        binding.btPausar.visibility = View.VISIBLE
        binding.btCancelar.visibility = Button.INVISIBLE
    }
    // función que actualiza el timer
    private fun updateCountDownTimer() {
        // calcula los segundos restantes
        val seconds = (tiempoInicial / 1000 % 60).toInt()
        // formatea el tiempo restante , mostrando dos dígitos
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds)
        // actualiza el textview con los segundos restantes (se actualiza cada segundo)
        binding.tvTimer.text = timeLeftFormatted
        // actualiza la progressbar según los segundos restantes (se actualiza cada segundo)
        binding.progressBar.progress = tiempoInicial
    }

    // Función para genera un intent con un ejercicio aleatorio
    private fun generarIntentEjercicioAleatorio(listaEjercicios: List<EjerciciosDataClass>): Intent {
        // Seleccionar un índice aleatorio de la lista
        val indiceAleatorio = (0 until listaEjercicios.size).random()

        // Obtiene el ejercicio correspondiente al índice aleatorio
        val ejercicioAleatorio = listaEjercicios[indiceAleatorio]

        // Crear un intent
        val intentEjercicioAleatorio = Intent().apply {
            // Agregar el ejercicio aleatorio al intent
            putExtra("ejercicio", ejercicioAleatorio)
        }

        return intentEjercicioAleatorio
    }
    // función que muestra el ejercicio generado en el intent
    private fun mostrarEjercicio(intent: Intent) {

        // Recupera el objeto "ejercicio" del intent

        val ejercicio: EjerciciosDataClass? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ejercicio", EjerciciosDataClass::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ejercicio")
        }

        // Verifica si se encontró el ejercicio
        if (ejercicio != null) {

            // Muestra el video del ejercicio
            val videoUri = Uri.parse("android.resource://com.joseponofre.fittraining/" + ejercicio.videoEjercicio)
            binding.vvEntrenamiento.setVideoURI(videoUri)

            // Configura el VideoView para que reproduzca el video en bucle
            binding.vvEntrenamiento.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }

            // Controles de reproducción
            binding.vvEntrenamiento.setMediaController(MediaController(this))
            binding.vvEntrenamiento.requestFocus()

            // Auto-iniciar el video
            binding.vvEntrenamiento.start()


            // muestra el nombre del ejercicio
            binding.tvEntrenamientoNombreEjercicio.text = ejercicio.nombreEjercicio
        }
    }
    // Función que muestra el descanso
    private fun mostrarDescanso() {
        // El vídeo que mostrará durante el descanso entre ejercicios

        val videoUri = Uri.parse("android.resource://com.joseponofre.fittraining/" + R.raw.v_rest)
        binding.vvEntrenamiento.setVideoURI(videoUri)

        binding.vvEntrenamiento.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
        }

        // Controles de reproducción
        binding.vvEntrenamiento.setMediaController(MediaController(this))
        binding.vvEntrenamiento.requestFocus()

        // Auto-iniciar el video
        binding.vvEntrenamiento.start()

        // Mostrará el texto de "Descansa"
        binding.tvEntrenamientoNombreEjercicio.text = getString(R.string.rest)
    }
    // Función que terminará el entrenamiento
    private fun finEjercicio() {
        // Llama a una instancia de la clase PreferenceManager (SharedPreferences) para obtener el id de usuario que está realizando el entrenamiento (el que tiene abierta su sesión).
        preferenceManager = PreferenceManager(this)
        val userId = preferenceManager.obtenerUserId()
        // Inicializa la variable fechaHoraActual a la fecha y hora actuales
        fechaHoraActual = LocalDateTime.now()

        // Formatea la fecha y la hora como strings según el formato indicado
        val diaEntrenamiento = fechaHoraActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val horaEntrenamiento = fechaHoraActual.format(DateTimeFormatter.ofPattern("HH:mm"))
        // calcula el total de calorías gastadas durante el entrenamiento multiplicando la duración del entrenamiento por 6 (seis calorías por cada minuto de entrenamiento).
        totalCalorias = (duracionTotal / 1000) * 6

        duracionTotalMinutos = (duracionTotal / 1000)
        // Pasará un intent con la información del entrenamiento a la siguiente activity "Terminado"
        val intent = Intent(this@Entrenamiento, Terminado::class.java)
        intent.putExtra("diaEntrenamiento", diaEntrenamiento)
        intent.putExtra("horaEntrenamiento", horaEntrenamiento)
        intent.putExtra("duracionEntrenamiento", duracionTotalMinutos)
        intent.putExtra("numEjercicios", numEjercicios)
        intent.putExtra("totalCalorias", totalCalorias)
        intent.putExtra("idUsuario", userId)
        // Abrirá la siguiente activity (Terminado)
        startActivity(intent)
    }
}