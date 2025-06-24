package com.example.gasnugas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gasnugas.repository.TaskRepository
import com.example.gasnugas.ui.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskRepository = TaskRepository(application)
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    fun getTasksByUser(userId: Long): Flow<List<Task>> {
        return taskRepository.getTasksByUser(userId)
    }
    
    fun getTasksByUserAndStatus(userId: Long, status: String): Flow<List<Task>> {
        return taskRepository.getTasksByUserAndStatus(userId, status)
    }
    
    fun insertTask(task: Task, userId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                taskRepository.insertTask(task, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun updateTask(task: Task, userId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                taskRepository.updateTask(task, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun deleteTask(task: Task, userId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                taskRepository.deleteTask(task, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun deleteTaskById(taskId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                taskRepository.deleteTaskById(taskId, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun getTaskCountByUser(userId: Long, callback: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = taskRepository.getTaskCountByUser(userId)
                callback(count)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun getTaskCountByUserAndStatus(userId: Long, status: String, callback: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = taskRepository.getTaskCountByUserAndStatus(userId, status)
                callback(count)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) 