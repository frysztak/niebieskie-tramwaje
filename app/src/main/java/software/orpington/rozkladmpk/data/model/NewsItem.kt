package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NewsItem(
    @Json(name = "Url") val url: String,
    @Json(name = "Title") val title: String,
    @Json(name = "PublishedOn") val publishedOn: String,
    @Json(name = "Synopsis") val synopsis: String,
    @Json(name = "AffectsLines") val affectsLines: String,
    @Json(name = "AffectsDay") val affectsDay: String,
    @Json(name = "Body") val body: String
)