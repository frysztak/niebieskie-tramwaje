package com.orpington.software.rozkladmpk.data.source.remote

import android.util.Log
import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.IDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RemoteDataSource private constructor(private val service: ApiService) : IRemoteDataSource {

    override fun getStopNames(callback: IDataSource.LoadDataCallback<StopNames>) {
        val articleResponseCall = service.getStops()
        articleResponseCall.enqueue(object : Callback<StopNames> {
            override fun onResponse(call: Call<StopNames>, response: Response<StopNames>) {
                if (response.isSuccessful) {
                    val stopNames = response.body()
                    stopNames?.let {
                        callback.onDataLoaded(it) }
                    ?: run {
                        Log.e(LOG_TAG, "Oops, something went wrong!")
                    }
                } else {
                    Log.e(LOG_TAG, "Oops, something went wrong!")
                }
            }

            override fun onFailure(call: Call<StopNames>, t: Throwable) {
                Log.e(LOG_TAG, "Error:" + t.message)
            }
        })

    }

    /*
    fun getArticles(source: String, callback: IDataSource.LoadDataCallback<Article>) {
        val articleResponseCall = service.getArticle(BuildConfig.API_KEY, source, "top")
        articleResponseCall.enqueue(object : Callback<ArticleResponse>() {
            fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.body() != null) {
                    if ("ok" == response.body().getStatus()) {
                        if (!response.body().getArticles().isEmpty()) {
                            callback.onDataLoaded(response.body().getArticles())
                        } else {
                            callback.onDataNotAvailable()
                            Log.e(LOG_TAG, "Oops, something went wrong!")
                        }
                    } else {
                        callback.onDataNotAvailable()
                        Log.e(LOG_TAG, "Oops, something went wrong!")
                    }
                }
            }

            fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.e(LOG_TAG, "Error:" + t.message)
                callback.onDataNotAvailable()
            }
        })

    }
    */

    companion object {

        private val LOG_TAG = RemoteDataSource::class.java.simpleName

        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(service: ApiService): RemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = RemoteDataSource(service)
            }
            return INSTANCE!!
        }
    }
}
