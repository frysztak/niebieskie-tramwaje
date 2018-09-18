package software.orpington.rozkladmpk.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import software.orpington.rozkladmpk.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        swapPageContent(R.id.navigation_home)
        navigation.setOnNavigationItemSelectedListener {
            swapPageContent(it.itemId)
            true
        }
    }

    private var currentPageIndex: Int = -1
    private fun swapPageContent(pageIdx: Int) {
        if (pageIdx == currentPageIndex) return

        val fragment: Fragment =
            when (pageIdx) {
                R.id.navigation_home -> HomeFragment.newInstance()
                else -> HomeFragment.newInstance()
            }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        currentPageIndex = pageIdx
    }
}
