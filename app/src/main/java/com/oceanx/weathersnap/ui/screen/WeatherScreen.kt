package com.oceanx.weathersnap.ui.screen

import androidx.compose.animation.togetherWith
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oceanx.weathersnap.data.model.CityResult
import com.oceanx.weathersnap.data.model.WeatherData
import com.oceanx.weathersnap.ui.components.GradientTopAppBar
import com.oceanx.weathersnap.ui.theme.AccentGreenYellow
import com.oceanx.weathersnap.ui.theme.AccentOrange
import com.oceanx.weathersnap.ui.theme.AccentTeal
import com.oceanx.weathersnap.ui.viewmodel.WeatherUiState
import com.oceanx.weathersnap.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onCreateReport: (WeatherData) -> Unit,
    onViewReports: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val query by viewModel.query.collectAsState()
    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = {
                    Column {
                        Text(
                            "WeatherSnap",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A1E0F)        // dark text on light bar
                        )
                        Text(
                            "Live weather reports with camera evidence",
                            fontSize = 10.sp,
                            color = Color(0xFF3A4A20)        // dark muted
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onViewReports,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF1A1E0F),   // dark button
                            contentColor = AccentGreenYellow
                        )
                    ) { Text("Reports") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search bar
            CitySearchSection(
                query = query,
                suggestions = suggestions,
                onQueryChange = { viewModel.onQueryChange(it) },
                onCitySelected = { viewModel.fetchWeather(it) }
            )

            // Weather content
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut()
                },
                label = "weather_state"
            ) { state ->
                when (state) {
                    is WeatherUiState.Idle -> IdleState()
                    is WeatherUiState.Loading -> LoadingState()
                    is WeatherUiState.Error -> ErrorState(state.message)
                    is WeatherUiState.Success -> WeatherSuccessContent(
                        weather = state.weather,
                        onCreateReport = onCreateReport
                    )
                }
            }
        }
    }
}

@Composable
private fun CitySearchSection(
    query: String,
    suggestions: List<CityResult>,
    onQueryChange: (String) -> Unit,
    onCitySelected: (CityResult) -> Unit
) {
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("City") },
            supportingText = { Text("Enter more than 2 letters to start city suggestions.") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        AnimatedVisibility(
            visible = suggestions.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(suggestions) { city ->
                        ListItem(
                            headlineContent = { Text(city.name, fontWeight = FontWeight.Medium) },
                            supportingContent = {
                                Text(
                                    buildString {
                                        city.state?.let { append("$it, ") }
                                        append(city.country ?: "")
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.clickable { onCitySelected(city) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherSuccessContent(
    weather: WeatherData,
    onCreateReport: (WeatherData) -> Unit   // changed
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WeatherCard(weather = weather)
        Button(
            onClick = { onCreateReport(weather) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50.dp),   // pill shape
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4E157),
                contentColor = Color(0xFF1A1E0F)
            )
        ) {
            Text(
                "Create Report",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun WeatherCard(weather: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252B14)
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
                        text = "${weather.cityName}, ${weather.country}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = weather.condition,
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp
                    )
                }
                // Temperature badge
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3A4A20)
                    )
                ) {
                    Text(
                        text = "${weather.temperature.toInt()}°C",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = AccentGreenYellow,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Humidity", "${weather.humidity}%",
                    AccentGreenYellow, Modifier.weight(1f))
                StatCard("Wind", "${weather.windSpeed} m/s",
                    AccentTeal, Modifier.weight(1f))
                StatCard("Pressure", "${weather.pressure.toInt()}",
                    AccentOrange, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1E0F)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 11.sp, color = Color(0xFFAAAAAA))
            Text(value, fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}

//@Composable
//private fun WeatherStatItem(label: String, value: String) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(
//            text = label,
//            fontSize = 12.sp,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Text(
//            text = value,
//            fontWeight = FontWeight.SemiBold,
//            fontSize = 14.sp,
//            color = MaterialTheme.colorScheme.primary
//        )
//    }
//}

@Composable
private fun IdleState() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = "Search for a city to see weather",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Error: $message",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}