package com.example.smartcampuscompanion.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.Task
import com.example.smartcampuscompanion.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.smartcampuscompanion.util.AlarmScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    navController: NavController,
    prefs: SharedPreferences
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadTasksForCurrentUser()
    }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }

    val isDark = remember { prefs.getBoolean("is_dark_mode", true) }
    val startGradient by animateColorAsState(if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0F2F5), tween(500), label = "")
    val endGradient by animateColorAsState(if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF), tween(500), label = "")
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subTextColor = textColor.copy(alpha = 0.6f)

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    fun openForAdd() {
        editingTask = null; title = ""; desc = ""; selectedTimestamp = System.currentTimeMillis(); showSheet = true
    }

    fun openForEdit(task: Task) {
        editingTask = task; title = task.title; desc = task.description; selectedTimestamp = task.dueDate; showSheet = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { openForAdd() },
                containerColor = Color(0xFF8E2DE2),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("New Task") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(startGradient, endGradient)))
                .padding(padding)
        ) {
            if (isLoading && tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8E2DE2))
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Schedule",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                            Text(
                                "${tasks.count { !it.isCompleted }} pending tasks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = subTextColor
                            )
                        }

                        Surface(
                            onClick = {
                                DatePickerDialog(context, { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                                    calendar.set(Calendar.MINUTE, 0)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)
                                    viewModel.setDateFilter(calendar.timeInMillis)
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            color = if (selectedDateFilter != null) Color(0xFF8E2DE2) else Color(0xFF8E2DE2).copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, "Calendar Filter", tint = if (selectedDateFilter != null) Color.White else Color(0xFF8E2DE2), modifier = Modifier.padding(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedDateFilter != null) {
                        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        val dateLabel = dateFormat.format(java.util.Date(selectedDateFilter!!))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = { viewModel.clearDateFilter() },
                                label = { Text("Filtered: $dateLabel") },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", Modifier.size(18.dp))
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (tasks.isEmpty()) {
                        EmptyTaskState(textColor)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(tasks, key = { it.documentId.ifEmpty { it.id.toString() } }) { task ->
                                TaskItem(task, isDark, { viewModel.toggleTaskCompletion(task) }, { viewModel.deleteTask(task) }, { openForEdit(task) })
                            }
                        }
                    }
                }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White,
                ) {
                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth().navigationBarsPadding()) {
                        Text(if (editingTask == null) "Create New Task" else "Edit Task", color = textColor, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            onClick = {
                                DatePickerDialog(context, { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    TimePickerDialog(context, { _, hh, mm ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hh)
                                        calendar.set(Calendar.MINUTE, mm)
                                        selectedTimestamp = calendar.timeInMillis
                                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            color = Color(0xFF8E2DE2).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, "Time", tint = Color(0xFF8E2DE2))
                                Spacer(modifier = Modifier.width(12.dp))
                                val formattedDate = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(selectedTimestamp))
                                Text(formattedDate, color = textColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (editingTask == null) {
                                    viewModel.addTask(title, desc, selectedTimestamp)
                                    // Schedule reminder for new task
                                    val fakeId = (title + selectedTimestamp).hashCode()
                                    AlarmScheduler.scheduleTaskReminder(context, fakeId, title, selectedTimestamp)
                                } else {
                                    viewModel.updateTask(editingTask!!.copy(title = title, description = desc, dueDate = selectedTimestamp))
                                    // Reschedule reminder for updated task
                                    val fakeId = (title + selectedTimestamp).hashCode()
                                    AlarmScheduler.scheduleTaskReminder(context, fakeId, title, selectedTimestamp)
                                }
                                showSheet = false
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            enabled = title.isNotBlank()
                        ) { Text("Save Task") }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, isDark: Boolean, onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val dateLabel = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(task.dueDate))
    val containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00BFA5))
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) textColor.copy(alpha = 0.4f) else textColor
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (task.isCompleted) Color.Gray else Color(0xFF8E2DE2),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        dateLabel,
                        color = if (task.isCompleted) Color.Gray else Color(0xFF8E2DE2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(onClick = {
                // Cancel reminder before deleting
                val fakeId = (task.title + task.dueDate).hashCode()
                AlarmScheduler.cancelTaskReminder(context, fakeId)
                onDelete()
            }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun EmptyTaskState(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircleOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF00BFA5).copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("All caught up!", color = textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("You have no pending tasks", color = textColor.copy(alpha = 0.5f))
    }
}