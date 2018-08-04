package com.orpington.software.rozkladmpk.dagger

import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesPresenter
import dagger.Module
import dagger.Provides

@Module
class StopsAndRoutesModule {
    @ActivityScope
    @Provides
    fun providerPresenter(dataSource: RemoteDataSource): StopsAndRoutesPresenter {
        return StopsAndRoutesPresenter(dataSource)
    }
}

