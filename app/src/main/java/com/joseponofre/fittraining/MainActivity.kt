package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.joseponofre.fittraining.databinding.ActivityMainActivityBinding

// La MainActivity es la primera activity que se abre tras la Splashscreen. Muestra los usuarios que ya están creados en la bbdd y/o la opción de pulsar sobre "Nuevo usuario"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainActivityBinding

    private lateinit var usuariosAdapter: UsuariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del RecyclerView que mostrará los usuarios

        binding.rvUsuarios.setHasFixedSize(true)
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Crea una instancia de la bbdd y obtiene la lista de usuarios
        val instanciaUsuarios = FITTrainingSQLite(this)
        val ejerciciosList: ArrayList<UsuariosDataClass> =
            ArrayList(instanciaUsuarios.getListaUsuarios())

        val usuariosOrdenados = ejerciciosList.sortedBy { it.nombreUsuario }
        // Muestra la lista de usuarios en el RecyclerView ordenados por nombre de usuario (alfabéticamente).
        usuariosAdapter = UsuariosAdapter(ArrayList(usuariosOrdenados))
        binding.rvUsuarios.adapter = usuariosAdapter

        usuariosAdapter.onItemClick = {
            // al hacer click sobre un usuario, abrirá la siguiente activity IniciarSesion
            val intent = Intent(this, IniciarSesion::class.java)
            intent.putExtra("usuario", it)
            startActivity(intent)

        }
        // al hacer click sobre el botón "Nuevo usuario", abrirá la activity NuevoUsuario
        binding.btNuevoUsuario.setOnClickListener {
            startActivity(Intent(this, NuevoUsuario::class.java))
        }
    }
}