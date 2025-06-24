package com.example.gasnugas.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "todo_tasks",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class TodoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val date: String, // Store as String for Room compatibility
    val tags: String, // Store as comma-separated string
    val status: String, // Store as string enum
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Note: TaskStatus enum is now defined in ui/utils.kt

// Extension functions to convert between domain models
fun TodoTask.toDomainTask(): com.example.gasnugas.ui.Task {
    return com.example.gasnugas.ui.Task(
        id = this.id.toInt(),
        title = this.title,
        date = LocalDate.parse(this.date),
        tags = if (this.tags.isBlank()) emptyList() else this.tags.split(","),
        status = when (this.status) {
            "BACKLOG" -> com.example.gasnugas.ui.TaskStatus.BACKLOG
            "IN_PROGRESS" -> com.example.gasnugas.ui.TaskStatus.IN_PROGRESS
            "DONE" -> com.example.gasnugas.ui.TaskStatus.DONE
            else -> com.example.gasnugas.ui.TaskStatus.BACKLOG
        }
    )
}

fun com.example.gasnugas.ui.Task.toTodoTask(userId: Long): TodoTask {
    return TodoTask(
        id = this.id.toLong(),
        userId = userId,
        title = this.title,
        date = this.date.toString(),
        tags = this.tags.joinToString(","),
        status = this.status.name,
        updatedAt = System.currentTimeMillis()
    )
} 