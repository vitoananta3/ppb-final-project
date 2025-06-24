package com.example.gasnugas.ui

import android.content.Context
import java.io.File
import java.time.LocalDate

// UI Domain models - shared across the app
data class Task(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val tags: List<String>,
    val status: TaskStatus
)

enum class TaskStatus {
    BACKLOG, IN_PROGRESS, DONE
}

fun saveTasksToFile(context: Context, tasks: List<Task>) {
    val fileOutput = context.openFileOutput("tasks.txt", Context.MODE_PRIVATE)
    fileOutput.bufferedWriter().use { writer ->
        for (task in tasks) {
            val line = listOf(
                task.id,
                task.title,
                task.date.toString(),
                task.tags.joinToString(","),
                task.status.name
            ).joinToString("|||")
            writer.write(line)
            writer.newLine()
        }
    }
}

fun upsertTask(context: Context, task: Task) {
    val tasks = loadTasksFromFile(context).toMutableList()

    val existingIndex = tasks.indexOfFirst { it.id == task.id }
    if (existingIndex != -1) {
        tasks[existingIndex] = task // update
    } else {
        tasks.add(task) // insert
    }

    saveTasksToFile(context, tasks)
}

fun deleteTask(context: Context, task: Task) {
    // Load current tasks from file
    val tasks = loadTasksFromFile(context).toMutableList()

    // Remove the task with the matching ID
    tasks.removeIf { it.id == task.id }

    // Save the updated tasks list to the file
    saveTasksToFile(context, tasks)
}


fun loadTasksFromFileIfNeeded(context: Context, isDebug: Boolean = false): List<Task> {
    val file = File(context.filesDir, "tasks.txt")

    return if (file.exists() && file.length() > 0) {
        // If file exists and has content, load tasks from the file
        loadTasksFromFile(context)
    } else {
        // If file doesn't exist or is empty, initialize mock data
        if(isDebug){
            initMock(context)
        }
        else {
            initFile(context)
        }
        loadTasksFromFile(context)
    }
}

fun initFile(context: Context) {
    val fileOutput = context.openFileOutput("tasks.txt", Context.MODE_PRIVATE)
    fileOutput.use { /* just create an empty file */ }
}


fun initMock(context: Context) {
    val mockTasks = listOf(
        Task(1, "Evaluasi Tengah Semester", LocalDate.of(2025, 4, 23), listOf("PPB", "Campus"), TaskStatus.IN_PROGRESS),
        Task(2, "Tugas 4 - Aplikasi Ulang Tahun", LocalDate.of(2025, 4, 8), listOf("PPB"), TaskStatus.DONE),
        Task(3, "ETS", LocalDate.of(2025, 4, 26), listOf("PPL"), TaskStatus.DONE),
        Task(4, "Tugas 2 - Design UI/UX", LocalDate.of(2025, 4, 22), listOf("PPL"), TaskStatus.DONE),
        Task(5, "Mengambil Laundry", LocalDate.of(2025, 4, 24), listOf("PPL"), TaskStatus.BACKLOG)
    )

    // Save mock data to the file
    saveTasksToFile(context, mockTasks)
}



fun loadTasksFromFile(context: Context): List<Task> {
    val file = context.getFileStreamPath("tasks.txt")
    if (!file.exists()) return emptyList()

    return file.bufferedReader().useLines { lines ->
        lines.mapNotNull { line ->
            val parts = line.split("|||")
            if (parts.size == 5) {
                try {
                    Task(
                        id = parts[0].toInt(),
                        title = parts[1],
                        date = LocalDate.parse(parts[2]),
                        tags = parts[3].split(","),
                        status = TaskStatus.valueOf(parts[4])
                    )
                } catch (e: Exception) {
                    null // skip invalid lines
                }
            } else null
        }.toList()
    }
}

