package software.orpington.rozkladmpk.utils

import android.graphics.Color
import java.util.*

class MapColoursHelper {
    private val lineColours = listOf<Int>(
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

    // marks used colours
    private val coloursMap: MutableMap<Int, Boolean>

    init {
        val map: MutableMap<Int, Boolean> = mutableMapOf()
        for (colour in lineColours) {
            map[colour] = false
        }

        coloursMap = map
    }

    fun getNextColor(): Int {
        // find first unused colour
        for ((colour, isUsed) in coloursMap) {
            if (!isUsed) {
                coloursMap[colour] = true
                return colour
            }
        }

        // all colours are taken. return a random one
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun releaseColour(colour: Int) {
        if (coloursMap.containsKey(colour)) {
            coloursMap[colour] = false
        }
    }

    fun releaseAllColours() {
        for ((k, _) in coloursMap) {
            coloursMap[k] = false
        }
    }
}