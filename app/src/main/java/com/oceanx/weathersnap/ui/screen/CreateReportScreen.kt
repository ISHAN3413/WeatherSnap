package com.oceanx.weathersnap.ui.screen

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import coil.compose.rememberAsyncImagePainter
import com.oceanx.weathersnap.data.model.WeatherData
import com.oceanx.weathersnap.ui.components.GradientTopAppBar
import com.oceanx.weathersnap.ui.theme.AccentGreenYellow
import com.oceanx.weathersnap.ui.theme.OliveGreenDark
import com.oceanx.weathersnap.ui.viewmodel.ReportUiState
import com.oceanx.weathersnap.ui.viewmodel.ReportViewModel
import com.oceanx.weathersnap.ui.viewmodel.SharedWeatherViewModel
import com.oceanx.weathersnap.util.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    reportViewModel: ReportViewModel,
    sharedWeatherViewModel: SharedWeatherViewModel,
    savedStateHandle: SavedStateHandle,
    onOpenCamera: () -> Unit,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val weather by sharedWeatherViewModel.selectedWeather.collectAsState()
    val uiState by reportViewModel.uiState.collectAsState()
    val imagePath by reportViewModel.imagePath.collectAsState()
    val originalKb by reportViewModel.originalKb.collectAsState()
    val compressedKb by reportViewModel.compressedKb.collectAsState()

    var notes by remember { mutableStateOf("") }

    // Observe image path returned from Camera screen
    val returnedPath = savedStateHandle.getStateFlow<String?>("imagePath", null)
        .collectAsState()

    LaunchedEffect(returnedPath.value) {
        returnedPath.value?.let { path ->
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    ImageCompressor.compress(context, path)
                }
                reportViewModel.setImage(
                    path = result.compressedPath,
                    originalKb = result.originalKb,
                    compressedKb = result.compressedKb
                )
                savedStateHandle["imagePath"] = null
            }
        }
    }

    // Navigate after save
    LaunchedEffect(uiState) {
        if (uiState is ReportUiState.Saved) {
            reportViewModel.resetState()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = {
                    Column {
                        Text(
                            "Create Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A1E0F)
                        )
                        Text(
                            "Capture, compress, annotate",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weather snapshot
            weather?.let { WeatherSnapshotCard(it) }

            // Photo preview
            PhotoPreviewSection(
                imagePath = imagePath,
                originalKb = originalKb,
                compressedKb = compressedKb,
                onCaptureClick = onOpenCamera
            )

            // Notes
            NotesSection(
                notes = notes,
                onNotesChange = { notes = it }
            )

            // Error
            if (uiState is ReportUiState.Error) {
                Text(
                    text = (uiState as ReportUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            // Save button
            Button(
                onClick = {
                    weather?.let { reportViewModel.saveReport(it, notes) }
                },
                enabled = uiState !is ReportUiState.Saving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is ReportUiState.Saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Report", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun WeatherSnapshotCard(weather: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${weather.cityName}, ${weather.country}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        weather.condition,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${weather.temperature.toInt()}°C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherStat("Humidity", "${weather.humidity}%")
                WeatherStat("Wind", "${weather.windSpeed} m/s")
                WeatherStat("Pressure", "${weather.pressure.toInt()} hPa")
            }
        }
    }
}

@Composable
private fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PhotoPreviewSection(
    imagePath: String?,
    originalKb: Long,
    compressedKb: Long,
    onCaptureClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = OliveGreenDark
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedContent(
                targetState = imagePath,
                transitionSpec = { fadeIn() + scaleIn(initialScale = 0.9f) togetherWith fadeOut() },
                label = "photo_preview"
            ) { path ->
                if (path != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(path),
                            contentDescription = "Captured photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Size info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SizeChip(label = "Original", value = "${originalKb} KB",
                                modifier = Modifier.weight(1f))
                            SizeChip(label = "Compressed", value = "${compressedKb} KB",
                                modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Photo preview",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCaptureClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (imagePath != null) "Retake Photo" else "Capture Photo")
            }
        }
    }
}

@Composable
private fun SizeChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun NotesSection(notes: String, onNotesChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Field Notes", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}