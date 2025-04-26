package com.example.gasnugas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gasnugas.ui.theme.GasnugasTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    onCreateTask: (String, LocalDate?, String, List<String>) -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Backlog") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var newTagText by remember { mutableStateOf("") }
    
    // Status dropdown states
    var showStatusDropdown by remember { mutableStateOf(false) }
    val statusOptions = listOf("Backlog", "In Progress", "Done")
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Task Name
            Column {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )
            }
            
            // Deadline
            Column {
                Text(
                    text = "Deadline",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedDate?.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)) ?: "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    enabled = false,
                    placeholder = { Text("Select date") },
                    shape = MaterialTheme.shapes.small,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar"
                        )
                    }
                )
                
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                    )
                    
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val instant = Instant.ofEpochMilli(millis)
                                        selectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
            
            // Status
            Column {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = showStatusDropdown,
                    onExpandedChange = { showStatusDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    selectedStatus = option
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Tags - Modified to handle multiple tags
            Column {
                Text(
                    text = "Tag",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display existing tags
                if (tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(tags) { tag ->
                            TagChip(
                                tag = tag,
                                onDelete = { tags = tags.filter { it != tag } }
                            )
                        }
                    }
                }
                
                // Input field to add new tag
                OutlinedTextField(
                    value = newTagText,
                    onValueChange = { newTagText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    placeholder = { Text("Add a tag") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (newTagText.isNotBlank()) {
                                    tags = tags + newTagText.trim()
                                    newTagText = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Tag"
                            )
                        }
                    }
                )
            }
            
            // Spacer to push the create button to the bottom
            Spacer(modifier = Modifier.weight(1f))
            
            // Create Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { 
                        onCreateTask(taskName, selectedDate, selectedStatus, tags)
                        onNavigateBack()
                    },
                    enabled = taskName.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EA)
                    )
                ) {
                    Text("Create")
                }
            }
        }
    }
} 