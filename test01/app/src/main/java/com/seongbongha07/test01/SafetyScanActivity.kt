package com.seongbongha07.test01

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.seongbongha07.test01.tts.TTSManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import kotlin.math.exp

/*────────────────── Activity ──────────────────*/
class SafetyScanActivity : ComponentActivity() {
    private val CAMERA_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchUi()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun launchUi() = setContent { SafetyScanScreen() }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) launchUi() else finish()
    }
}

/*────────────────── UI ──────────────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScanScreen() {
    val context = LocalContext.current
    val ttsManager = remember { TTSManager(context) }
    var helmetOk by remember { mutableStateOf<Boolean?>(null) }
    var lastSpoken by remember { mutableStateOf<Boolean?>(null) }

    /* TTS once per state change */
    LaunchedEffect(helmetOk) {
        helmetOk?.let { curr ->
            if (curr != lastSpoken) {
                val msg = if (curr) "헬멧을 착용했습니다." else "헬멧을 착용하지 않았습니다."
                ttsManager.speakOut(msg)
                lastSpoken = curr
            }
        }
    }

    Scaffold(topBar = { TopBar() }, bottomBar = { BottomNav() }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Safety Scan", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            CameraPreview(
                onResult = { helmetOk = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(24.dp))
            Text(
                when (helmetOk) {
                    null -> "Detecting…"
                    true -> "Helmet detected – all good!"
                    false -> "Helmet not detected – please wear immediately!"
                },
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = when (helmetOk) {
                    true -> Color(0xFF4CAF50)
                    false -> Color.Red
                    else -> Color.Black
                }
            )
            Spacer(Modifier.height(32.dp))
            val context = LocalContext.current          // ← 화면 맨 위에서 이미 쓰고 있으니 재사용

            Button(
                onClick = {
                    context.startActivity(               // ← context로 바로 startActivity
                        Intent(context, EduUIActivity::class.java)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0F0F0),
                    contentColor   = Color.Black
                )
            ) {
                Text("Report Issue")
            }
        }
    }
}

/*────────────────── TopBar ──────────────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { (context as? Activity)?.finish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Safedrony", fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = { context.startActivity(Intent(context, DashboardActivity::class.java)) }) {
                Icon(painterResource(R.drawable.settings_24px), contentDescription = "Settings")
            }
        }
    )
}

/*────────────────── BottomNav ──────────────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav() {
    val context = LocalContext.current
    val items: List<Pair<Int, String>> = listOf(
        R.drawable.home_24px to "Home",
        R.drawable.photo_scan_24px to "Scan",
        R.drawable.report_24px to "Report",
        R.drawable.settings_24px to "Settings"
    )
    val selectedIndex = 1
    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { idx, (iconRes, label) ->
            NavigationBarItem(
                selected = idx == selectedIndex,
                onClick = {
                    when (idx) {
                        0 -> context.startActivity(Intent(context, MainActivity::class.java))
                        1 -> {}
                        2 -> context.startActivity(Intent(context, WorkerboardActivity::class.java))
                        3 -> context.startActivity(Intent(context, DashboardActivity::class.java))
                    }
                },
                icon = { Icon(painterResource(iconRes), contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

/*────────────────── Camera + Model ──────────────────*/
@Composable
fun CameraPreview(onResult: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Interpreter lazy-load once
    val interpreter by remember {
        mutableStateOf(Interpreter(FileUtil.loadMappedFile(context, "best_float32.tflite")))
    }
    val exec = remember { Executors.newSingleThreadExecutor() }

    AndroidView(factory = { ctx ->
        val view = PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
        ProcessCameraProvider.getInstance(ctx).addListener({
            val provider = ProcessCameraProvider.getInstance(ctx).get()
            val preview = Preview.Builder().build().apply { setSurfaceProvider(view.surfaceProvider) }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(exec) { proxy ->
                processFrame(interpreter, proxy) { hasHelmet ->
                    Handler(Looper.getMainLooper()).post { onResult(hasHelmet) }
                }
            }
            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(ctx))
        view
    }, modifier = modifier)
}

/*──────── Frame → Bool helper ───────*/
private fun processFrame(
    interpreter: Interpreter,
    imageProxy: ImageProxy,
    cb: (Boolean) -> Unit
) {
    try {
        // 1) ImageProxy(YUV) ▶ Bitmap(RGB)
        val rot = imageProxy.imageInfo.rotationDegrees
        val bm0 = imageProxy.toBitmap()
        val bm = if (rot != 0) {
            Bitmap.createBitmap(
                bm0, 0, 0, bm0.width, bm0.height,
                Matrix().apply { postRotate(rot.toFloat()) }, true
            )
        } else bm0

        // 2) Bitmap ▶ ByteBuffer(NCHW 640×640×3)
        val input = bitmapToNCHW(bm)

        // 3) 모델 출력 버퍼 [1,300,6]
        val output = Array(1) { Array(300) { FloatArray(6) } }
        interpreter.run(input, output)

        // 4) 박스 통과 조건
        var helmet = false
        output[0].forEach { box ->
            val obj = sigmoid(box[4])
            val w = box[2]; val h = box[3]
            Log.d("HelmetDebug", "obj=$obj w=$w h=$h")
            if (obj >= 0.55f && w * h >= 0.02f && w <= 0.9f && h <= 0.9f) {
                helmet = true
            }
        }
        cb(helmet)
    } finally {
        imageProxy.close()
    }
}

/*──────── Util funcs ───────*/
private fun bitmapToNCHW(bm: Bitmap): ByteBuffer {
    val w = 640; val h = 640; val c = 3
    val buf = ByteBuffer.allocateDirect(c * w * h * 4).order(ByteOrder.nativeOrder())
    val rs = Bitmap.createScaledBitmap(bm, w, h, true)
    val px = IntArray(w * h); rs.getPixels(px, 0, w, 0, 0, w, h)
    for (ch in 0 until c)
        for (p in px) buf.putFloat(
            when (ch) {
                0 -> ((p shr 16) and 0xFF) / 255f
                1 -> ((p shr 8) and 0xFF) / 255f
                else -> (p and 0xFF) / 255f
            }
        )
    return buf
}

private fun sigmoid(x: Float) = (1f / (1f + exp(-x)))
