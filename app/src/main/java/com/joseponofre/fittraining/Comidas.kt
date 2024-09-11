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
class Comidas : Navigation() {

    private lateinit var binding: ActivityComidasBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var desayunosAdapter: ComidasAdapter
    private lateinit var comidasAdapter: ComidasAdapter
    private lateinit var cenasAdapter: ComidasAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_comidas
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityComidasBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root) // Agrega el layout de la actividad al FrameLayout

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_comidas
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId) // Llama al método en la clase base para manejar la navegación
        }


        // Configuración del primer RecyclerView (Desayunos)
        // Indica que el RecyclerView tiene un tamaño fijo (lo que optimiza su rendimiento)
        binding.rvDesayunos.setHasFixedSize(true)
        // Configura el Administrador de Diseño (LayoutManager) de manera LINEAL y en HORIZONTAL
        binding.rvDesayunos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Configuración del segundo RecyclerView (Comidas)
        binding.rvComidas.setHasFixedSize(true)
        binding.rvComidas.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Configuración del tercer RecyclerView (Cenas)
        binding.rvCenas.setHasFixedSize(true)
        binding.rvCenas.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Crea una instancia de la base de datos para acceder a ella y obtener la lista de comidas
        val instanciaComidas = FITTrainingSQLite(this)
        val listaCompleta: ArrayList<ComidasDataClass> =
            ArrayList(instanciaComidas.getListaComidas())

        // Ordena la lista por tipo de comidas (desayunos, comidas o cenas)
        listaCompleta.sortBy { it.tipoComida }

        // Filtra la listaCompleta de comidas por tipo de comidas
        val desayunosList =
            listaCompleta.filter { it.tipoComida == ComidasDataClass.TipoComida.DESAYUNO }
        val comidasList =
            listaCompleta.filter { it.tipoComida == ComidasDataClass.TipoComida.COMIDA }
        val cenasList = listaCompleta.filter { it.tipoComida == ComidasDataClass.TipoComida.CENA }

        // Genera los adapter que cargará en cada uno de los recyclerviews basado en el tipo de comida (desayuno, comida, cena).
        // Adapter Desayunos
        desayunosAdapter = ComidasAdapter(
            ComidasDataClass.TipoComida.DESAYUNO,
            desayunosList as ArrayList<ComidasDataClass>
        )
        binding.rvDesayunos.adapter = desayunosAdapter

        // Adapter Comidas
        comidasAdapter = ComidasAdapter(
            ComidasDataClass.TipoComida.COMIDA,
            comidasList as ArrayList<ComidasDataClass>
        )
        binding.rvComidas.adapter = comidasAdapter

        // Adapter Cenas
        cenasAdapter = ComidasAdapter(
            ComidasDataClass.TipoComida.CENA,
            cenasList as ArrayList<ComidasDataClass>
        )
        binding.rvCenas.adapter = cenasAdapter

        // Al hacer click cada uno de los elementos del RecyclerView (sobre cada uno de los desayunos), abrirá la activity Ingredientes
        desayunosAdapter.onItemClick = {
            val intent = Intent(this, Ingredientes::class.java)
            intent.putExtra("comida", it)
            startActivity(intent)
        }
        // Al hacer click cada uno de los elementos del RecyclerView (sobre cada uno de las comidas), abrirá la activity Ingredientes
        comidasAdapter.onItemClick = {
            val intent = Intent(this, Ingredientes::class.java)
            intent.putExtra("comida", it)
            startActivity(intent)
        }
        // Al hacer click cada uno de los elementos del RecyclerView (sobre cada uno de las cenas), abrirá la activity Ingredientes
        cenasAdapter.onItemClick = {
            val intent = Intent(this, Ingredientes::class.java)
            intent.putExtra("comida", it)
            startActivity(intent)
        }
    }


}