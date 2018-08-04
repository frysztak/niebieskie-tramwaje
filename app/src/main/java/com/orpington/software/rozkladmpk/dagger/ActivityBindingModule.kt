package com.orpington.software.rozkladmpk.dagger


import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(StopsAndRoutesModule::class))
    internal abstract fun stopsAndRoutesActivity(): StopsAndRoutesActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(RouteVariantsModule::class))
    internal abstract fun routeVariantsActivity(): RouteVariantsActivity
}
