package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joseponofre.fittraining.databinding.ActivityNuevoUsuarioBinding

class NuevoUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityNuevoUsuarioBinding

    private lateinit var db: FITTrainingSQLite
    private lateinit var preference: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FITTrainingSQLite(this)
        preference = PreferenceManager(this)

        // al pulsar sobre la flecha de "atrás", abrirá la activity anterior
        binding.ivAtras.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Al pulsar sobre el botón "Crear usuario"...
        binding.btCrearUsuario.setOnClickListener {
            // Obtiene el nombre de usuario y las contraseñas ingresadas
            val nombreUsuario = binding.etNombre.text.toString()
            val contrasenya = binding.etContrasenya.text.toString()
            val contrasenya2 = binding.etContrasenya2.text.toString()

            // Comprueba que los campos nombreUsuario y contrasenya no estén vacíos.
            if (!comprobarVacios(nombreUsuario, contrasenya)) {
                Toast.makeText(this, "Todos los campos son obligatorios, por favor complete el campo de nombre y los dos campos con la contraseña repetida.", Toast.LENGTH_SHORT).show()
            } // Verifica si el nombre de usuario ya existe en la base de datos
            else {
                if (nombreUsuarioExiste(nombreUsuario)) {
                    Toast.makeText(
                        this,
                        "El nombre de usuario ya existe, por favor elija otro nombre de usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Verifica si las contraseñas coinciden
                    if (contrasenya != contrasenya2) {
                        Toast.makeText(this, "Las contraseñas no coinciden, por favor vuelva a introducir las contraseñas", Toast.LENGTH_SHORT).show()
                    } else {
                        crearNuevoUsuario()
                    }
                }
            }
        }
    }
    // Función que comprueba que los campos nombreUsuario y contrasenya no estén vacíos (devuelve true si no están vacíos).
    private fun comprobarVacios(nombreUsuario: String, contrasenya: String): Boolean {
        return nombreUsuario.isNotEmpty() && contrasenya.isNotEmpty()
    }

    // Método para verificar si el nombre de usuario ya existe en la base de datos
    private fun nombreUsuarioExiste(nombreUsuario: String): Boolean {
        val listaUsuarios = db.getListaUsuarios()
        for (usuario in listaUsuarios) {
            if (usuario.nombreUsuario.equals(nombreUsuario, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    // Método para crear el nuevo usuario en la base de datos
    private fun crearNuevoUsuario() {
        val nombreUsuario = binding.etNombre.text.toString()
        val imagenUsuario = "imagen_usuario"
        val contrasenya = binding.etContrasenya.text.toString()
        val nuevoUsuario =
            UsuariosDataClass(nombreUsuario, imagenUsuario, contrasenya)
        val idUsuario = db.insertarNuevoUsuario(nuevoUsuario)
        Toast.makeText(this, "Nuevo usuario creado correctamente", Toast.LENGTH_SHORT).show()

        preference.guardarUserId(idUsuario.toInt())

        // Abrir la MainActivity
        abrirAplicacion()
    }

    // Método para abrir la aplicación en la MainActivity
    private fun abrirAplicacion() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
