package com.oceanx.weathersnap.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.oceanx.weathersnap.data.local.WeatherReport
import com.oceanx.weathersnap.ui.components.GradientTopAppBar
import com.oceanx.weathersnap.ui.theme.AccentGreenYellow
import com.oceanx.weathersnap.ui.theme.OliveGreenDark
import com.oceanx.weathersnap.ui.viewmodel.SavedReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedReportsScreen(onBack: () -> Unit) {
    val viewModel: SavedReportsViewModel = hiltViewModel()
    val reports by viewModel.reports.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = {
                    Column {
                        Text(
                            "Saved Reports",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A1E0F)
                        )
                        Text(
                            if (reports.isEmpty()) "No reports yet"
                            else "${reports.size} report${if (reports.size > 1) "s" else ""} stored locally",
                            fontSize = 12.sp,
                            color = Color(0xFF3A4A20)
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onBack,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF1A1E0F),   // dark button
                            contentColor = AccentGreenYellow
                        )
                    ) { Text("Back") }
                }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = reports.isEmpty(),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "reports_state"
        ) { isEmpty ->
            if (isEmpty) {
                EmptyReportsState(modifier = Modifier.padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(reports, key = { it.id }) { report ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 2 }
                        ) {
                            ReportCard(report = report)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyReportsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📭",
                fontSize = 48.sp
            )
            Text(
                text = "No reports yet",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                text = "Create a weather report to see it here",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ReportCard(report: WeatherReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = OliveGreenDark
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Captured image
            Image(
                painter = rememberAsyncImagePainter(report.imagePath),
                contentDescription = "Weather report photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "${report.cityName}, ${report.country}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = report.condition,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTimestamp(report.timestamp),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${report.temperature.toInt()}°C",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                // Weather stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReportStat("Humidity", "${report.humidity}%")
                    ReportStat("Wind", "${report.windSpeed} m/s")
                    ReportStat("Pressure", "${report.pressure.toInt()} hPa")
                }

                HorizontalDivider()

                // Image sizes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SizeCard(
                        label = "Original",
                        value = "${report.originalSizeKb} KB",
                        modifier = Modifier.weight(1f)
                    )
                    SizeCard(
                        label = "Compressed",
                        value = "${report.compressedSizeKb} KB",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Notes
                if (report.notes.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = report.notes,
                            modifier = Modifier.padding(10.dp),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SizeCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}