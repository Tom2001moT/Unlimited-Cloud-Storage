package com.example.unlimcloud.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unlimcloud.R
import com.example.unlimcloud.data.TelegramSession
import com.example.unlimcloud.ui.ScreenDestination

@Composable
fun AppNavigationDrawerContent(
    currentScreen: ScreenDestination,
    session: TelegramSession?,
    onDestinationClicked: (ScreenDestination) -> Unit,
    onLogoutClicked: () -> Unit,
    onSwitchAccountClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .testTag("app_navigation_drawer_sheet"),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unlim_logo),
                    contentDescription = "Unlim Cloud Logo",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Unlim Cloud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Telegram Storage Client",
                        fontSize = 12.sp,
                        color = Color(0xFF65DE69),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Scrollable Navigation Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavigationDrawerItem(
                    label = { Text("Files Explorer", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    selected = currentScreen is ScreenDestination.Explorer,
                    onClick = { onDestinationClicked(ScreenDestination.Explorer) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF65DE69).copy(alpha = 0.15f),
                        selectedIconColor = Color(0xFF65DE69),
                        selectedTextColor = Color(0xFF65DE69),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_files")
                )

                NavigationDrawerItem(
                    label = { Text("Media Gallery", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.PermMedia, contentDescription = null) },
                    selected = currentScreen is ScreenDestination.Gallery,
                    onClick = { onDestinationClicked(ScreenDestination.Gallery) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF65DE69).copy(alpha = 0.15f),
                        selectedIconColor = Color(0xFF65DE69),
                        selectedTextColor = Color(0xFF65DE69),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_gallery")
                )

                NavigationDrawerItem(
                    label = { Text("Web Portal", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Language, contentDescription = null) },
                    selected = currentScreen is ScreenDestination.WebPortal,
                    onClick = { onDestinationClicked(ScreenDestination.WebPortal) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF65DE69).copy(alpha = 0.15f),
                        selectedIconColor = Color(0xFF65DE69),
                        selectedTextColor = Color(0xFF65DE69),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_portal")
                )

                NavigationDrawerItem(
                    label = { Text("Updates", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null) },
                    selected = currentScreen is ScreenDestination.Updates,
                    onClick = { onDestinationClicked(ScreenDestination.Updates) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF65DE69).copy(alpha = 0.15f),
                        selectedIconColor = Color(0xFF65DE69),
                        selectedTextColor = Color(0xFF65DE69),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_updates")
                )

                NavigationDrawerItem(
                    label = { Text("Donate & Support", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    selected = currentScreen is ScreenDestination.Donate,
                    onClick = { onDestinationClicked(ScreenDestination.Donate) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF65DE69).copy(alpha = 0.15f),
                        selectedIconColor = Color(0xFF65DE69),
                        selectedTextColor = Color(0xFF65DE69),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_donate")
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Telegram Account Actions (Switch / Logout)
                NavigationDrawerItem(
                    label = { Text("Switch Telegram ID", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF65DE69)) },
                    selected = false,
                    onClick = onSwitchAccountClicked,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("drawer_nav_switch_account")
                )

                NavigationDrawerItem(
                    label = { Text("Logout Account", fontWeight = FontWeight.SemiBold, color = Color(0xFFEF4444)) },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444)) },
                    selected = false,
                    onClick = onLogoutClicked,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = Color(0xFFEF4444)
                    ),
                    modifier = Modifier.testTag("drawer_nav_logout")
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Visual Storage Quota Indicator at the bottom of the navigation drawer
            StorageQuotaIndicator(
                session = session,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_bottom_quota_indicator")
            )
        }
    }
}
