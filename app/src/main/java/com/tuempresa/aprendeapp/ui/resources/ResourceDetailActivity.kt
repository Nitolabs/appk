@file:Suppress("DEPRECATION")

package com.tuempresa.aprendeapp.ui.resources

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.data.model.Recurso
import com.tuempresa.aprendeapp.databinding.ActivityResourceDetailBinding

class ResourceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResourceDetailBinding
    private lateinit var recurso: Recurso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recurso = intent.getParcelableExtra("recurso") ?: return

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvTitulo.text = recurso.titulo
            tvDescripcion.text = recurso.descripcion
            tvTipo.text = recurso.tipo
            tvEnlace.text = recurso.enlace

            Glide.with(this@ResourceDetailActivity)
                .load(recurso.imagen)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivRecurso)

            btnOpenLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, recurso.enlace.toUri())
                startActivity(intent)
            }
        }
    }
}