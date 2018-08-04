package com.orpington.software.rozkladmpk.dagger

import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface NetworkComponent {
    fun inject(presenter: StopsAndRoutesPresenter)
}

