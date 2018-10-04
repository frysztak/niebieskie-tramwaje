package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.NewsItem

interface NewsContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadMostRecentNews()
        fun showMoreClicked()
    }

    interface View : BaseView {
        fun showMostRecentNews(news: NewsItem)
        fun showNewsDetail(news: NewsItem)
    }
}