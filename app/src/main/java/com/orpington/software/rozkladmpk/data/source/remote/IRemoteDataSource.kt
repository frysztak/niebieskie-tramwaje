package com.orpington.software.rozkladmpk.data.source.remote

import com.orpington.software.rozkladmpk.data.source.IDataSource.LoadDataCallback
import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.IDataSource


interface IRemoteDataSource : IDataSource {

    fun getStopNames(callback: LoadDataCallback<StopNames>)

    //fun getArticles(source: String, callback: LoadDataCallback<Article>)
}