package software.orpington.rozkladmpk.home.newsList

import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.NewsItem

interface NewsListContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun getNextPage()
        fun itemClicked(position: Int)
    }

    interface View : BaseView {
        fun displayNews(news: List<NewsItem>)
        fun hideDataFailedToLoad()
        fun showDetail(item: NewsItem)
    }
}