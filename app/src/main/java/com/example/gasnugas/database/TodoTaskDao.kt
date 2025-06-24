package com.example.gasnugas.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoTaskDao {
    @Query("SELECT * FROM todo_tasks WHERE userId = :userId ORDER BY date ASC")
    fun getTasksByUser(userId: Long): Flow<List<TodoTask>>
    
    @Query("SELECT * FROM todo_tasks WHERE userId = :userId AND status = :status ORDER BY date ASC")
    fun getTasksByUserAndStatus(userId: Long, status: String): Flow<List<TodoTask>>
    
    @Query("SELECT * FROM todo_tasks WHERE id = :taskId AND userId = :userId LIMIT 1")
    suspend fun getTaskById(taskId: Long, userId: Long): TodoTask?
    
    @Insert
    suspend fun insertTask(task: TodoTask): Long
    
    @Update
    suspend fun updateTask(task: TodoTask)
    
    @Delete
    suspend fun deleteTask(task: TodoTask)
    
    @Query("DELETE FROM todo_tasks WHERE id = :taskId AND userId = :userId")
    suspend fun deleteTaskById(taskId: Long, userId: Long)
    
    @Query("SELECT COUNT(*) FROM todo_tasks WHERE userId = :userId")
    suspend fun getTaskCountByUser(userId: Long): Int
    
    @Query("SELECT COUNT(*) FROM todo_tasks WHERE userId = :userId AND status = :status")
    suspend fun getTaskCountByUserAndStatus(userId: Long, status: String): Int
}