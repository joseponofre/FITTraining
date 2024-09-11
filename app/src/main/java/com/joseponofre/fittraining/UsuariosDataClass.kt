package com.joseponofre.fittraining

import android.os.Parcel
import android.os.Parcelable

// DataClass donde se declaran las propiedades de los usuarios (nombre, imagen y contraseña)
data class UsuariosDataClass(

    val nombreUsuario: String,
    val imagenUsuario: String,
    val contrasenya: String
) : Parcelable {
    //  Constructor que permite pasar un objeto parcel (en este caso un objeto "usuario") entre una activity y otra (en este caso entre la MainActivity y la activity IniciarSesion)//
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    )
    // función para escribir los datos del objeto "usuario" en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(nombreUsuario)
        parcel.writeString(imagenUsuario)
        parcel.writeString(contrasenya)

    }
    // Método que se utiliza para describir el contenido dle objeto "Parcelable", devuelve 0 porque no hay objetos especiales dentro del objeto "usuario"
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UsuariosDataClass> {
        // crea un nuevo objeto "usuario" desde un parcel
        override fun createFromParcel(parcel: Parcel): UsuariosDataClass {
            return UsuariosDataClass(parcel)
        }
        // crea un nuevo arraz de usuarios del tamaño especificado
        override fun newArray(size: Int): Array<UsuariosDataClass?> {
            return arrayOfNulls(size)
        }
    }

}
