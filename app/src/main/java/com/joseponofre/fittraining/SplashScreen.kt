package com.joseponofre.fittraining


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// SplashScreen
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        // Handler para manejar un retraso de 2 segundos antes de iniciar la MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
        val db = FITTrainingSQLite(this@SplashScreen)
        // inserta los ejercicios predefinidos en la bbdd (solo lo realiza la primera vez que se instala la aplicación,
        // ya que el método se encarga de manejarlo).
        db.insertarEjerciciosPredefinidos()
        // inserta las comidas predefinidas en la bbdd (solo lo realiza la primera vez que se instala la aplicación,
        // ya que el método se encarga de manejarlo).
        db.insertarComidas()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
        }, 2000) // retraso de 2 segundos
    }
}