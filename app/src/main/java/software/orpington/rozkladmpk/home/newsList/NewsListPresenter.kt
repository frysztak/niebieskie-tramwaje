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

    private val items : MutableList<NewsItem> = mutableListOf()
    private var lastPage = 0
    override fun getNextPage() {
        remoteDataSource.getNews(lastPage, object : IDataSource.LoadDataCallback<List<NewsItem>> {
            override fun onDataLoaded(data: List<NewsItem>) {
                items += data
                view?.displayNews(data)
                lastPage++
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun itemClicked(position: Int) {
        val item = items[position]
        view?.showDetail(item)
    }
}