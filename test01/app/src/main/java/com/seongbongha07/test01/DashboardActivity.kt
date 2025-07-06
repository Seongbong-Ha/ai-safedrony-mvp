package com.seongbongha07.test01  // ← 당신의 패키지명으로 바꿔야 합니다

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seongbongha07.test01.ui.theme.Test01Theme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test01Theme {
                DashboardTabScreen()
            }
        }
    }
}

@Composable
fun DashboardTabScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Status", "Camera", "Log")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> StatusScreen()
            1 -> CameraScreen()
            2 -> LogScreen()
        }
    }
}

@Composable
fun StatusScreen() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Status Info Here", fontSize = 20.sp)
    }
}

@Composable
fun CameraScreen() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Drone Camera View", fontSize = 20.sp)
    }
}

@Composable
fun LogScreen() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Activity Logs", fontSize = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    Test01Theme {
        DashboardTabScreen()
    }
}