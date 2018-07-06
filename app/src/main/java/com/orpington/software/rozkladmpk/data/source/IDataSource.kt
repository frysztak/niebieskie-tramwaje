package com.orpington.software.rozkladmpk.data.source

interface IDataSource {

    interface LoadDataCallback<T> {

        fun onDataLoaded(data: T)

        fun onDataNotAvailable()
    }
}
