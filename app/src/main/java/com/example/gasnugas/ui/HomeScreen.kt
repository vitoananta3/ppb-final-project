package com.example.gasnugas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gasnugas.ui.auth.AuthViewModel
import com.example.gasnugas.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    navController: androidx.navigation.NavController,
    taskViewModel: TaskViewModel = viewModel()
) {
    // Remove these state variables since we'll use navigation instead
    // var showCreateTaskScreen by remember { mutableStateOf(false) }
    // var showTaskDetailScreen by remember { mutableStateOf(false) }
    // var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var statusFilter by remember { mutableStateOf("All") }
    var tagFilter by remember { mutableStateOf("All") }
    var isNameSortAscendingSort by remember { mutableStateOf(true) }
    var isDeadlineSortAscendingSort by remember { mutableStateOf(true) }
    var selectedSortOption by remember { mutableStateOf("Deadline") }
    val context = LocalContext.current

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    val currentUserId = currentUser?.id ?: return
    
    // Get tasks from Room database
    val allTasks by taskViewModel.getTasksByUser(currentUserId).collectAsState(initial = emptyList())
    
    // Apply filters to tasks
    val filteredTasks = remember(allTasks, statusFilter, tagFilter, isNameSortAscendingSort, isDeadlineSortAscendingSort, selectedSortOption) {
        applyTaskFilters(allTasks, statusFilter, tagFilter, isNameSortAscendingSort, isDeadlineSortAscendingSort, selectedSortOption)
    }

    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH)
    val monthName = today.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)
    
    val backlogCount = allTasks.count { task -> task.status == TaskStatus.BACKLOG }
    val inProgressCount = allTasks.count { task -> task.status == TaskStatus.IN_PROGRESS }
    val doneCount = allTasks.count { task -> task.status == TaskStatus.DONE }

    var showSortFilterDialog by remember { mutableStateOf(false) }

    // Remove conditional rendering - now using navigation
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Card(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF424242), // Darker border color
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gas",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Nugas",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6200EA) // Same color as DONE status
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Welcome Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Welcome back, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "User"}!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$dayOfWeek, ${today.dayOfMonth} $monthName ${today.year}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                
                item {
                    // Task status counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskStatusChip(
                            count = backlogCount,
                            label = "Backlog",
                            modifier = Modifier.weight(1f)
                        )
                        TaskStatusChip(
                            count = inProgressCount,
                            label = "In Progress",
                            modifier = Modifier.weight(1f)
                        )
                        TaskStatusChip(
                            count = doneCount,
                            label = "Done",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    // Sort & Filter button with Add button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            onClick = { showSortFilterDialog = true },
                            label = { Text("Filter & Sort") },
                            selected = statusFilter != "All" || tagFilter != "All",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.FilterAlt,
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Add button with updated onClick to navigate to CreateTaskScreen
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { navController.navigate("create_task") },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Task",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
                
                // Tasks list
                if (filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "No tasks found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Try changing your filters or create a new task",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = {
                                navController.navigate("task_detail/${task.id}")
                            },
                            onDeleteClick = {
                                taskToDelete = task
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            // Sort and Filter Dialog
            if (showSortFilterDialog) {
                SortFilterDialog(
                    onDismiss = { showSortFilterDialog = false },
                    onApply = { newStatusFilter, newTagFilter, newIsNameSortAscending, newIsDeadlineSortAscending, newSortOption ->
                        statusFilter = newStatusFilter
                        tagFilter = newTagFilter
                        selectedSortOption = newSortOption
                        isNameSortAscendingSort = newIsNameSortAscending
                        isDeadlineSortAscendingSort = newIsDeadlineSortAscending
                        showSortFilterDialog = false
                    },
                    tasks = allTasks,
                    currentStatusFilter = statusFilter,
                    currentTagFilter = tagFilter,
                    isNameSortAscending = isNameSortAscendingSort,
                    isDeadlineSortAscending = isDeadlineSortAscendingSort,
                    currentSortOption = selectedSortOption
                )
            }
            
            // Delete confirmation dialog
            if (showDeleteDialog && taskToDelete != null) {
                AlertDialog(
                    onDismissRequest = { 
                        showDeleteDialog = false
                        taskToDelete = null
                    },
                    shape = RoundedCornerShape(8.dp),
                    title = {
                        Text(text = "Are you sure?")
                    },
                    text = {
                        Text(text = "This will permanently delete the task \"${taskToDelete?.title}\". This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                taskToDelete?.let { task ->
                                    taskViewModel.deleteTask(task, currentUserId)
                                }
                                showDeleteDialog = false
                                taskToDelete = null
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { 
                                showDeleteDialog = false
                                taskToDelete = null
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }

// Helper function to apply filters and sorting - renamed to avoid conflicts
private fun applyTaskFilters(
    tasks: List<Task>,
    status: String,
    tag: String,
    isNameSortAscending: Boolean,
    isDeadlineSortAscending: Boolean,
    sortOption: String = "Deadline"
): List<Task> {
    var filtered = tasks
    
    // Apply status filter if not "All"
    if (status != "All") {
        // Convert space to underscore for enum matching (e.g., "In Progress" -> "IN_PROGRESS")
        val statusEnum = status.replace(" ", "_").uppercase()
        filtered = filtered.filter {
            it.status.name.equals(statusEnum, ignoreCase = true)
        }
    }
    
    // Apply tag filter if not "All"
    if (tag != "All") {
        filtered = filtered.filter { it.tags.contains(tag) }
    }
    
    // Apply sorting based on selected sort option
    filtered = when (sortOption) {
        "Name" -> {
            if (isNameSortAscending) {
                filtered.sortedBy { it.title.lowercase() }
            } else {
                filtered.sortedByDescending { it.title.lowercase() }
            }
        }
        else -> { // "Deadline" or any other default
            if (isDeadlineSortAscending) {
                filtered.sortedBy { it.date }
            } else {
                filtered.sortedByDescending { it.date }
            }
        }
    }
    
    return filtered
}

@Composable
fun TaskStatusChip(count: Int, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onFiltered: (List<Task>, String, String, Boolean, Boolean, String) -> Unit,
    currentStatusFilter: String = "All",
    currentTagFilter: String = "All",
    isNameSortAscending: Boolean = true,
    isDeadlineSortAscending: Boolean = true,
    currentSortOption: String = "Deadline"
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        SortFilterDialog(
            onDismiss = { showDialog = false },
            onApply = { status, tag, isNameSortAsc, isDeadlineSortAsc, sortOption ->
                // Apply filters and sorting directly using the applyTaskFilters function
                val filtered = applyTaskFilters(
                    tasks, 
                    status, 
                    tag, 
                    isNameSortAsc, 
                    isDeadlineSortAsc, 
                    sortOption
                )

                // Return filtered list and all filter/sort values to parent
                onFiltered(filtered, status, tag, isNameSortAsc, isDeadlineSortAsc, sortOption)
                showDialog = false
            },
            tasks = tasks,
            currentStatusFilter = currentStatusFilter,
            currentTagFilter = currentTagFilter,
            isNameSortAscending = isNameSortAscending,
            isDeadlineSortAscending = isDeadlineSortAscending,
            currentSortOption = currentSortOption
        )
    }
    
    Card(
        modifier = modifier.clickable { showDialog = true },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                "Sort & Filter",
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = "Filter",
                tint = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterDialog(
    onDismiss: () -> Unit,
    onApply: (String, String, Boolean, Boolean, String) -> Unit,
    tasks: List<Task>? = null,
    currentStatusFilter: String = "All",
    currentTagFilter: String = "All",
    isNameSortAscending: Boolean = true,
    isDeadlineSortAscending: Boolean = true,
    currentSortOption: String = "Deadline"
) {
    // State for selected values, initialized with current filters
    var selectedStatus by remember { mutableStateOf(currentStatusFilter) }
    var selectedTag by remember { mutableStateOf(currentTagFilter) }
    
    // Sort direction state initialized with current values
    var isNameSortAscending by remember { mutableStateOf(isNameSortAscending) }
    var isDeadlineSortAscending by remember { mutableStateOf(isDeadlineSortAscending) }
    
    // Remember the currently selected sort option
    var selectedSortOption by remember { mutableStateOf(currentSortOption) }
    
    // Available options
    val statusOptions = listOf("All", "Backlog", "In Progress", "Done")
    
    // Get available tags from all tasks
    val uniqueTags = remember(tasks) {
        val allTasks = tasks ?: emptyList()
        val allTags = if (allTasks.isNotEmpty()) {
            allTasks.flatMap { it.tags }.distinct().sorted()
        } else {
            listOf("Campus", "PPB", "PPL")
        }
        listOf("All") + allTags
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(8.dp),
        title = { 
            Text(
                "Sort & Filter", 
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sort by section
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortOptions = listOf("Name", "Deadline")
                    
                    sortOptions.forEach { option ->
                        FilterChip(
                            selected = selectedSortOption == option,
                            onClick = { 
                                // If clicking the already selected option, toggle sort direction
                                if (selectedSortOption == option) {
                                    if (option == "Name") {
                                        isNameSortAscending = !isNameSortAscending
                                    } else {
                                        isDeadlineSortAscending = !isDeadlineSortAscending
                                    }
                                } else {
                                    selectedSortOption = option
                                }
                            },
                            label = { Text(option) },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                if (selectedSortOption == option) {
                                    // Determine rotation based on the current sort direction
                                    val isAscending = if (option == "Name") isNameSortAscending else isDeadlineSortAscending
                                    val rotation = if (isAscending) 0f else 180f
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = "Sort direction",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .rotate(rotation)
                                    )
                                }
                            }
                        )
                    }
                }
                
                // Filter by Status section
                Text(
                    text = "Filter by Status",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
                
                // Using regular dropdown menu instead of ExposedDropdownMenuBox
                var showStatusMenu by remember { mutableStateOf(false) }
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStatusMenu = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedStatus)
                            // Removed arrow icon
                        }
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .heightIn(max = 233.dp) // Set maximum height
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedStatus = option
                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Filter by Tag section
                Text(
                    text = "Filter by Tag",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
                
                // Using regular dropdown menu for tags as well
                var showTagMenu by remember { mutableStateOf(false) }
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTagMenu = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedTag)
                            // Removed arrow icon
                        }
                        
                        DropdownMenu(
                            expanded = showTagMenu,
                            onDismissRequest = { showTagMenu = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .heightIn(max = 233.dp) // Set maximum height
                        ) {
                            uniqueTags.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedTag = option
                                        showTagMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(selectedStatus, selectedTag, isNameSortAscending, isDeadlineSortAscending, selectedSortOption)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val formattedDate = "${task.date.dayOfWeek.getDisplayName(
                            java.time.format.TextStyle.SHORT, 
                            Locale.ENGLISH
                        )}, ${task.date.dayOfMonth} ${
                            task.date.month.getDisplayName(
                                java.time.format.TextStyle.SHORT, 
                                Locale.ENGLISH
                            )
                        } ${task.date.year}"
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tags with limited display
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (task.tags.size <= 2) {
                            // Show all tags if 2 or fewer
                            task.tags.forEach { tag ->
                                TagChip(tag = tag)
                            }
                        } else {
                            // Show only first 2 tags + "..." indicator
                            TagChip(tag = task.tags[0])
                            TagChip(tag = task.tags[1])
                            TagChip(tag = "...")
                        }
                    }
                }
                
                // Status badge moved here, delete button removed from this row
                StatusBadge(status = task.status)
            }
            
            // New row for delete button at the bottom right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Task",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun TagChip(tag: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .widthIn(max = 120.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val (backgroundColor, textColor) = when(status) {
        TaskStatus.BACKLOG -> Pair(Color.LightGray, Color.DarkGray)
        TaskStatus.IN_PROGRESS -> Pair(Color(0xFFFFD700), Color.Black)
        TaskStatus.DONE -> Pair(Color(0xFF6200EA), Color.White)
    }
    
    val text = when(status) {
        TaskStatus.BACKLOG -> "Backlog"
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.DONE -> "Done"
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = textColor
        )
    }
}

// @Preview(showBackground = true)
// @Composable
// fun HomeScreenPreview() {
//     GasnugasTheme {
//         HomeScreen()
//     }
// } 