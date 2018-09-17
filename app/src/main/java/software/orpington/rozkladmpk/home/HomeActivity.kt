package software.orpington.rozkladmpk.home

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import software.orpington.rozkladmpk.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener {
            val fragment: Fragment =
                when (it.itemId) {
                0 -> HomeFragment.newInstance()
                else -> HomeFragment.newInstance()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()
            true
        }
    }
}
