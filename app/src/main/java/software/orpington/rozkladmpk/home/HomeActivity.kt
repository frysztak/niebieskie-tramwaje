package software.orpington.rozkladmpk.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem
import software.orpington.rozkladmpk.home.map.MapFragment
import software.orpington.rozkladmpk.home.newsList.NewsListFragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val pageID = when (savedInstanceState) {
            null -> R.id.navigation_home
            else -> savedInstanceState.getInt("pageID", R.id.navigation_home)
        }
        swapPageContent(pageID)
        navigation.setOnNavigationItemSelectedListener {
            swapPageContent(it.itemId)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("pageID", navigation.selectedItemId)
    }

    private fun getFragmentTag(pageID: Int): String {
        return when (pageID) {
            R.id.navigation_home -> "HomeFragment"
            R.id.navigation_map -> "MapFragment"
            R.id.navigation_news -> "NewsFragment"
            else -> "UnknownFragment"
        }
    }

    private val changePageCallback = object : ChangePageCallback {
        override fun showNewsPage(news: NewsItem) {
            swapPageContent(R.id.navigation_news, news)
            navigation.selectedItemId = R.id.navigation_news
        }
    }

    private fun createNewFragment(pageID: Int): Fragment {
        return when (pageID) {
            R.id.navigation_home -> HomeFragment.newInstance(changePageCallback)
            R.id.navigation_map -> MapFragment.newInstance()
            else -> NewsListFragment.newInstance()
        }
    }

    private var currentPageIndex: Int = -1
    private fun swapPageContent(pageID: Int, news: NewsItem? = null) {
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

        if (news != null && pageID == R.id.navigation_news) {
            val newsListFragment = fragment as NewsListFragment? ?: return
            supportFragmentManager.executePendingTransactions()
            newsListFragment.showDetail(news)
        }
    }
}
