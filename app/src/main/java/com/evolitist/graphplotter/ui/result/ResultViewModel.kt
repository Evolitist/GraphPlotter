package com.evolitist.graphplotter.ui.result

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.graphplotter.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ResultViewModel(val points: Array<Point>) : ViewModel() {
    inline fun saveImage(context: Context, bitmap: Bitmap, crossinline callback: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val name = "graph_${SimpleDateFormat(
                "dd-MM-yyyy_HH-mm-ss",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)}"
            val out = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Graphs")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@launch
                resolver.openOutputStream(imageUri) ?: return@launch
            } else {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Graphs").run {
                    mkdirs()
                    val image = File(this, "$name.png")
                    FileOutputStream(image)
                }
            }
            out.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            launch(Dispatchers.Main) {
                callback()
            }
        }
}
