package com.seongbongha07.test01

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seongbongha07.test01.ui.theme.Test01Theme

/*──────── Activity ────────*/
class EduUIActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Test01Theme { SafetyEducationScreen() } }
    }
}

/*──────── Screen ────────*/
@Composable
fun SafetyEducationScreen() {
    Scaffold(
        topBar = { EduTopBar() },
        bottomBar = { EduBottomNav() }      // ← 하단 네비게이션 추가
    ) { inner -> MainContent(Modifier.padding(inner)) }
}

/*──────── Top Bar ────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduTopBar() {                         // ← 이름 변경
    val context = LocalContext.current
    TopAppBar(
        title = { Text("Safety Education") },
        navigationIcon = {
            IconButton(onClick = { (context as? Activity)?.finish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* 설정 */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

/*──────── Main Content ────────*/
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        MainImage()
        Spacer(Modifier.height(24.dp))
        HazardList()
        Spacer(Modifier.height(24.dp))
        DescriptionAndButton()
    }
}

/*──────── UI Components ────────*/
@Composable
fun MainImage() {
    Image(
        painter = painterResource(R.drawable.construction_site_image),
        contentDescription = "Construction Site",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

data class HazardItem(val title: String, val description: String)

@Composable
fun HazardList() {
    val items = listOf(
        HazardItem("추락 위험", "난간 없는 개방 구역"),
        HazardItem("걸림·넘어짐 위험", "흩어져 있는 자재"),
        HazardItem("안전 구역", "지정된 안전 구역")
    )
    Column {
        items.forEach { item ->
            HazardRow(item)
            Divider(Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun HazardRow(item: HazardItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            item.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            item.description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DescriptionAndButton() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "드론이 다양한 위험 요소를 실시간으로 식별하고 표시하여 현장의 안전 의식을 높입니다.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
//        Button(
//            onClick = { /* 스캔 시작 */ },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Start Scan", Modifier.padding(8.dp))
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun EduPreview() {
    Test01Theme { SafetyEducationScreen() }
}

/*──────── Bottom Navigation ────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduBottomNav() {
    val context = LocalContext.current
    val items = listOf(
        R.drawable.home_24px to "Home",
        R.drawable.photo_scan_24px to "Scan",
        R.drawable.report_24px to "Report",
        R.drawable.settings_24px to "Settings"
    )
    val selected = 2   // “Report / Education” 탭이 현재 화면이라고 가정

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { idx, (iconRes, label) ->
            NavigationBarItem(
                selected = idx == selected,
                icon = { Icon(painterResource(iconRes), contentDescription = label) },
                label = { Text(label) },
                onClick = {
                    when (idx) {
                        0 -> context.startActivity(Intent(context, MainActivity::class.java))
                        1 -> context.startActivity(Intent(context, SafetyScanActivity::class.java))
                        2 -> { /* 현재 화면 */
                        }

                        3 -> context.startActivity(Intent(context, DashboardActivity::class.java))
                    }
                }
            )
        }
    }
}
