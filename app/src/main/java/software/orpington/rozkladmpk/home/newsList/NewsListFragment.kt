package software.orpington.rozkladmpk.home.newsList

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_news_preview_list.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem
import software.orpington.rozkladmpk.data.source.ApiClient

class NewsListFragment : Fragment(), NewsListContract.View {
    private lateinit var presenter: NewsListPresenter
    private lateinit var adapter: NewsListAdapter
    private lateinit var layoutManager: CustomLinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val httpClient = ApiClient.getHttpClient(context!!.cacheDir)
        presenter = NewsListPresenter(Injection.provideDataSource(httpClient))
        adapter = NewsListAdapter(context!!, presenter)
        layoutManager = CustomLinearLayoutManager(context)

        newsPreviews.apply {
            adapter = this@NewsListFragment.adapter
            layoutManager = this@NewsListFragment.layoutManager
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        presenter.getNextPage()
        val frag = childFragmentManager.findFragmentById(R.id.newsDetailsOverlay) as NewsDetailsFragment
        childFragmentManager
            .beginTransaction()
            .hide(frag)
            .commit()

        childFragmentManager.addOnBackStackChangedListener {
            layoutManager.setScrollEnabled(frag.isHidden)
            adapter.isClickable = frag.isHidden
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_news_preview_list, null)
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun displayNews(news: List<NewsItem>) {
        adapter.addItems(news)
    }

    override fun hideDataFailedToLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgressBar() {

    }

    override fun hideProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    override fun showDetail(item: NewsItem) {
        val frag = childFragmentManager.findFragmentById(R.id.newsDetailsOverlay) as NewsDetailsFragment
        frag.setData(item)
        childFragmentManager
            .beginTransaction()
            .show(frag)
            .addToBackStack("")
            .commit()
    }

    companion object {
        fun newInstance(): NewsListFragment {
            return NewsListFragment()
        }
    }
}