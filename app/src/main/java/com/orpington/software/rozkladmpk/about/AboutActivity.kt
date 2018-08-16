package com.orpington.software.rozkladmpk.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.orpington.software.rozkladmpk.BuildConfig
import com.orpington.software.rozkladmpk.R
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.about)

        LibsConfiguration.getInstance().uiListener = object : LibsConfiguration.LibsUIListener {
            override fun postOnCreateView(view: View): View {
                val recyclerView = view.findViewById<RecyclerView>(R.id.cardListView)
                recyclerView.isNestedScrollingEnabled = false
                return view
            }

            override fun preOnCreateView(view: View): View {
                return view
            }
        }
        val librariesFragment = LibsBuilder().fragment()
        fragmentManager
            .beginTransaction()
            .replace(libraries.id, librariesFragment)
            .commit()

        val versionString = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
        appVersion.text = versionString
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}
