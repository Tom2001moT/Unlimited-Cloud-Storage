package com.example.unlimcloud.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun DonateScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.donate_icon),
            contentDescription = "Donate Support",
            modifier = Modifier
                .size(70.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Support Unlim Cloud",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "If you find this software useful, consider supporting us with a donation. Your contributions help maintain and improve Unlim Cloud across platforms.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Official Donate Inulute Portal
        DonateActionCard(
            title = "Official Donation Page",
            subtitle = "donate.inulute.vercel.app",
            icon = Icons.Default.Favorite,
            badgeColor = Color(0xFFEF4444),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.inulute.vercel.app"))
                context.startActivity(intent)
            },
            testTag = "donate_official_card"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ko-fi
        DonateActionCard(
            title = "Buy Me a Coffee (Ko-fi)",
            subtitle = "ko-fi.com/inulute",
            icon = Icons.Default.Coffee,
            badgeColor = Color(0xFFF59E0B),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/inulute"))
                context.startActivity(intent)
            },
            testTag = "donate_kofi_card"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PayPal
        DonateActionCard(
            title = "PayPal",
            subtitle = "paypal.me/inulute",
            icon = Icons.Default.Payment,
            badgeColor = Color(0xFF3B82F6),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/inulute"))
                context.startActivity(intent)
            },
            testTag = "donate_paypal_card"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // UPI
        DonateActionCard(
            title = "UPI Payment (India Only)",
            subtitle = "upi-inulute.vercel.app",
            icon = Icons.Default.Payment,
            badgeColor = Color(0xFF10B981),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://upi-inulute.vercel.app/"))
                context.startActivity(intent)
            },
            testTag = "donate_upi_card"
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inulute/unlim-cloud"))
                context.startActivity(intent)
            },
            modifier = Modifier.testTag("github_repo_button")
        ) {
            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View Project on GitHub")
        }
    }
}

@Composable
private fun DonateActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.OpenInBrowser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
