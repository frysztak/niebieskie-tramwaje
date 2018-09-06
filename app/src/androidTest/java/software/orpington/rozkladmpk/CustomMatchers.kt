package software.orpington.rozkladmpk

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher

//https://medium.com/@taingmeng/custom-recyclerview-matcher-and-viewassertion-with-espresso-kotlin-45845c64ab44
class CustomMatchers {
    companion object {
        fun withItemCount(count: Int): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

                override fun describeTo(description: Description?) {
                    description?.appendText("RecyclerView with item count: $count")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    return item?.adapter?.itemCount == count
                }

                override fun describeMismatch(item: Any?, description: Description?) {
                    if (item is RecyclerView?) {
                        val count = item?.adapter?.itemCount
                        description?.appendText("RecyclerView has $count items")
                    }
                    description?.appendText("Item is not RecyclerView")
                }
            }
        }
    }
}