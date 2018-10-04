package software.orpington.rozkladmpk.home.newsList

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
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
        hideDetailsOverlay()

        val frag = childFragmentManager.findFragmentById(R.id.newsDetailsOverlay) as NewsDetailsFragment
        frag.setBackButtonCallback(object : BackButtonCallback {
            override fun backButtonPressed() {
                childFragmentManager.popBackStack()
            }
        })

        childFragmentManager.addOnBackStackChangedListener {
            layoutManager.setScrollEnabled(frag.isHidden)
            adapter.isClickable = frag.isHidden
        }
    }

    private fun hideDetailsOverlay() {
        val frag = childFragmentManager.findFragmentById(R.id.newsDetailsOverlay) as NewsDetailsFragment
        childFragmentManager
            .beginTransaction()
            .hide(frag)
            .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_news_preview_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsPreviews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val isAtTheBottom = !recyclerView.canScrollVertically(1)
                if (isAtTheBottom) {
                    presenter.getNextPage()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun displayNews(news: List<NewsItem>) = adapter.addItems(news)
    override fun showProgressBar() = adapter.showProgressBar()
    override fun hideProgressBar() = adapter.hideProgressBar()
    override fun reportThatSomethingWentWrong() = adapter.showError()
    override fun hideDataFailedToLoad() = adapter.hideDataFailedToLoad()

    override fun showDetail(item: NewsItem) {
        val frag = childFragmentManager.findFragmentById(R.id.newsDetailsOverlay) as NewsDetailsFragment
        frag.setData(item)
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.news_overlay_enter, R.anim.news_overlay_exit, R.anim.news_overlay_enter, R.anim.news_overlay_exit)
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