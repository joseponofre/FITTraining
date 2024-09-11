package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.ActivityEjerciciosBinding
import com.joseponofre.fittraining.databinding.ActivityHistorialBinding
import com.joseponofre.fittraining.databinding.LayoutPrincipalBinding

// Clase Historial que extiende de Navigation. En la clase Historial se muestra el historial de entrenamientos de un determinado usuario (el que tiene abierta la sesión).
class Historial : Navigation() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var entrenamientosAdapter: EntrenamientosAdapter
    private lateinit var preferenceManager: PreferenceManager

    override fun getLayoutId(): Int {
        return R.layout.activity_historial
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root) // Agrega el layout de la actividad al FrameLayout

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_historial
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId) // Llama al método en la clase base para manejar la navegación
        }

        // configuración del RecyclerView
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
        // obtiene el id de usuario que tiene la sesión abierta a través de PreferenceManager (que extiende de SharedPreferences).
        preferenceManager = PreferenceManager(this)
        val userId = preferenceManager.obtenerUserId()

        // Obtiene la lista de entrenamientos guardados en la base de datos por id de usuario
        val instanciaEntrenamientos = FITTrainingSQLite(this)
        val entrenamientosList: ArrayList<EntrenamientosDataClass> =
            ArrayList(instanciaEntrenamientos.getEntrenamientosPorUsuario(userId))

        // Ordena la lista de entrenamientos por fecha y hora
        val entrenamientosOrdenados =
            entrenamientosList.sortedWith(compareByDescending<EntrenamientosDataClass> { it.diaEntrenamiento }.thenByDescending { it.horaEntrenamiento })

        // Configura el adaptador con la lista de entrenamientos ordenados por fecha y hora y los muestra en el RecyclerView
        entrenamientosAdapter = EntrenamientosAdapter(ArrayList(entrenamientosOrdenados))
        binding.rvHistorial.adapter = entrenamientosAdapter
    }


}