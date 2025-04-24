package com.example.ets

import java.util.Calendar
import java.util.UUID
import java.util.Random

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val tag: String,
    val deadline: Calendar
)


fun getMockTask(): List<Task> {
    // Create an array of Calendar objects
    val calendarArray = Array(10) {
        Calendar.getInstance().apply {
            // Randomize year, month, day, hour, and minute
            set(
                2025, // Year
                Random().nextInt(12), // Random month (0 - 11)
                Random().nextInt(28) + 1, // Random day (1 - 28)
                Random().nextInt(24), // Random hour (0 - 23)
                Random().nextInt(60) // Random minute (0 - 59)
            )
        }
    }

    // Map the randomized calendar objects to tasks
    return listOf(
        Task(
            title = "Tugas ETS",
            tag = "Kuliah",
            deadline = calendarArray[0]
        ),
        Task(
            title = "Build Compose UI",
            tag = "Coding",
            deadline = calendarArray[1]
        ),
        Task(
            title = "Review Code for App",
            tag = "Coding",
            deadline = calendarArray[2]
        ),
        Task(
            title = "Meeting with Client",
            tag = "Work",
            deadline = calendarArray[3]
        ),
        Task(
            title = "Prepare Presentation for Workshop",
            tag = "Work",
            deadline = calendarArray[4]
        ),
        Task(
            title = "Attend Workshop",
            tag = "Learning",
            deadline = calendarArray[5]
        ),
        Task(
            title = "Submit Project Report",
            tag = "Work",
            deadline = calendarArray[6]
        ),
        Task(
            title = "Plan Next Sprint",
            tag = "Work",
            deadline = calendarArray[7]
        ),
        Task(
            title = "Team Building Activity",
            tag = "Team",
            deadline = calendarArray[8]
        ),
        Task(
            title = "Launch New Feature",
            tag = "Work",
            deadline = calendarArray[9]
        )
    )
}


