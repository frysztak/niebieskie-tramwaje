package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.StopNames


interface RepositoryDataSource {
    fun getAllStopNames(callback: IDataSource.LoadDataCallback<StopNames>,
                        isNetworkAvailable: Boolean)

    //fun saveArticles(articles: List<Article>)

    //fun deleteAllArticles()
}