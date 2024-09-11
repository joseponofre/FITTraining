package com.joseponofre.fittraining

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseponofre.fittraining.databinding.LayoutPrincipalBinding


// Clase Navigation que se utiliza para navegar entre las activitys

abstract class Navigation : AppCompatActivity() {

    // Método para obtener el ID del layout que se está mostrando
    abstract fun getLayoutId(): Int

    // Este método maneja la navegación entre las diferentes actividades
    // Ahora devuelve un Boolean para indicar si la navegación se ha manejado correctamente
    open fun onNavigate(actionId: Int): Boolean {

        val intent = when (actionId) {
            R.id.nav_comidas -> Intent(this, Comidas::class.java)
            R.id.nav_historial -> Intent(this, Historial::class.java)
            R.id.nav_entrenar -> Intent(this, Entrenar::class.java)
            R.id.nav_ejercicios -> Intent(this, Ejercicios::class.java)
            R.id.nav_perfil -> Intent(this, Perfil::class.java)
            else -> return false
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout principal que contiene el FrameLayout para las actividades secundarias
        val mainBinding = LayoutPrincipalBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        // Inicializa el BottomNavigationView
        val bottomNavigationView: BottomNavigationView = mainBinding.bottomNavigation
        // Configura el listener para el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavigate(menuItem.itemId)
        }
    }
}
