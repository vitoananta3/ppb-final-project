package com.example.ets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ets.ui.theme.EtsTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EtsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val data = getMockTask()

    // Convert the list to a string for debugging
    val debugText = data.joinToString("\n") { task ->
        "Title: ${task.title}, Tag: ${task.tag}, Deadline: ${task.deadline.get(Calendar.DAY_OF_MONTH)}-${task.deadline.get(Calendar.MONTH) + 1}-${task.deadline.get(Calendar.YEAR)} ${task.deadline.get(Calendar.HOUR_OF_DAY)}:${task.deadline.get(Calendar.MINUTE)}"
    }

    Text(
        text = debugText,  // Displaying the list as text
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EtsTheme {
        Greeting("Android")
    }
}