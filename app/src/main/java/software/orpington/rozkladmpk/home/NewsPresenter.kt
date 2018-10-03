package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.data.model.NewsItem
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class NewsPresenter (
    private val remoteDataSource: RemoteDataSource
): NewsContract.Presenter {
    private var view: NewsContract.View? = null
    override fun attachView(view: NewsContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadMostRecentNews() {
        view?.showProgressBar()
        remoteDataSource.getMostRecentNews(object: IDataSource.LoadDataCallback<NewsItem> {
            override fun onDataLoaded(data: NewsItem) {
                view?.showMostRecentNews(data)
                view?.hideProgressBar()
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }
}