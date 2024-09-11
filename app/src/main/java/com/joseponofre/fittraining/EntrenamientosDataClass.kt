package com.joseponofre.fittraining

import java.time.LocalTime
import java.util.Date

// DataClass donde se declaran las propiedades del entrenamiento (día, hora, duración, numero de ejercicios y total de calorías)

data class EntrenamientosDataClass(
    val diaEntrenamiento: Date,
    val horaEntrenamiento: LocalTime,
    var duracionEntrenamiento: Int,
    var numEjercicios: Int,
    var totalCalorias: Int,
    var idUsuario: Int
) {

}

