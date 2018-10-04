package software.orpington.rozkladmpk.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.home.map.MapFragment
import software.orpington.rozkladmpk.home.newsList.NewsListFragment

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

    private fun getFragmentTag(pageID: Int): String {
        return when (pageID) {
            R.id.navigation_home -> "HomeFragment"
            R.id.navigation_map -> "MapFragment"
            R.id.navigation_news -> "NewsFragment"
            else -> "UnknownFragment"
        }
    }

    private fun createNewFragment(pageID: Int): Fragment {
        return when (pageID) {
            R.id.navigation_home -> HomeFragment.newInstance()
            R.id.navigation_map -> MapFragment.newInstance()
            else -> NewsListFragment.newInstance()
        }
    }

    private var currentPageIndex: Int = -1
    private fun swapPageContent(pageID: Int) {
        if (pageID == currentPageIndex) return

        // based on https://stackoverflow.com/a/45301078
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        val fragmentTag = getFragmentTag(pageID)
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = createNewFragment(pageID)
            fragmentTransaction.add(R.id.frameLayout, fragment, fragmentTag)
        } else {
            fragmentTransaction.show(fragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commit()

        currentPageIndex = pageID
    }
}
