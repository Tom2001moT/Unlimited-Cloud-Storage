package com.example.unlimcloud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unlimcloud.ui.MainViewModel
import com.example.unlimcloud.ui.ScreenDestination
import com.example.unlimcloud.ui.components.AppNavigationDrawerContent
import com.example.unlimcloud.ui.screens.DonateScreen
import com.example.unlimcloud.ui.screens.ExplorerScreen
import com.example.unlimcloud.ui.screens.GalleryScreen
import com.example.unlimcloud.ui.screens.SplashScreen
import com.example.unlimcloud.ui.screens.UpdatesScreen
import com.example.unlimcloud.ui.screens.WebPortalScreen
import com.example.unlimcloud.ui.theme.UnlimCloudTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UnlimCloudTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnlimCloudApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlimCloudApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val session by viewModel.session.collectAsState()
    val files by viewModel.filteredFiles.collectAsState()
    val mediaFiles by viewModel.mediaFiles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()
    val isGridViewMode by viewModel.isGridViewMode.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }

    // Handle back button when drawer is open or when navigating secondary screens
    if (drawerState.isOpen) {
        BackHandler {
            scope.launch { drawerState.close() }
        }
    } else if (currentScreen !is ScreenDestination.Splash && currentScreen !is ScreenDestination.Explorer) {
        BackHandler {
            viewModel.navigateTo(ScreenDestination.Explorer)
        }
    }

    if (currentScreen is ScreenDestination.Splash) {
        SplashScreen(
            updateState = updateState,
            isChecking = isCheckingUpdate,
            onContinueToApp = { viewModel.navigateTo(ScreenDestination.Explorer) },
            onOpenDonate = { viewModel.navigateTo(ScreenDestination.Donate) },
            onRetryCheck = { viewModel.checkForUpdates(silentCheck = false) }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppNavigationDrawerContent(
                    currentScreen = currentScreen,
                    session = session,
                    onDestinationClicked = { destination ->
                        viewModel.navigateTo(destination)
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClicked = {
                        scope.launch { drawerState.close() }
                        showLogoutConfirmDialog = true
                    },
                    onSwitchAccountClicked = {
                        scope.launch { drawerState.close() }
                        showLoginDialog = true
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.testTag("open_navigation_drawer_button")
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Open Navigation Drawer",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.unlim_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = when (currentScreen) {
                                        is ScreenDestination.Explorer -> "Unlim Cloud"
                                        is ScreenDestination.Gallery -> "Gallery"
                                        is ScreenDestination.Updates -> "Updates"
                                        is ScreenDestination.Donate -> "Support"
                                        is ScreenDestination.WebPortal -> "Web Portal"
                                        else -> "Unlim Cloud"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                },
                                modifier = Modifier.testTag("quota_drawer_shortcut_button")
                            ) {
                                Icon(
                                    Icons.Default.PieChart,
                                    contentDescription = "View Storage Quota",
                                    tint = Color(0xFF65DE69)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is ScreenDestination.Explorer,
                            onClick = { viewModel.navigateTo(ScreenDestination.Explorer) },
                            icon = { Icon(Icons.Default.Folder, contentDescription = "Files") },
                            label = { Text("Files") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F141C),
                                selectedTextColor = Color(0xFF65DE69),
                                indicatorColor = Color(0xFF65DE69)
                            ),
                            modifier = Modifier.testTag("nav_item_files")
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenDestination.Gallery,
                            onClick = { viewModel.navigateTo(ScreenDestination.Gallery) },
                            icon = { Icon(Icons.Default.PermMedia, contentDescription = "Gallery") },
                            label = { Text("Gallery") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F141C),
                                selectedTextColor = Color(0xFF65DE69),
                                indicatorColor = Color(0xFF65DE69)
                            ),
                            modifier = Modifier.testTag("nav_item_gallery")
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenDestination.WebPortal,
                            onClick = { viewModel.navigateTo(ScreenDestination.WebPortal) },
                            icon = { Icon(Icons.Default.Language, contentDescription = "Web Portal") },
                            label = { Text("Portal") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F141C),
                                selectedTextColor = Color(0xFF65DE69),
                                indicatorColor = Color(0xFF65DE69)
                            ),
                            modifier = Modifier.testTag("nav_item_portal")
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenDestination.Updates,
                            onClick = { viewModel.navigateTo(ScreenDestination.Updates) },
                            icon = { Icon(Icons.Default.SystemUpdate, contentDescription = "Updates") },
                            label = { Text("Updates") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F141C),
                                selectedTextColor = Color(0xFF65DE69),
                                indicatorColor = Color(0xFF65DE69)
                            ),
                            modifier = Modifier.testTag("nav_item_updates")
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenDestination.Donate,
                            onClick = { viewModel.navigateTo(ScreenDestination.Donate) },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Donate") },
                            label = { Text("Support") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F141C),
                                selectedTextColor = Color(0xFF65DE69),
                                indicatorColor = Color(0xFF65DE69)
                            ),
                            modifier = Modifier.testTag("nav_item_donate")
                        )
                    }
                }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        is ScreenDestination.Explorer -> {
                            ExplorerScreen(
                                session = session,
                                files = files,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                selectedSortOption = selectedSortOption,
                                isGridViewMode = isGridViewMode,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onCategoryChange = { viewModel.setCategory(it) },
                                onSortChange = { viewModel.setSortOption(it) },
                                onToggleGridView = { viewModel.toggleGridViewMode() },
                                onUploadFile = { name, cat, size -> viewModel.uploadSampleFile(name, cat, size) },
                                onDeleteFile = { id -> viewModel.deleteFile(id) },
                                onDeleteFiles = { ids -> viewModel.deleteFiles(ids) },
                                onMoveFiles = { ids, folder -> viewModel.moveFiles(ids, folder) },
                                onLogoutClicked = { showLogoutConfirmDialog = true }
                            )
                        }

                        is ScreenDestination.Gallery -> {
                            GalleryScreen(mediaFiles = mediaFiles)
                        }

                        is ScreenDestination.Updates -> {
                            UpdatesScreen(
                                updateState = updateState,
                                isChecking = isCheckingUpdate,
                                onCheckAgain = { viewModel.checkForUpdates(silentCheck = false) }
                            )
                        }

                        is ScreenDestination.Donate -> {
                            DonateScreen()
                        }

                        is ScreenDestination.WebPortal -> {
                            WebPortalScreen()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Logout from Telegram?") },
            text = {
                Text("You will be logged out of current Telegram cloud storage account (${session?.telegramId ?: "Connected"}). You can log back in or connect another Telegram ID.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        viewModel.logout()
                        showLoginDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_logout_dialog_button")
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Login / Switch Telegram Account Dialog
    if (showLoginDialog) {
        var inputId by remember { mutableStateOf("") }
        var inputUser by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Connect Telegram Storage") },
            text = {
                Column {
                    Text("Enter your Telegram ID to load files from your personal Telegram Cloud storage:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputId,
                        onValueChange = { inputId = it },
                        label = { Text("Telegram ID (e.g. 819283741)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_telegram_id_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputUser,
                        onValueChange = { inputUser = it },
                        label = { Text("Username (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalId = if (inputId.isBlank()) (100000000..999999999).random().toString() else inputId.trim()
                        val finalUser = if (inputUser.isBlank()) "user_$finalId" else inputUser.trim()
                        viewModel.login(finalId, finalUser)
                        showLoginDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF65DE69),
                        contentColor = Color(0xFF0F141C)
                    ),
                    modifier = Modifier.testTag("confirm_login_dialog_button")
                ) {
                    Text("Connect Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
