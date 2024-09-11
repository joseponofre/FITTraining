package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.ActivityEntrenarBinding


// Activity Entrenar donde el usuario puede personalizar su entrenamiento, seleccionando la duración total del entrenamiento, la duración de los ejercicios y la duración de los descansos.
class Entrenar : Navigation() {

    private lateinit var binding: ActivityEntrenarBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun getLayoutId(): Int {
        return R.layout.activity_entrenar
    }

    private var duracionTotalSeleccionada = 0
    private var duracionEjerciciosSeleccionada = 0
    private var duracionDescansoSeleccionada = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityEntrenarBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root)

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_entrenar
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId)
        }

        // Asigna un OnClickListener al cardview "Duración total"
        binding.cvDuracionTotal.setOnClickListener {
            duracionTotal()
        }

        // Asigna un OnClickListener al cardview "Duración ejercicios"
        binding.cvDuracionEjercicios.setOnClickListener {
            duracionEjercicios()
        }

        // Asigna un OnClickListener al cardview "Duración descanso"
        binding.cvDuracionDescanso.setOnClickListener {
            duracionDescanso()
        }

        // Al pulsar sobre "Comenzar" abre la activity Entrenamiento pasándole un intent con la duración del entrenamiento, la duración de cada ejercicio y la duración del descanso (que ha seleccionado el usuario)
        binding.btComenzar.setOnClickListener {

            if (duracionTotalSeleccionada == 0 || duracionEjerciciosSeleccionada == 0 || duracionDescansoSeleccionada == 0) {
                Toast.makeText(this, "Debe seleccionar las 3 duraciones antes de continuar", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Entrenamiento::class.java)
                intent.putExtra("duracionTotal", duracionTotalSeleccionada)
                intent.putExtra("duracionEjercicios", duracionEjerciciosSeleccionada)
                intent.putExtra("duracionDescanso", duracionDescansoSeleccionada)
                startActivity(intent)
            }

        }
    }

    private fun duracionTotal() {
        // Crea un NumberPicker para que el usuario pueda elegir la duración total del entrenamiento
        val duracionTotal = NumberPicker(this)
        duracionTotal.minValue = 1
        duracionTotal.maxValue = 60

        // Crea el AlertDialog donde el usuario podrá elegir la duración total del entrenamiento
        val builderTotal = AlertDialog.Builder(this)
        builderTotal.setTitle("Selecciona la duración total del entrenamiento (minutos)")
        builderTotal.setView(duracionTotal)
        builderTotal.setPositiveButton("Aceptar") { _, _ ->
            // Obtiene el valor seleccionado por el usuario
            val durTotalSeleccionada = duracionTotal.value
            this.duracionTotalSeleccionada = durTotalSeleccionada
            // Actualiza el TextView con la duración seleccionada
            binding.tvElegirDuracionTotal.text = "$durTotalSeleccionada minutos"
        }
        // No hace nada si el usuario cancela la selección
        builderTotal.setNegativeButton("Cancelar") { _, _ ->
        }
        builderTotal.show()
    }

    private fun duracionEjercicios() {

        // Crea un NumberPicker para que el usuario elija los segundos que durará cada ejercicio
        val duracionEjercicios = NumberPicker(this)
        duracionEjercicios.minValue = 10
        duracionEjercicios.maxValue = 50

        // Abre un AlertDialog donde el usuario podrá elegir la duración de cada ejercicio
        val builderEjercicios = AlertDialog.Builder(this)
        builderEjercicios.setTitle("Selecciona la duración de cada ejercicio (segundos)")
        builderEjercicios.setView(duracionEjercicios)
        builderEjercicios.setPositiveButton("Aceptar") { _, _ ->
            // Obtiene el valor seleccionado por el usuario
            val durEjerciciosSeleccionada = duracionEjercicios.value
            this.duracionEjerciciosSeleccionada = durEjerciciosSeleccionada
            // Actualiza el TextView con la duración seleccionada
            binding.tvElegirDuracionEjercicios.text = "$durEjerciciosSeleccionada segundos"
        }
        // No hace nada si el usuario cancela la selección
        builderEjercicios.setNegativeButton("Cancelar") { _, _ ->

        }
        builderEjercicios.show()
    }

    private fun duracionDescanso() {

        // Crea un NumberPicker para que el usuario elija los segundos que durará cada descanso
        val duracionDescanso = NumberPicker(this)
        duracionDescanso.minValue = 10
        duracionDescanso.maxValue = 50

        //Abre el AlertDialog donde el usuario elegirá la duración de los descansos
        val builderDescanso = AlertDialog.Builder(this)
        builderDescanso.setTitle("Selecciona la duración de los descansos entre ejercicios (segundos)")
        builderDescanso.setView(duracionDescanso)
        builderDescanso.setPositiveButton("Aceptar") { _, _ ->
            // Obtiene el valor seleccionado por el usuario
            val durDescansoSeleccionada = duracionDescanso.value
            this.duracionDescansoSeleccionada = durDescansoSeleccionada
            // Actualiza el TextView con la duración seleccionada
            binding.tvElegirDuracionDescanso.text = "$durDescansoSeleccionada segundos"
        }
        // No hace nada si el usuario cancela la selección
        builderDescanso.setNegativeButton("Cancelar") { _, _ ->
        }
        builderDescanso.show()
    }


}