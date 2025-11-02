package com.tuempresa.aprendeapp.data.repository

import com.tuempresa.aprendeapp.data.api.RetrofitClient
import com.tuempresa.aprendeapp.data.model.Recurso

class RecursoRepository {

    private val api = RetrofitClient.apiService

    suspend fun getRecursos(): Result<List<Recurso>> {
        return try {
            val response = api.getRecursos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener recursos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecursoById(id: String): Result<Recurso> {
        return try {
            val response = api.getRecursoById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Recurso no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRecurso(recurso: Recurso): Result<Recurso> {
        return try {
            val response = api.createRecurso(recurso)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear recurso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRecurso(id: String, recurso: Recurso): Result<Recurso> {
        return try {
            val response = api.updateRecurso(id, recurso)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar recurso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecurso(id: String): Result<Unit> {
        return try {
            val response = api.deleteRecurso(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar recurso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}