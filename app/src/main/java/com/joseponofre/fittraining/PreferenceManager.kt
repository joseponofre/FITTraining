package com.joseponofre.fittraining

import android.content.Context
import android.content.SharedPreferences

// Clase PreferenceManager que utiliza las SharedPreferences de Android Studio y se utiliza para abrir y cerrar la sesión de un usuario
class PreferenceManager (context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "FITTrainingPreferences",
        Context.MODE_PRIVATE
    )
    // guarda el id de usuario
    fun guardarUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }
    // obtiene el id de usuario (que tiene la sesión abierta)
    fun obtenerUserId(): Int {
        return sharedPreferences.getInt("userId", -1)

    }
    // cierra la sesión
    fun limpiarUserId() {
        sharedPreferences.edit().remove("userId").apply()
    }
}