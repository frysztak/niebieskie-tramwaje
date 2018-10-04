package software.orpington.rozkladmpk.home.newsList

import software.orpington.rozkladmpk.data.model.NewsItem
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class NewsListPresenter(
    private val remoteDataSource: RemoteDataSource
) : NewsListContract.Presenter {
    private var view: NewsListContract.View? = null
    override fun attachView(view: NewsListContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private val items: MutableList<NewsItem> = mutableListOf()
    private var lastPage = 0

    @set:Synchronized
    @get:Synchronized
    private var requestInProgress = false

    @set:Synchronized
    @get:Synchronized
    private var requestFailed = false

    override fun getNextPage() {
        if (requestInProgress || requestFailed) return

        requestInProgress = true
        view?.showProgressBar()
        remoteDataSource.getNews(lastPage, object : IDataSource.LoadDataCallback<List<NewsItem>> {
            override fun onDataLoaded(data: List<NewsItem>) {
                view?.hideProgressBar()

                items += data
                view?.displayNews(data)
                lastPage++

                requestInProgress = false
                requestFailed = false
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
                requestInProgress = false
                requestFailed = true
            }
        })
    }

    override fun itemClicked(position: Int) {
        val item = items[position]
        view?.showDetail(item)
    }

    override fun retryButtonClicked() {
        view?.hideDataFailedToLoad()
        requestFailed = false
        getNextPage()
    }
}