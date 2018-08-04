package com.orpington.software.rozkladmpk.dagger

import android.app.Application
import com.orpington.software.rozkladmpk.RozkladMPK
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class,
    AppModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])

interface AppComponent : AndroidInjector<RozkladMPK> {

    fun getRemoteDataSource(): RemoteDataSource

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        @BindsInstance
        fun baseUrl(@Named("baseUrl") baseUrl: String): Builder

        fun build(): AppComponent
    }

}