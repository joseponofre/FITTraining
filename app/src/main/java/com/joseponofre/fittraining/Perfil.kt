package com.joseponofre.fittraining

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.ActivityComidasBinding
import com.joseponofre.fittraining.databinding.ActivityEntrenarBinding
import com.joseponofre.fittraining.databinding.ActivityPerfilBinding
import com.joseponofre.fittraining.databinding.LayoutPrincipalBinding

// Activity Perfil, desde donde el usuario puede cerrar la sesión o eliminar su cuenta. Extiende de la clase Navigation (para navegar por las distintas activitys).
class Perfil : Navigation() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun getLayoutId(): Int {
        return R.layout.activity_perfil
    }

    private lateinit var db: FITTrainingSQLite
    private lateinit var preference: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout específico para esta actividad
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        val container: FrameLayout = findViewById(R.id.main_container)
        container.addView(binding.root) // Agrega el layout de la actividad al FrameLayout

        // Configura el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_perfil
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId) // Llama al método en la clase base para manejar la navegación
        }

        // Crea una instancia de la bbdd (FITTrainingSQLite) y de PreferenceManager (SharedPreferences)
        db = FITTrainingSQLite(this)
        preference = PreferenceManager(this)


        // obtiene el usuario que tiene abierta la sesión con PreferenceManager
        val userId = preference.obtenerUserId()
        // muestra el nombre de usuario que tiene abierta la sesión en el TextView
        val nombreUsuario = db.obtenerNombreUsuarioPorId(userId)
        binding.tvNombreUsuario.text = nombreUsuario

        // Al hacer click sobr el botón "Cerrar sesión", cerrará la sesión utilizando PreferenceManager (limpiarUserId) y abrirá la activity MainActivity
        binding.btCerrarSesion.setOnClickListener {
            preference.limpiarUserId()
            startActivity(Intent(this, MainActivity::class.java))
        }
        // Al hacer click sobre el botón "Borrar cuenta"...
        binding.btBorrarCuenta.setOnClickListener {
            // abre un AlertDialog donde le pregunta al usuario si quiere efectivamente borrar su cuenta (esta acción no se puede deshacer).
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Borrar la cuenta del usuario")
            builder.setMessage("¿Estás seguro/a de que quieres borrar la cuenta de usuario? ¡¡ ATENCIÓN !! El usuario se borrará y todo el historial de entrenamientos se eliminará completamente, esta acción no se puede deshacer.")
            builder.setPositiveButton("Borrar cuenta") {_, _ ->
                // En caso afirmativo, borra la tabla Entrenamientos por idUsuario
                preference = PreferenceManager(this)
                val usuario = preference.obtenerUserId()
                db = FITTrainingSQLite(this)
                db.eliminarEntrenamientosPorUsuario(usuario)
                // Borrar el Usuario que tiene abierta la sesión de la tabla Usuarios
                db.eliminarUsuario(usuario)

                // Vuelve a la Main Activity
                startActivity(Intent(this, MainActivity::class.java))
            } // en caso de pulsar sobre "Cancelar", cierra el AlertDiaglo y no hace nada más.
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

}