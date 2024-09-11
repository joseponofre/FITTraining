package com.joseponofre.fittraining

import android.os.Parcel
import android.os.Parcelable


// DATA CLASS donde se declaran las propiedades del ejercicio (imagen, nombre del ejercicio e instrucciones)

data class EjerciciosDataClass(
    val imagenEjercicio: Int,
    val nombreEjercicio: String,
    val instruccionesEjercicio: String,
    val videoEjercicio: Int
) : Parcelable {
    //  Constructor que permite pasar un objeto parcel (en este caso un objeto "ejercicio") entre una activity y otra (en este caso entre la activity Ejercicios y la activity Instrucciones)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
    )
    // función para escribir los datos del objeto "ejercicio" en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imagenEjercicio)
        parcel.writeString(nombreEjercicio)
        parcel.writeString(instruccionesEjercicio)
        parcel.writeInt(videoEjercicio)
    }
    // Método que se utiliza para describir el contenido dle objeto "Parcelable", devuelve 0 porque no hay objetos especiales dentro del objeto "ejercicio"
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EjerciciosDataClass> {
        // Crea un nuevo objeto "ejercicio" desde un Parcel
        override fun createFromParcel(parcel: Parcel): EjerciciosDataClass {
            return EjerciciosDataClass(parcel)
        }
        // Crea un nuevo array de ejercicios del tamaño especificado
        override fun newArray(size: Int): Array<EjerciciosDataClass?> {
            return arrayOfNulls(size)
        }
    }
}
