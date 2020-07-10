package com.evolitist.graphplotter.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class ResponseOuter(val result: Int = 0, val response: Response = Response(null, ""))

@JsonClass(generateAdapter = true)
data class Response(val points: List<Point>?, val message: String?)

@Parcelize
@JsonClass(generateAdapter = true)
data class Point(val x: Float, val y: Float) : Parcelable
