package com.tuempresa.aprendeapp.ui.resources

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.data.model.Recurso
import com.tuempresa.aprendeapp.databinding.FragmentResourcesBinding
import com.tuempresa.aprendeapp.ui.resources.adapter.ResourceAdapter
import com.tuempresa.aprendeapp.utils.showToast
import com.tuempresa.aprendeapp.viewmodel.ResourceViewModel

class ResourcesFragment : Fragment() {
    private var _binding: FragmentResourcesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResourceViewModel by viewModels()
    private lateinit var adapter: ResourceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        observeViewModel()

        viewModel.loadRecursos()
    }

    private fun setupRecyclerView() {
        adapter = ResourceAdapter(
            onItemClick = { recurso -> openResourceDetail(recurso) },
            onEditClick = { recurso -> editRecurso(recurso) },
            onDeleteClick = { recurso -> showDeleteConfirmation(recurso) }
        )

        binding.rvRecursos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ResourcesFragment.adapter
        }
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isEmpty()) {
                viewModel.loadRecursos()
            } else {
                viewModel.searchRecursos(query)
            }
        }

        binding.btnSort.setOnClickListener { showSortDialog() }
        binding.btnFilter.setOnClickListener { showFilterDialog() }
    }

    private fun observeViewModel() {
        viewModel.recursos.observe(viewLifecycleOwner) { recursos ->
            adapter.submitList(recursos)
            binding.tvNoRecursos.visibility = if (recursos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { requireContext().showToast(it) }
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) requireContext().showToast("Operación exitosa")
        }
    }

    private fun showSortDialog() {
        val options = arrayOf(
            getString(R.string.sort_title_asc),
            getString(R.string.sort_title_desc),
            getString(R.string.sort_type)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sort_by))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.sortRecursos(ResourceViewModel.SortType.TITLE_ASC)
                    1 -> viewModel.sortRecursos(ResourceViewModel.SortType.TITLE_DESC)
                    2 -> viewModel.sortRecursos(ResourceViewModel.SortType.TYPE)
                }
            }
            .show()
    }

    private fun showFilterDialog() {
        val tipos = arrayOf("Todos", "Libro", "Video", "Tutorial", "Artículo", "Podcast")

        AlertDialog.Builder(requireContext())
            .setTitle("Filtrar por tipo")
            .setItems(tipos) { _, which ->
                viewModel.filterByType(tipos[which])
            }
            .show()
    }

    private fun openResourceDetail(recurso: Recurso) {
        val intent = Intent(requireContext(), ResourceDetailActivity::class.java).apply {
            putExtra("recurso", recurso)
        }
        startActivity(intent)
    }

    private fun editRecurso(recurso: Recurso) {
        val intent = Intent(requireContext(), AddEditResourceActivity::class.java).apply {
            putExtra("recurso", recurso)
            putExtra("isEdit", true)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(recurso: Recurso) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.delete_button)) { _, _ ->
                recurso.id?.let { viewModel.deleteRecurso(it) }
            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRecursos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}