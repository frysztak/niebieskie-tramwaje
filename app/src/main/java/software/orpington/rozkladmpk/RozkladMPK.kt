package software.orpington.rozkladmpk

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import org.slf4j.impl.HandroidLoggerAdapter

class RozkladMPK : Application() {
    override fun onCreate() {
        super.onCreate()
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }
    }
}