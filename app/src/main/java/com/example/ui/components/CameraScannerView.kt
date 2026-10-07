package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.Warga
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScannerView(
  wargaList: List<Warga>,
  onBarcodeDetected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { granted ->
      hasCameraPermission = granted
    }
  )

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  var camera by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }
  var manualCodeInput by remember { mutableStateOf("") }
  var lastScannedCode by remember { mutableStateOf<String?>(null) }
  var scanCooldown by remember { mutableStateOf(false) }

  // Scan line animation
  val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
  val laserPositionFraction by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Camera Viewport or Permission Request Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black),
      contentAlignment = Alignment.Center
    ) {
      if (hasCameraPermission) {
        AndroidView(
          factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            val cameraExecutor = Executors.newSingleThreadExecutor()

            cameraProviderFuture.addListener({
              try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                  it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                  .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                  .build()

                val reader = MultiFormatReader()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                  if (scanCooldown) {
                    imageProxy.close()
                    return@setAnalyzer
                  }

                  val mediaImage = imageProxy.image
                  if (mediaImage != null) {
                    val buffer = mediaImage.planes[0].buffer
                    val data = ByteArray(buffer.remaining())
                    buffer.get(data)
                    val width = imageProxy.width
                    val height = imageProxy.height

                    try {
                      val source = PlanarYUVLuminanceSource(
                        data, width, height,
                        0, 0, width, height, false
                      )
                      val bitmap = BinaryBitmap(HybridBinarizer(source))
                      val result = reader.decodeWithState(bitmap)
                      val detectedText = result.text
                      if (!detectedText.isNullOrBlank() && detectedText != lastScannedCode) {
                        lastScannedCode = detectedText
                        onBarcodeDetected(detectedText)
                      }
                    } catch (e: Exception) {
                      // Normal when no barcode is in frame
                    } finally {
                      reader.reset()
                      imageProxy.close()
                    }
                  } else {
                    imageProxy.close()
                  }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                  lifecycleOwner,
                  cameraSelector,
                  preview,
                  imageAnalysis
                )
              } catch (e: Exception) {
                e.printStackTrace()
              }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
          },
          modifier = Modifier.fillMaxSize()
        )

        // Overlay with scanning frame & laser
        Box(
          modifier = Modifier
            .size(230.dp, 200.dp)
            .border(
              width = 2.dp,
              color = MaterialTheme.colorScheme.primary,
              shape = RoundedCornerShape(12.dp)
            )
        ) {
          // Animated red/green scan laser line
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp)
              .offset {
                IntOffset(
                  x = 0,
                  y = (laserPositionFraction * 194.dp.toPx()).roundToInt()
                )
              }
              .background(
                Brush.horizontalGradient(
                  listOf(Color.Transparent, Color(0xFF10B981), Color.Transparent)
                )
              )
          )
        }

        // Torch toggle button
        IconButton(
          onClick = {
            camera?.let { cam ->
              val newState = !isTorchOn
              cam.cameraControl.enableTorch(newState)
              isTorchOn = newState
            }
          },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
            .size(44.dp)
            .testTag("torch_toggle_button"),
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.6f),
            contentColor = if (isTorchOn) Color(0xFFFBBF24) else Color.White
          )
        ) {
          Icon(
            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = if (isTorchOn) "Matikan Lampu Senter" else "Nyalakan Lampu Senter"
          )
        }

        // Subtitle badge inside viewfinder
        Surface(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp),
          color = Color.Black.copy(alpha = 0.7f),
          shape = CircleShape
        ) {
          Text(
            text = "Arahkan kamera ke Barcode / QR Kartu Warga",
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
          )
        }
      } else {
        // No permission state
        Column(
          modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Izin Kamera",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Izin Kamera Diperlukan untuk Scan Barcode",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Aktifkan kamera agar pendataan jimpitan ronda berjalan otomatis dan cepat.",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(14.dp))
          Button(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("grant_camera_permission_button")
          ) {
            Text("Izinkan Akses Kamera")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Manual input & quick simulation panel
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
      shape = RoundedCornerShape(12.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = manualCodeInput,
            onValueChange = { manualCodeInput = it },
            placeholder = { Text("Ketik ID / Kode Barcode...", fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("manual_barcode_input"),
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
              Icon(Icons.Default.QrCode, contentDescription = "ID Warga")
            }
          )
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (manualCodeInput.isNotBlank()) {
                onBarcodeDetected(manualCodeInput.trim())
                manualCodeInput = ""
              }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .height(56.dp)
              .testTag("search_barcode_button")
          ) {
            Icon(Icons.Default.Search, contentDescription = "Cari Warga")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Simulasi Cepat (Klik untuk Scan Otomatis):",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick simulation chips from warga list
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          wargaList.take(8).forEach { warga ->
            SuggestionChip(
              onClick = {
                onBarcodeDetected(warga.barcodeData)
              },
              label = {
                Text("${warga.name.take(14)} (${warga.houseNumber.take(7)})", fontSize = 11.sp)
              },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surface
              ),
              modifier = Modifier.testTag("chip_scan_${warga.id}")
            )
          }
        }
      }
    }
  }
}
