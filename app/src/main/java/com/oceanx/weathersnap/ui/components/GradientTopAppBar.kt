package com.oceanx.weathersnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oceanx.weathersnap.ui.theme.AccentGreenYellow
import com.oceanx.weathersnap.ui.theme.ButtonGreenYellow
import com.oceanx.weathersnap.ui.theme.DarkBackground
import com.oceanx.weathersnap.ui.theme.OliveContainer

val appBarGradient = Brush.horizontalGradient(
    colors = listOf(
        AccentGreenYellow,
        ButtonGreenYellow,
        OliveContainer,
        DarkBackground
    )
)

@Composable
fun GradientTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(appBarGradient)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            title()
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}
@Preview
@Composable
fun gd(){
    GradientTopAppBar(
        modifier = Modifier.padding(5.dp),
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
                    fontSize = 8.sp,
                    color = Color(0xFF3A4A20)        // dark muted
                )
            }
        },
        actions = {
            FilledTonalButton(
                onClick = {},
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF1A1E0F),   // dark button
                    contentColor = AccentGreenYellow
                )
            ) { Text("Reports") }
        }
    )
}

