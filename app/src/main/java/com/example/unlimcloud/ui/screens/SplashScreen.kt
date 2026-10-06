package com.example.unlimcloud.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unlimcloud.R
import com.example.unlimcloud.data.UpdateRepository

@Composable
fun SplashScreen(
    updateState: UpdateRepository.UpdateResult?,
    isChecking: Boolean,
    onContinueToApp: () -> Unit,
    onOpenDonate: () -> Unit,
    onRetryCheck: () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Animated Logo matching original index.html
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.2f) + fadeIn()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unlim_logo),
                    contentDescription = "UnlimCloud Logo",
                    modifier = Modifier
                        .size(170.dp)
                        .padding(bottom = 16.dp)
                        .testTag("app_logo")
                )
            }

            Text(
                text = "Unlim Cloud",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Unlimited Storage powered by Telegram ID",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (isChecking) {
                CircularProgressIndicator(
                    color = Color(0xFF65DE69),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Checking for updates...",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            } else {
                when (updateState) {
                    is UpdateRepository.UpdateResult.HasUpdate -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E2633)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Update Available: Latest Version - ${updateState.latestVersion}",
                                    color = Color(0xFF65DE69),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://github.com/inulute/unlim-cloud/releases/latest")
                                            )
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF65DE69),
                                            contentColor = Color(0xFF0D1117)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("download_update_button")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download", fontWeight = FontWeight.SemiBold)
                                    }

                                    OutlinedButton(
                                        onClick = onContinueToApp,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("remind_later_button")
                                    ) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Remind Later")
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Donate prompt from index.html
                                Text(
                                    text = "If you find this software useful, consider supporting us with a donation.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(2.dp, Color(0xFF4A4A4A), RoundedCornerShape(8.dp))
                                        .clickable { onOpenDonate() }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("donate_box")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.donate_icon),
                                            contentDescription = "Donate",
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Donate", color = Color(0xFF65DE69), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    is UpdateRepository.UpdateResult.Error -> {
                        Text(
                            text = "Error... Check your internet connection and try again.",
                            color = Color(0xFFFFA5A3),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onRetryCheck,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222C3A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("retry_check_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry")
                            }
                            Button(
                                onClick = onContinueToApp,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65DE69), contentColor = Color(0xFF0D1117)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("skip_to_app_button")
                            ) {
                                Text("Open App Offline")
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "Up to date (v2.0.0)",
                            color = Color(0xFF65DE69),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = onContinueToApp,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF65DE69),
                                contentColor = Color(0xFF0D1117)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("enter_app_button")
                        ) {
                            Icon(Icons.Default.Cloud, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Cloud Storage", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Web portal fallback shortcut
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://unlim-cloud.web.app"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("open_web_portal_button")
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Unlim Cloud Web Portal", fontSize = 13.sp)
            }
        }
    }
}
