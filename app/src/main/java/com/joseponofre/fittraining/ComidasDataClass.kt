package com.joseponofre.fittraining


import android.os.Build
import android.os.Parcel
import android.os.Parcelable


// DATA CLASS donde se declaran las propiedades de las comidas (imagen, nombre, tipo comida, total de calorías, cantidad de proteína, cantidad de carbohidratos y cantidad de grasas.)

data class ComidasDataClass(
    val imagenComida: Int,
    val nombreComida: String,
    val tipoComida: TipoComida,
    val ingredientes: String,
    val calorias: Int,
    val carbohidratos: Int,
    val proteina: Int,
    val grasas: Int
) : Parcelable {
    // Constructor que permite pasar un objeto parcel (en este caso un objeto "comida") entre una activity y otra (en este caso entre la activity Comidas y la activity Ingredientes)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(TipoComida::class.java.classLoader, TipoComida::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(TipoComida::class.java.classLoader)!!
        },
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )
    // función para escribir los datos del objeto "comida" en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imagenComida)
        parcel.writeString(nombreComida)
        parcel.writeParcelable(tipoComida, flags)
        parcel.writeString(ingredientes)
        parcel.writeInt(calorias)
        parcel.writeInt(carbohidratos)
        parcel.writeInt(proteina)
        parcel.writeInt(grasas)
    }
    // Método que se utiliza para describir el contenido dle objeto "Parcelable", devuelve 0 porque no hay objetos especiales dentro del objeto "comidas"
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ComidasDataClass> {
        // Crea un nuevo objeto "comida" desde un Parcel
        override fun createFromParcel(parcel: Parcel): ComidasDataClass {
            return ComidasDataClass(parcel)
        }
        // Crea un nuevo array de "comidas" del tamaño especificado
        override fun newArray(size: Int): Array<ComidasDataClass?> {
            return arrayOfNulls(size)
        }
    }
    // Enumeración que representa el TipoComida (desayuno, comida, cena)
    enum class TipoComida : Parcelable {
        DESAYUNO, COMIDA, CENA;

    // Escribe el valor ordinal de la constante TipoComida en el objeto Parcel
        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(ordinal)
        }
        // Método que se utiliza para describir el contenido dle objeto "Parcelable", devuelve 0 porque no hay objetos especiales dentro del objeto "TipoComida"
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<TipoComida> {
            // Crea un objeto TipoComida a partir de un Parcel
            override fun createFromParcel(parcel: Parcel): TipoComida {
                return values()[parcel.readInt()]
            }
            // Crea un nuevo array de "TipoComida" del tamaño especificado
            override fun newArray(size: Int): Array<TipoComida?> {
                return arrayOfNulls(size)
            }
        }
    }
}