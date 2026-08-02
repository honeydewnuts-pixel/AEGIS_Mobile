package com.aegis.mobile.capture

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aegis.mobile.models.AnalysisResponse
import com.aegis.mobile.network.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ScreenCaptureService : Service() {

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var apiService: com.aegis.mobile.network.ApiService

    companion object {
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"
        private const val NOTIF_ID = 1001
        private const val CAPTURE_INTERVAL = 3000L // 3 seconds
    }

    override fun onCreate() {
        super.onCreate()
        apiService = RetrofitClient.getApiService(this)
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startForeground(NOTIF_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0)?: 0
        val resultData = intent?.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)?: return START_NOT_STICKY

        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)
        setupVirtualDisplay()
        handler.postDelayed(captureRunnable, CAPTURE_INTERVAL)
        return START_STICKY
    }

    private fun setupVirtualDisplay() {
        val metrics = resources.displayMetrics
        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "AEGIS_ScreenCapture",
            metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
    }

    private val captureRunnable = object : Runnable {
        override fun run() {
            captureAndSend()
            handler.postDelayed(this, CAPTURE_INTERVAL)
        }
    }

    private fun captureAndSend() {
        val image = imageReader?.acquireLatestImage()?: return
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        scope.launch {
            sendBitmapToBrain(bitmap)
        }
    }

    private suspend fun sendBitmapToBrain(bitmap: Bitmap) {
        try {
            val file = File(cacheDir, "screenshot.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = apiService.analyzeScreenshot(body)
            if (response.isSuccessful) {
                val result: AnalysisResponse? = response.body()
                Log.d("AEGIS", "Brain Response: ${result?.signal} - ${result?.confidence}")
                // TODO: Send result to UI via Broadcast/LiveData
            } else {
                Log.e("AEGIS", "Brain Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AEGIS", "Send failed: ${e.message}")
        }
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel("aegis_service", "AEGIS Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, "aegis_service")
           .setContentTitle("AEGIS Active")
           .setContentText("Analyzing screen every 3 seconds...")
           .setSmallIcon(android.R.drawable.ic_menu_camera)
           .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(captureRunnable)
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
