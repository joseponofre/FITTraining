package com.joseponofre.fittraining


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joseponofre.fittraining.databinding.ItemComidasBinding

// Clase Adapter utilizada para manejar los RecyclerView de la activity Comidas
class ComidasAdapter(
    private val tipoComida: ComidasDataClass.TipoComida,
    private val comidaslist: ArrayList<ComidasDataClass>
) :
    RecyclerView.Adapter<ComidasAdapter.ImageViewHolder>() {

    var onItemClick: ((ComidasDataClass) -> Unit)? = null

    // Clase que proporciona una forma de acceder y manipular las vistas contenidas dentro de un elemento de la lista de un Recyclerview
    // Cada elemento del RecyclerView tendrá una imagen (la imagen de la comida) y un nombre (el nombre de la comida)
    class ImageViewHolder(val binding: ItemComidasBinding) : RecyclerView.ViewHolder(binding.root)


    // Método que crea e inicializa un ViewHolder y su View asociada para mostrar la información deseada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemComidasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }
    // Método que recupera la información de la base de datos para ser mostrada en el ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val comida = comidaslist[position]

        // Recupera la imagen del plato y el nombre del plato (imagenComida y nombreComida)
        when (tipoComida) {
            ComidasDataClass.TipoComida.DESAYUNO -> {
                holder.binding.ivComida.setImageResource(comida.imagenComida)
                holder.binding.tvNombreComida.text = comida.nombreComida
            }

            ComidasDataClass.TipoComida.COMIDA -> {
                holder.binding.ivComida.setImageResource(comida.imagenComida)
                holder.binding.tvNombreComida.text = comida.nombreComida
            }

            ComidasDataClass.TipoComida.CENA -> {
                holder.binding.ivComida.setImageResource(comida.imagenComida)
                holder.binding.tvNombreComida.text = comida.nombreComida
            }
        }
        // Configura el onClickListener
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(comida)
        }
    }
    // Método para obtener el número total de comidas (platos)
    override fun getItemCount(): Int = comidaslist.size

}