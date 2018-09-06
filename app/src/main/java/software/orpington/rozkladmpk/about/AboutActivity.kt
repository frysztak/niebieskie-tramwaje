package software.orpington.rozkladmpk.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import software.orpington.rozkladmpk.BuildConfig
import software.orpington.rozkladmpk.R
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.about)

        val versionString = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
        appVersion.text = versionString

        licensesButton.setOnClickListener {
            val dialog = LicensesDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "LicensesDialog")
        }
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
