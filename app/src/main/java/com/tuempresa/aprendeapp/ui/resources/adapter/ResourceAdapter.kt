package com.tuempresa.aprendeapp.ui.resources.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.data.model.Recurso
import com.tuempresa.aprendeapp.databinding.ItemRecursoBinding

class ResourceAdapter(
    private val onItemClick: (Recurso) -> Unit,
    private val onEditClick: (Recurso) -> Unit,
    private val onDeleteClick: (Recurso) -> Unit
) : ListAdapter<Recurso, ResourceAdapter.ResourceViewHolder>(RecursoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val binding = ItemRecursoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResourceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResourceViewHolder(
        private val binding: ItemRecursoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recurso: Recurso) {
            binding.apply {
                tvTitulo.text = recurso.titulo
                tvDescripcion.text = recurso.descripcion
                tvTipo.text = recurso.tipo

                Glide.with(root.context)
                    .load(recurso.imagen)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivRecurso)

                root.setOnClickListener { onItemClick(recurso) }
                btnEdit.setOnClickListener { onEditClick(recurso) }
                btnDelete.setOnClickListener { onDeleteClick(recurso) }
            }
        }
    }

    private class RecursoDiffCallback : DiffUtil.ItemCallback<Recurso>() {
        override fun areItemsTheSame(oldItem: Recurso, newItem: Recurso): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recurso, newItem: Recurso): Boolean {
            return oldItem == newItem
        }
    }
}