package software.orpington.rozkladmpk.utils

import android.graphics.Color

class MapColoursHelper {
    private val lineColors = listOf<Int>(
        Color.parseColor("#E91E63"),
        Color.parseColor("#673AB7"),
        Color.parseColor("#2196F3"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#CDDC39"),
        Color.parseColor("#FFC107"),
        Color.parseColor("#FF5722"),
        Color.parseColor("#9E9E9E")
    )

    private var colorCounter = 0
    fun getNextColor(): Int {
        val color = lineColors[colorCounter]

        colorCounter = (colorCounter + 1) % lineColors.size

        return color
    }

    fun goBack() {
        if (colorCounter == 0) return
        colorCounter = (colorCounter - 1) % lineColors.size
    }

    fun resetCounter() {
        colorCounter = 0
    }
}