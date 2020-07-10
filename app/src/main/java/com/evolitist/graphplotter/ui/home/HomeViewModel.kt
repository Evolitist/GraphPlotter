package com.evolitist.graphplotter.ui.home

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.graphplotter.model.Point
import com.evolitist.graphplotter.model.Response
import com.evolitist.graphplotter.repository.PointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val inputText = MutableLiveData<String>()
    val points = MutableLiveData<Array<Point>>()

    suspend fun fetchPoints(count: Int): Array<Point> {
        val response = try {
            val r = PointRepository.pointsApi.getPoints(count).response
            if (!r.message.isNullOrEmpty()) {
                Response(null, String(Base64.decode(r.message, Base64.DEFAULT)))
            } else {
                r
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val pts = response?.points?.sortedBy { it.x }?.toTypedArray()
        points.postValue(pts)
        return if (!pts.isNullOrEmpty()) {
            pts
        } else if (!response?.message.isNullOrEmpty()) {
            throw Exception(response?.message)
        } else {
            throw Exception("Unknown error")
        }
    }

    inline fun getPoints(crossinline callback: (Array<Point>?, Exception?) -> Unit) =
        viewModelScope.launch(Dispatchers.Default) {
            val count = inputText.value?.toIntOrNull() ?: return@launch
            var exception: Exception? = null
            val points = try {
                fetchPoints(count)
            } catch (e: Exception) {
                exception = e
                null
            }
            launch(Dispatchers.Main) {
                callback(points, exception)
            }
        }
}