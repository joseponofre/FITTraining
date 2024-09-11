package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.ActivityComidasBinding
import com.joseponofre.fittraining.databinding.ActivityEjerciciosBinding
import com.joseponofre.fittraining.databinding.LayoutPrincipalBinding

// Extiende de la clase Navigation
class Ejercicios : Navigation() {

    private lateinit var binding: ActivityEjerciciosBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var ejerciciosAdapter: EjerciciosAdapter


    override fun getLayoutId(): Int {
        return R.layout.activity_ejercicios
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityEjerciciosBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root) // Agrega el layout de la actividad al FrameLayout

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_ejercicios
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId) // Llama al método en la clase base para manejar la navegación
        }


        // Configuración del RecyclerView
        // Indica que el RecyclerView tiene un tamaño fijo (lo que optimiza su rendimiento)
        binding.rvEjercicios.setHasFixedSize(true)
        // Configura el Administrador de Diseño (LayoutManager) de manera lineal (en vertical por defecto)
        binding.rvEjercicios.layoutManager = LinearLayoutManager(this)

        // Obtiene la lista de ejercicios desde la base de datos
        val instanciaEjercicios = FITTrainingSQLite(this)
        val ejerciciosList: ArrayList<EjerciciosDataClass> =
            ArrayList(instanciaEjercicios.getListaEjercicios())

        // Ordena la lista de ejercicios en orden alfabético
        val ejerciciosOrdenados = ejerciciosList.sortedBy { it.nombreEjercicio }

        // Configura el adaptador con la lista de ejercicios ordenados alfabéticamente
        ejerciciosAdapter = EjerciciosAdapter(ArrayList(ejerciciosOrdenados))
        binding.rvEjercicios.adapter = ejerciciosAdapter

        // Al hacer click sobre cada uno de los elementos del RecyclerView (sobre cada uno de los ejercicios), abre la activity Instrucciones
        ejerciciosAdapter.onItemClick = {
            val intent = Intent(this, Instrucciones::class.java)
            intent.putExtra("ejercicio", it)
            startActivity(intent)
        }
    }


}