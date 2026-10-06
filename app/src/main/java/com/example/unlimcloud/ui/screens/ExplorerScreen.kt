package com.example.unlimcloud.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unlimcloud.data.CloudFile
import com.example.unlimcloud.data.SortOption
import com.example.unlimcloud.data.StorageCategory
import com.example.unlimcloud.data.TelegramSession
import kotlinx.coroutines.launch

@Composable
fun ExplorerScreen(
    session: TelegramSession?,
    files: List<CloudFile>,
    searchQuery: String,
    selectedCategory: StorageCategory,
    selectedSortOption: SortOption,
    isGridViewMode: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (StorageCategory) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleGridView: () -> Unit,
    onUploadFile: (String, StorageCategory, Long) -> Unit,
    onDeleteFile: (String) -> Unit,
    onDeleteFiles: (Set<String>) -> Unit,
    onMoveFiles: (Set<String>, String) -> Unit,
    onLogoutClicked: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Multi-select state
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedFileIds by remember { mutableStateOf(setOf<String>()) }
    var showBatchDeleteConfirm by remember { mutableStateOf(false) }
    var showBatchMoveDialog by remember { mutableStateOf(false) }

    if (isSelectionMode) {
        BackHandler {
            isSelectionMode = false
            selectedFileIds = emptySet()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF65DE69),
                    contentColor = Color(0xFF0F141C),
                    modifier = Modifier.testTag("upload_file_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload to Telegram Storage")
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isSelectionMode,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("multi_select_bottom_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isSelectionMode = false
                                    selectedFileIds = emptySet()
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${selectedFileIds.size} selected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (selectedFileIds.isNotEmpty()) {
                                        showBatchMoveDialog = true
                                    }
                                },
                                enabled = selectedFileIds.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B82F6),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("batch_move_button")
                            ) {
                                Icon(Icons.Default.DriveFileMove, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Move", fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    if (selectedFileIds.isNotEmpty()) {
                                        showBatchDeleteConfirm = true
                                    }
                                },
                                enabled = selectedFileIds.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("batch_delete_button")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Storage quota & Telegram identity banner
            SessionSummaryCard(session = session, onLogoutClicked = onLogoutClicked)

            // Search Bar at the top of the file explorer
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_files_input"),
                placeholder = { Text("Search files & folders by name...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (searchQuery.isNotBlank()) Color(0xFF65DE69) else Color(0xFF94A3B8)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF65DE69),
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Active search filter indicator if user is querying
            if (searchQuery.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found ${files.size} ${if (files.size == 1) "result" else "results"} for \"$searchQuery\"",
                        fontSize = 12.sp,
                        color = Color(0xFF65DE69),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Clear filter",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onSearchChange("") }
                            .padding(4.dp)
                    )
                }
            }

            // Category Filter Chips, Sort Dropdown & Grid View Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = StorageCategory.values()
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF65DE69),
                                selectedLabelColor = Color(0xFF0F141C)
                            ),
                            modifier = Modifier.testTag("filter_chip_${cat.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Grid / List Toggle Button
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGridViewMode) Color(0xFF65DE69).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .clickable { onToggleGridView() }
                        .testTag("toggle_view_mode_button")
                ) {
                    Box(
                        modifier = Modifier.padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGridViewMode) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridViewMode) "Switch to List View" else "Switch to Grid View",
                            tint = if (isGridViewMode) Color(0xFF65DE69) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Sort Dropdown Button & Menu
                Box {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .clickable { sortMenuExpanded = true }
                            .testTag("sort_dropdown_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort files",
                                tint = Color(0xFF65DE69),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sort",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        Text(
                            text = "Sort by",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF65DE69),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )

                        SortOption.values().forEach { option ->
                            val isSelected = selectedSortOption == option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF65DE69) else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF65DE69),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(18.dp))
                                    }
                                },
                                onClick = {
                                    onSortChange(option)
                                    sortMenuExpanded = false
                                },
                                modifier = Modifier.testTag("sort_item_${option.name}")
                            )
                        }
                    }
                }
            }

            // Selection Mode Header Bar
            if (isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Multi-Select Mode Active",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF65DE69)
                    )
                    TextButton(
                        onClick = {
                            selectedFileIds = if (selectedFileIds.size == files.size) {
                                emptySet()
                            } else {
                                files.map { it.id }.toSet()
                            }
                        },
                        modifier = Modifier.testTag("select_all_button")
                    ) {
                        Icon(Icons.Default.SelectAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (selectedFileIds.size == files.size) "Deselect All" else "Select All", fontSize = 12.sp)
                    }
                }
            }

            // File Content View (List View or Grid View)
            if (files.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No files match \"$searchQuery\"" else "No files in your cloud storage yet",
                            color = Color(0xFF94A3B8),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to upload files to your Telegram storage",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (isGridViewMode) {
                // GRID VIEW MODE WITH THUMBNAIL PREVIEWS
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 135.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("explorer_grid_view")
                ) {
                    items(files, key = { it.id }) { file ->
                        val isSelected = selectedFileIds.contains(file.id)
                        FileGridItem(
                            file = file,
                            isSelectionMode = isSelectionMode,
                            isSelected = isSelected,
                            onLongPress = {
                                if (!isSelectionMode) {
                                    isSelectionMode = true
                                    selectedFileIds = setOf(file.id)
                                }
                            },
                            onTap = {
                                if (isSelectionMode) {
                                    selectedFileIds = if (isSelected) {
                                        val newSet = selectedFileIds - file.id
                                        if (newSet.isEmpty()) isSelectionMode = false
                                        newSet
                                    } else {
                                        selectedFileIds + file.id
                                    }
                                }
                            },
                            onDownload = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Downloading ${file.name} via Telegram API...")
                                }
                            },
                            onShare = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "UnlimCloud: ${file.name}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share file link"))
                            },
                            onDelete = {
                                onDeleteFile(file.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Removed ${file.name}")
                                }
                            }
                        )
                    }
                }
            } else {
                // LIST VIEW MODE
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("explorer_list_view"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(files, key = { it.id }) { file ->
                        val isSelected = selectedFileIds.contains(file.id)
                        FileItemRow(
                            file = file,
                            isSelectionMode = isSelectionMode,
                            isSelected = isSelected,
                            onLongPress = {
                                if (!isSelectionMode) {
                                    isSelectionMode = true
                                    selectedFileIds = setOf(file.id)
                                }
                            },
                            onTap = {
                                if (isSelectionMode) {
                                    selectedFileIds = if (isSelected) {
                                        val newSet = selectedFileIds - file.id
                                        if (newSet.isEmpty()) isSelectionMode = false
                                        newSet
                                    } else {
                                        selectedFileIds + file.id
                                    }
                                }
                            },
                            onDownload = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Downloading ${file.name} via Telegram API...")
                                }
                            },
                            onShare = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "UnlimCloud: ${file.name} (Telegram Message #${file.telegramMessageId ?: "N/A"})")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share file link"))
                            },
                            onDelete = {
                                onDeleteFile(file.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Removed ${file.name}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddFileDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, size ->
                onUploadFile(name, category, size)
                showAddDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Uploaded $name to Telegram Cloud")
                }
            }
        )
    }

    if (showBatchDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBatchDeleteConfirm = false },
            title = { Text("Delete ${selectedFileIds.size} files?") },
            text = {
                Text("This will permanently remove the selected items from your Telegram cloud storage channel.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = selectedFileIds.size
                        onDeleteFiles(selectedFileIds)
                        showBatchDeleteConfirm = false
                        isSelectionMode = false
                        selectedFileIds = emptySet()
                        scope.launch {
                            snackbarHostState.showSnackbar("Deleted $count items")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_batch_delete_button")
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBatchMoveDialog) {
        BatchMoveDialog(
            selectedCount = selectedFileIds.size,
            folders = files.filter { it.isFolder },
            onDismiss = { showBatchMoveDialog = false },
            onConfirmMove = { folderName ->
                onMoveFiles(selectedFileIds, folderName)
                showBatchMoveDialog = false
                val count = selectedFileIds.size
                isSelectionMode = false
                selectedFileIds = emptySet()
                scope.launch {
                    snackbarHostState.showSnackbar("Moved $count items to $folderName")
                }
            }
        )
    }
}

@Composable
private fun SessionSummaryCard(
    session: TelegramSession?,
    onLogoutClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = Color(0xFF65DE69),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Telegram ID: ${session?.telegramId ?: "Not Connected"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val used = session?.let { formatBytes(it.totalStorageUsedBytes) } ?: "0 B"
                Text(
                    text = "$used used • Unlimited Telegram Quota",
                    fontSize = 12.sp,
                    color = Color(0xFF65DE69),
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF65DE69).copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${session?.totalFiles ?: 0} Files",
                        color = Color(0xFF65DE69),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(
                    onClick = onLogoutClicked,
                    modifier = Modifier.testTag("summary_logout_button")
                ) {
                    Text("Logout", color = Color(0xFFFFA5A3), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileGridItem(
    file: CloudFile,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isImageOrVideo = file.category == StorageCategory.MEDIA && !file.isFolder
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF65DE69).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF65DE69)) else null,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        onTap()
                    }
                },
                onLongClick = onLongPress
            )
            .testTag("file_grid_item_${file.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Preview thumbnail container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                if (isImageOrVideo && !file.previewUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(file.previewUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    )

                    // Video badge overlay
                    if (file.name.endsWith(".mp4", ignoreCase = true) || file.name.endsWith(".mov", ignoreCase = true)) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayCircleFilled,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else {
                    // Fallback Icon display
                    Icon(
                        imageVector = when {
                            file.isFolder -> Icons.Default.Folder
                            file.category == StorageCategory.MEDIA -> Icons.Default.Image
                            file.category == StorageCategory.ARCHIVES -> Icons.Default.Archive
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = when (file.category) {
                            StorageCategory.MEDIA -> Color(0xFF60A5FA)
                            StorageCategory.DOCUMENTS -> Color(0xFFFACC15)
                            StorageCategory.ARCHIVES -> Color(0xFFC084FC)
                            else -> Color(0xFF65DE69)
                        },
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Checkbox badge in multi-select mode
                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = if (isSelected) "Selected" else "Not selected",
                            tint = if (isSelected) Color(0xFF65DE69) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = file.formattedSize,
                        fontSize = 10.sp,
                        color = Color(0xFF65DE69)
                    )
                }

                if (!isSelectionMode) {
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Download") },
                                leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onDownload()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Link") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onShare()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = Color(0xFFEF4444)) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileItemRow(
    file: CloudFile,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isImageOrVideo = file.category == StorageCategory.MEDIA && !file.isFolder
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF65DE69).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF65DE69)) else null,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        onTap()
                    }
                },
                onLongClick = onLongPress
            )
            .testTag("file_item_${file.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) Color(0xFF65DE69) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (file.category) {
                            StorageCategory.MEDIA -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                            StorageCategory.DOCUMENTS -> Color(0xFFEAB308).copy(alpha = 0.2f)
                            StorageCategory.ARCHIVES -> Color(0xFFA855F7).copy(alpha = 0.2f)
                            else -> Color(0xFF65DE69).copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isImageOrVideo && !file.previewUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(file.previewUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = when {
                            file.isFolder -> Icons.Default.Folder
                            file.category == StorageCategory.MEDIA -> Icons.Default.PermMedia
                            file.category == StorageCategory.ARCHIVES -> Icons.Default.Archive
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = when (file.category) {
                            StorageCategory.MEDIA -> Color(0xFF60A5FA)
                            StorageCategory.DOCUMENTS -> Color(0xFFFACC15)
                            StorageCategory.ARCHIVES -> Color(0xFFC084FC)
                            else -> Color(0xFF65DE69)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                val folderTag = if (file.folderPath != "/") " • ${file.folderPath}" else ""
                Text(
                    text = "${file.formattedSize} • ${file.modifiedTime}$folderTag",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isSelectionMode) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Download") },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDownload()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share Link") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFEF4444)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchMoveDialog(
    selectedCount: Int,
    folders: List<CloudFile>,
    onDismiss: () -> Unit,
    onConfirmMove: (String) -> Unit
) {
    var selectedFolder by remember { mutableStateOf(folders.firstOrNull()?.name ?: "Camera Backup 2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move $selectedCount Items") },
        text = {
            Column {
                Text("Select destination cloud folder:")
                Spacer(modifier = Modifier.height(12.dp))

                listOf("Camera Backup 2026", "Documents Archive", "Personal Cloud", "Telegram Downloads").forEach { folder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedFolder = folder }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = if (selectedFolder == folder) Color(0xFF65DE69) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = folder,
                            fontWeight = if (selectedFolder == folder) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFolder == folder) Color(0xFF65DE69) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmMove("/$selectedFolder") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65DE69), contentColor = Color(0xFF0F141C)),
                modifier = Modifier.testTag("confirm_move_button")
            ) {
                Text("Move Here")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddFileDialog(
    onDismiss: () -> Unit,
    onAdd: (String, StorageCategory, Long) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(StorageCategory.DOCUMENTS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload File to Cloud") },
        text = {
            Column {
                Text("Select and upload a document or media file to your Telegram storage.")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (e.g. Document.pdf or Photo.jpg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Category:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(StorageCategory.DOCUMENTS, StorageCategory.MEDIA, StorageCategory.ARCHIVES).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (fileName.isBlank()) "Uploaded_File_${System.currentTimeMillis() % 1000}" else fileName
                    val size = when (category) {
                        StorageCategory.MEDIA -> 45_000_000L
                        StorageCategory.ARCHIVES -> 120_000_000L
                        else -> 2_500_000L
                    }
                    onAdd(finalName, category, size)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65DE69), contentColor = Color(0xFF0F141C))
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.0f KB", kb)
        else -> "$bytes B"
    }
}
