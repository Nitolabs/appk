package com.tuempresa.aprendeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.aprendeapp.data.model.Recurso
import com.tuempresa.aprendeapp.data.repository.RecursoRepository
import kotlinx.coroutines.launch

class ResourceViewModel : ViewModel() {

    private val repository = RecursoRepository()

    private val _recursos = MutableLiveData<List<Recurso>>()
    val recursos: LiveData<List<Recurso>> = _recursos

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    fun loadRecursos() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getRecursos()
            result.onSuccess { recursos ->
                _recursos.value = recursos
                _loading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _loading.value = false
            }
        }
    }

    fun searchRecursos(query: String) {
        val allRecursos = _recursos.value ?: return

        if (query.isEmpty()) {
            return
        }

        val filtered = allRecursos.filter { recurso ->
            recurso.id?.contains(query, ignoreCase = true) == true ||
                    recurso.titulo.contains(query, ignoreCase = true) ||
                    recurso.tipo.contains(query, ignoreCase = true)
        }

        _recursos.value = filtered
    }

    fun sortRecursos(sortType: SortType) {
        val currentRecursos = _recursos.value ?: return

        val sorted = when (sortType) {
            SortType.TITLE_ASC -> currentRecursos.sortedBy { it.titulo }
            SortType.TITLE_DESC -> currentRecursos.sortedByDescending { it.titulo }
            SortType.TYPE -> currentRecursos.sortedBy { it.tipo }
        }

        _recursos.value = sorted
    }

    fun filterByType(type: String) {
        if (type == "Todos") {
            loadRecursos()
            return
        }

        viewModelScope.launch {
            val result = repository.getRecursos()
            result.onSuccess { recursos ->
                _recursos.value = recursos.filter { it.tipo.equals(type, ignoreCase = true) }
            }
        }
    }

    fun createRecurso(recurso: Recurso) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.createRecurso(recurso)
            result.onSuccess {
                _operationSuccess.value = true
                _loading.value = false
                loadRecursos()
            }.onFailure { exception ->
                _error.value = exception.message
                _loading.value = false
                _operationSuccess.value = false
            }
        }
    }

    fun updateRecurso(id: String, recurso: Recurso) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateRecurso(id, recurso)
            result.onSuccess {
                _operationSuccess.value = true
                _loading.value = false
                loadRecursos()
            }.onFailure { exception ->
                _error.value = exception.message
                _loading.value = false
                _operationSuccess.value = false
            }
        }
    }

    fun deleteRecurso(id: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.deleteRecurso(id)
            result.onSuccess {
                _operationSuccess.value = true
                _loading.value = false
                loadRecursos()
            }.onFailure { exception ->
                _error.value = exception.message
                _loading.value = false
                _operationSuccess.value = false
            }
        }
    }

    enum class SortType {
        TITLE_ASC, TITLE_DESC, TYPE
    }
}