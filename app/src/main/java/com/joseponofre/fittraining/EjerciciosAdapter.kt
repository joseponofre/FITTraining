package com.joseponofre.fittraining

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joseponofre.fittraining.databinding.ItemEjercicioBinding


// Clase Adapter utilizada para manejar el RecyclerView de la activity Ejercicios
class EjerciciosAdapter(private val ejercicioslist: ArrayList<EjerciciosDataClass>) :
    RecyclerView.Adapter<EjerciciosAdapter.ImageViewHolder>() {

    var onItemClick: ((EjerciciosDataClass) -> Unit)? = null

    // Clase que proporciona una forma de acceder y manipular las vistas contenidas dentro de un elemento de la lista de un Recyclerview
    // Cada elemento del RecyclerView tendrá una imagen (la imagen del ejercicio) y un nombre (el nombre del ejercicio)
    class ImageViewHolder(val binding: ItemEjercicioBinding) : RecyclerView.ViewHolder(binding.root)


    // Método que crea e inicializa un ViewHolder y su View asociada para mostrar la información deseada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            ItemEjercicioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }
    // Método que recupera la información de la base de datos para ser mostrada en el ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val ejercicio = ejercicioslist[position]
        holder.binding.ivEjercicio.setImageResource(ejercicio.imagenEjercicio)
        holder.binding.tvEjercicio.text = ejercicio.nombreEjercicio

        // Configura el onClickListener
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ejercicio)
        }
    }
    // Método para obtener el número total de ejercicios
    override fun getItemCount(): Int = ejercicioslist.size
}