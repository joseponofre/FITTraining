package com.joseponofre.fittraining

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joseponofre.fittraining.databinding.ItemUsuariosBinding

class UsuariosAdapter(private val usuariosList: ArrayList<UsuariosDataClass>) :
    RecyclerView.Adapter<UsuariosAdapter.ImageViewHolder>() {

    var onItemClick: ((UsuariosDataClass) -> Unit)? = null

    // ViewHolder con ViewBinding
    class ImageViewHolder(val binding: ItemUsuariosBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemUsuariosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val usuario = usuariosList[position]

        holder.binding.tvNombreUsuario.text = usuario.nombreUsuario
        Glide.with(holder.itemView.context)
            .load(R.drawable.imagen_usuario)
            .into(holder.binding.ivUsuario)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(usuario)
        }
    }

    override fun getItemCount(): Int = usuariosList.size
}
