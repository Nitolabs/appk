package com.tuempresa.aprendeapp.ui.resources

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.data.model.Recurso
import com.tuempresa.aprendeapp.databinding.ActivityAddEditResourceBinding
import com.tuempresa.aprendeapp.utils.showToast
import com.tuempresa.aprendeapp.viewmodel.ResourceViewModel

class AddEditResourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditResourceBinding
    private val viewModel: ResourceViewModel by viewModels()

    private var isEditMode = false
    private var recursoToEdit: Recurso? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkEditMode()
        setupUI()
        observeViewModel()
    }

    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("isEdit", false)
        recursoToEdit = intent.getParcelableExtra("recurso")

        title = if (isEditMode) getString(R.string.edit_resource_title)
        else getString(R.string.add_resource_title)

        if (isEditMode && recursoToEdit != null) {
            binding.apply {
                etTitulo.setText(recursoToEdit?.titulo)
                etDescripcion.setText(recursoToEdit?.descripcion)
                etTipo.setText(recursoToEdit?.tipo)
                etEnlace.setText(recursoToEdit?.enlace)
                etImagen.setText(recursoToEdit?.imagen)
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            btnSave.setOnClickListener {
                if (validateFields()) saveRecurso()
            }
            btnCancel.setOnClickListener { finish() }
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val tipo = etTipo.text.toString().trim()
            val enlace = etEnlace.text.toString().trim()
            val imagen = etImagen.text.toString().trim()

            return when {
                titulo.isEmpty() -> { tilTitulo.error = getString(R.string.error_empty_field); false }
                descripcion.isEmpty() -> { tilDescripcion.error = getString(R.string.error_empty_field); false }
                tipo.isEmpty() -> { tilTipo.error = getString(R.string.error_empty_field); false }
                enlace.isEmpty() -> { tilEnlace.error = getString(R.string.error_empty_field); false }
                imagen.isEmpty() -> { tilImagen.error = getString(R.string.error_empty_field); false }
                else -> {
                    tilTitulo.error = null
                    tilDescripcion.error = null
                    tilTipo.error = null
                    tilEnlace.error = null
                    tilImagen.error = null
                    true
                }
            }
        }
    }

    private fun saveRecurso() {
        binding.apply {
            val recurso = Recurso(
                id = recursoToEdit?.id,
                titulo = etTitulo.text.toString().trim(),
                descripcion = etDescripcion.text.toString().trim(),
                tipo = etTipo.text.toString().trim(),
                enlace = etEnlace.text.toString().trim(),
                imagen = etImagen.text.toString().trim()
            )

            if (isEditMode && recursoToEdit?.id != null) {
                viewModel.updateRecurso(recursoToEdit!!.id!!, recurso)
            } else {
                viewModel.createRecurso(recurso)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            binding.apply {
                btnSave.isEnabled = !isLoading
                btnCancel.isEnabled = !isLoading
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel.operationSuccess.observe(this) { success ->
            if (success) {
                val message = if (isEditMode) getString(R.string.resource_updated)
                else getString(R.string.resource_saved)
                showToast(message)
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { showToast(it) }
        }
    }
}