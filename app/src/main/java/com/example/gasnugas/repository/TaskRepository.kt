package com.example.gasnugas.repository

import android.content.Context
import com.example.gasnugas.database.AppDatabase
import com.example.gasnugas.database.TodoTask
import com.example.gasnugas.database.toDomainTask
import com.example.gasnugas.database.toTodoTask
import com.example.gasnugas.ui.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val taskDao = database.todoTaskDao()
    
    fun getTasksByUser(userId: Long): Flow<List<Task>> {
        return taskDao.getTasksByUser(userId).map { todoTasks ->
            todoTasks.map { it.toDomainTask() }
        }
    }
    
    fun getTasksByUserAndStatus(userId: Long, status: String): Flow<List<Task>> {
        return taskDao.getTasksByUserAndStatus(userId, status).map { todoTasks ->
            todoTasks.map { it.toDomainTask() }
        }
    }
    
    suspend fun getTaskById(taskId: Long, userId: Long): Task? {
        return taskDao.getTaskById(taskId, userId)?.toDomainTask()
    }
    
    suspend fun insertTask(task: Task, userId: Long): Long {
        val todoTask = task.toTodoTask(userId)
        return taskDao.insertTask(todoTask)
    }
    
    suspend fun updateTask(task: Task, userId: Long) {
        val todoTask = task.toTodoTask(userId)
        taskDao.updateTask(todoTask)
    }
    
    suspend fun deleteTask(task: Task, userId: Long) {
        val todoTask = task.toTodoTask(userId)
        taskDao.deleteTask(todoTask)
    }
    
    suspend fun deleteTaskById(taskId: Long, userId: Long) {
        taskDao.deleteTaskById(taskId, userId)
    }
    
    suspend fun getTaskCountByUser(userId: Long): Int {
        return taskDao.getTaskCountByUser(userId)
    }
    
    suspend fun getTaskCountByUserAndStatus(userId: Long, status: String): Int {
        return taskDao.getTaskCountByUserAndStatus(userId, status)
    }
} 