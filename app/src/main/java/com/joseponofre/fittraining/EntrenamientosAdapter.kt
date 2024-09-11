package com.joseponofre.fittraining

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joseponofre.fittraining.databinding.ItemEntrenamientoBinding
import java.text.SimpleDateFormat

// Clase Adapter utilizada para manejar el RecyclerView de la activity Historial, que mostrará los entrenamientos guardados en la bbdd
class EntrenamientosAdapter(private val entrenamientosList: ArrayList<EntrenamientosDataClass>) :
    RecyclerView.Adapter<EntrenamientosAdapter.ImageViewHolder>() {

    // Clase que proporciona una forma de acceder y manipular las vistas contenidas dentro de un elemento de la lista de un Recyclerview
    // Cada elemento del RecyclerView mostrará la fecha y hora de cada entrenamiento guardado, el número de ejercicios realizados, la duración del entrenamiento y las calorías gastadas (kcal).
    class ImageViewHolder(val binding: ItemEntrenamientoBinding) : RecyclerView.ViewHolder(binding.root)


    // Método que crea e inicializa un ViewHolder y su View asociada para mostrar la información deseada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemEntrenamientoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }
    // Método que recupera la información de la base de datos para ser mostrada en el ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val entrenamiento = entrenamientosList[position]

        // Recupera la fecha del entrenamiento y la formatea formato "dd-MM-yyyy"
        val formatoSalida = SimpleDateFormat("dd-MM-yyyy")
        val fechaConvertida = formatoSalida.format(entrenamiento.diaEntrenamiento)
        val context = holder.itemView.context
        val formattedString = context.getString(R.string.day, fechaConvertida.toString())
        holder.binding.tvFecha.text = formattedString
        // Recupera la hora del entrenamiento
        val formattedHora =
            context.getString(R.string.hour, entrenamiento.horaEntrenamiento.toString())
        holder.binding.tvHora.text = formattedHora
        // Recupera el número total de ejercicios realizados
        val formattedNumEjer =
            context.getString(R.string.exercises_, entrenamiento.numEjercicios.toString())
        holder.binding.tvNumEjercicios.text = formattedNumEjer
        // Recupera la duración del entrenamiento
        val formattedDuracion =
            context.getString(R.string.minutes, entrenamiento.duracionEntrenamiento.toString())
        holder.binding.tvDuracion.text = formattedDuracion
        // Recupera el total de calorías (kcal)
        val formattedKcal = context.getString(R.string.kcal, entrenamiento.totalCalorias.toString())
        holder.binding.tvKcal.text = formattedKcal
    }
    // Método para obtener el número total de entrenamientos guardados en la bbdd
    override fun getItemCount(): Int = entrenamientosList.size

}