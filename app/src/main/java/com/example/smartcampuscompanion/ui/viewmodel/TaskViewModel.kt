package com.example.smartcampuscompanion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.local.Task
import com.example.smartcampuscompanion.data.repository.AuthRepository
import com.example.smartcampuscompanion.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedDateFilter = MutableStateFlow<Long?>(null)
    val selectedDateFilter: StateFlow<Long?> = _selectedDateFilter.asStateFlow()

    // Start empty, load explicitly when screen opens
    private val _rawTasks = MutableStateFlow<List<Task>>(emptyList())
    private val rawTasks: StateFlow<List<Task>> = _rawTasks.asStateFlow()

    val filteredTasks: StateFlow<List<Task>> = combine(
        rawTasks,
        _selectedDateFilter
    ) { tasks, filterDate ->
        if (filterDate == null) {
            tasks
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = filterDate
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            val endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1
            tasks.filter { it.dueDate in startOfDay..endOfDay }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadTasksForCurrentUser() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _rawTasks.value = emptyList()
            _error.value = "Not authenticated"
            return
        }
        viewModelScope.launch {
            taskRepository.getTasksForUser(uid).collect { tasks ->
                _rawTasks.value = tasks
            }
        }
    }

    fun setDateFilter(timestamp: Long) {
        _selectedDateFilter.value = timestamp
    }

    fun clearDateFilter() {
        _selectedDateFilter.value = null
    }

    fun addTask(title: String, description: String, dueDate: Long) {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _error.value = "Not authenticated"
            return
        }
        if (title.isBlank()) {
            _error.value = "Title cannot be empty"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val task = Task(
                userId = uid,
                title = title,
                description = description,
                dueDate = dueDate
            )
            taskRepository.addTask(task)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message ?: "Failed to add task" }
            _isLoading.value = false
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            taskRepository.updateTask(task)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message ?: "Failed to update task" }
            _isLoading.value = false
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            taskRepository.deleteTask(task.documentId)
                .onFailure { _error.value = it.message ?: "Failed to delete task" }
            _isLoading.value = false
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() {
        _error.value = null
    }
}