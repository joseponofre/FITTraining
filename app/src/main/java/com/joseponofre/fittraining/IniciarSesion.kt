package com.joseponofre.fittraining

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class IniciarSesion : AppCompatActivity() {

    private lateinit var db: FITTrainingSQLite
    private lateinit var preference: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // al pulsar sobre la flecha de "atrás", abrirá la activity anterior
        val atras: ImageView = findViewById(R.id.iv_atras)
        atras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Crea una instancia de la bbdd (FITTrainingSQLte)
        db = FITTrainingSQLite(this)
        // Crea una instancia de PreferenceManager (SharedPreferences)
        preference = PreferenceManager(this)

        // Recibe un intent de la activity anterior (la MainActivity) con el usuario y muestra su nombre

        val usuario: UsuariosDataClass? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("usuario", UsuariosDataClass::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuario")
        }

        val tvNombreUsuario: TextView = findViewById(R.id.tv_nombre_usuario)
        if (usuario != null) {
            tvNombreUsuario.text = usuario.nombreUsuario
        }


        var introducirContrasenya: EditText = findViewById(R.id.et_introducir_contrasenya)

        var iniciarSesion: Button = findViewById(R.id.bt_iniciar_sesion)


        // OnClickListener en el botón iniciarSesion. Revisa si la contraseña y el nombre del usuario introducidos coinciden con los guardados en la bbdd
        iniciarSesion.setOnClickListener{
            val nombreUsuario = tvNombreUsuario.text.toString()
            val contrasenya = introducirContrasenya.text.toString()
            val contrasenyaCorrecta = db.verificarContrasenya(nombreUsuario, contrasenya)
            if (contrasenyaCorrecta) {
                // Si la contraseña es correcta se abrirá la aplicación
                val userId = db.obtenerIdUsuarioPorNombre(nombreUsuario)
                preference.guardarUserId(userId)
                abrirAplicacion()
            } else {
                // Si la contraseña no es correcta, muestra un mensaje de error
                Toast.makeText(this, "La contraseña no es correcta, por favor revise y vuelva a introducir la contraseña", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Método que abre la aplicación en la activity Entrenar
    private fun abrirAplicacion() {
        val intent = Intent(this, Entrenar::class.java)
        startActivity(intent)
    }
}