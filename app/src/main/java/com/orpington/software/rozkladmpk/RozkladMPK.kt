package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.dagger.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.slf4j.impl.HandroidLoggerAdapter

class RozkladMPK : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent
            .builder()
            .application(this)
            .baseUrl("http://192.168.0.22:8080")
            .build()
    }
}