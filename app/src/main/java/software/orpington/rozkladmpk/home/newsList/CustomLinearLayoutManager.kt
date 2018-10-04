package software.orpington.rozkladmpk.home.newsList

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context?): LinearLayoutManager(context) {

    private var scrollEnabled = true
    fun setScrollEnabled(flag: Boolean) {
        scrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return scrollEnabled && super.canScrollVertically()
    }
}