package com.orpington.software.rozkladmpk.dagger

import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsPresenter
import dagger.Module
import dagger.Provides

@Module
class RouteVariantsModule {
    @ActivityScope
    @Provides
    fun providerPresenter(dataSource: RemoteDataSource): RouteVariantsPresenter {
        return RouteVariantsPresenter(dataSource)
    }
}
