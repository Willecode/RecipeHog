package com.portfolio.core.presentation.designsystem.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.portfolio.core.presentation.designsystem.BackIcon
import com.portfolio.core.presentation.designsystem.R
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

/**
 * Assumes that camera permissions have been granted
 * TODO: Add support for landscape orientation
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onPhotoTaken: (Bitmap) -> Unit,
    onCancel: () -> Unit,
    onError: () -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FIT_CENTER }
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    AndroidView(factory = { previewView }, modifier = modifier.fillMaxSize())
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                takePicture(imageCapture, context, onPhotoTaken, onError)

            },
            shape = CircleShape,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .size(75.dp)
        ) {
        }
    }
    BackButtonBox(onCancel)
    FrameGuide()
}

@Composable
private fun FrameGuide() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(3.dp, MaterialTheme.colorScheme.primary)
        ) {
        }
    }
}

@Composable
private fun BackButtonBox(onCancel: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        IconButton(
            onClick = { onCancel() },
            colors = IconButtonDefaults.iconButtonColors()
                .copy(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = BackIcon,
                contentDescription = stringResource(id = R.string.navigate_back)
            )
        }
    }
}

private fun takePicture(
    imageCapture: ImageCapture,
    context: Context,
    onPhotoTaken: (Bitmap) -> Unit,
    onError: () -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = toCroppedSquareBitmap(image = image, rotationDegree = 90f)
                if (bitmap == null) {
                    onError()
                } else {
                    onPhotoTaken(bitmap)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onError()
            }

        })
}

private fun toCroppedSquareBitmap(image: ImageProxy, rotationDegree: Float): Bitmap? {

    val retBitmap = try {
        val bitmap = image.toBitmap()
        val minDimension = min(bitmap.width, bitmap.height)
        val cropped = Bitmap.createBitmap(bitmap, 0, 0, minDimension, minDimension)

        if (rotationDegree != 0f) {
            val rotationMatrix = Matrix()
            rotationMatrix.postRotate(rotationDegree)
            Bitmap.createBitmap(cropped, 0, 0, cropped.width, cropped.height, rotationMatrix, true)
        } else cropped
    } catch (e: IllegalArgumentException) {
        return null
    }

    return retBitmap
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraPreviewPreview() {
    RecipeHogTheme {
        Surface {
            CameraPreview(onPhotoTaken = {}, onCancel = { /*TODO*/ }) {

            }
        }
    }
}