package com.joseponofre.fittraining

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.joseponofre.fittraining.databinding.ActivityIngredientesBinding

// Clase Ingredientes donde se muestran los ingredientes y valores nutricionales de cada plato.

class Ingredientes : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibe un intent de la activity anterior (Comidas) para mostrar la imagen del plato, su nombre, tipo de comida, ingredientes, total de calorías, carbohidratos, proteína y grasas.

        val comida: ComidasDataClass? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("comida", ComidasDataClass::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("comida")
        }
        if (comida != null) {

            // Carga la imagen del plato con Glide
            Glide.with(this)
                .load(comida.imagenComida)
                .into(binding.imgComida)
            // Muestra el resto de datos del plato (nombre, tipo comida, ingredientes, calorías carbohidratos, proteína y grasas.
            binding.tvNombrePlato.text = comida.nombreComida
            binding.tvTipoComida.text = comida.tipoComida.toString()
            binding.tvIngredientes.text = comida.ingredientes
            binding.tvKcal.text = comida.calorias.toString() + " @string"
            binding.tvCarbs.text = comida.carbohidratos.toString() + " gr Carbohidratos"
            binding.tvProte.text = comida.proteina.toString() + " gr Proteína"
            binding.tvGrasas.text = comida.grasas.toString() + " gr Grasas"

        }
        // Pulsanso sobre la flecha de atrás, vuelve a la activity anterior Comidas
        binding.ivAtrasIngr.setOnClickListener {
            startActivity(Intent(this, Comidas::class.java))
        }
    }
}