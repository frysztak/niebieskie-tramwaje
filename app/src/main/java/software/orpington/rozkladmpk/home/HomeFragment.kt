package software.orpington.rozkladmpk.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.home_home_layout.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.routeDetails.RouteDetailsActivity
import software.orpington.rozkladmpk.routeVariants.RouteVariantsActivity
import software.orpington.rozkladmpk.stopsForRoute.StopsForRouteActivity
import software.orpington.rozkladmpk.utils.onQueryChanged

class HomeFragment : Fragment(), SearchContract.View, FavouritesContract.View, NewsContract.View {

    private lateinit var searchPresenter: SearchPresenter
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var favouritesPresenter: FavouritesPresenter

    private lateinit var newsPresenter: NewsPresenter

    private lateinit var sharedPreferences: SharedPreferences

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val httpClient = ApiClient.getHttpClient(context!!.cacheDir)
        searchPresenter = SearchPresenter(Injection.provideDataSource(httpClient))
        searchAdapter = SearchAdapter(context!!, searchPresenter)

        favouritesPresenter = FavouritesPresenter()
        favouritesAdapter = FavouritesAdapter(context!!, favouritesPresenter)

        newsPresenter = NewsPresenter(Injection.provideDataSource(httpClient))

        sharedPreferences = context!!.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        searchPresenter.loadData()
        newsPresenter.loadMostRecentNews()

        home_searchResultsRecycler.apply {
            adapter = this@HomeFragment.searchAdapter
            layoutManager = LinearLayoutManager(context)
        }

        home_favouritesList.apply {
            adapter = this@HomeFragment.favouritesAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        home_searchView.onQueryChanged { query ->
            searchPresenter.queryTextChanged(query)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_home_layout, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        searchPresenter.attachView(this)

        favouritesPresenter.attachView(this)
        favouritesPresenter.onFavouritesLoaded(sharedPreferences.all)

        newsPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        searchPresenter.detachView()
        favouritesPresenter.detachView()
        newsPresenter.detachView()
    }

    override fun showProgressBar() {
    }

    override fun hideProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    override fun showSearchResults(data: List<StopOrRouteViewItem>) {
        view?.findViewById<ConstraintLayout>(R.id.home_searchResultsLayout)?.visibility = View.VISIBLE
        searchAdapter.setItems(data)
    }

    override fun hideSearchResults() {
        view?.findViewById<ConstraintLayout>(R.id.home_searchResultsLayout)?.visibility = View.GONE
    }

    override fun showStopNotFound() {

    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(context, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

    override fun navigateToStopsForRoute(routeID: String) {
        val i = Intent(context, StopsForRouteActivity::class.java)
        i.putExtra("routeID", routeID)
        startActivity(i)
    }

    override fun showFavourites(data: List<FavouriteViewModel>) {
        favouritesAdapter.setItems(data)
    }

    override fun focusSearchBar() {
        home_searchView.onActionViewExpanded()
    }

    override fun navigateToRouteDetails(routeID: String, stopName: String, direction: String) {
        val i = Intent(context, RouteDetailsActivity::class.java)
        i.putExtra("routeID", routeID)
        i.putExtra("stopName", stopName)
        i.putExtra("direction", direction)
        startActivity(i)
    }

    override fun showMostRecentNews(news: NewsItem) {
        view?.apply {
            findViewById<TextView>(R.id.newsCard_date)?.text = news.affectsDay
            findViewById<TextView>(R.id.newsCard_title)?.text = news.title
            findViewById<TextView>(R.id.newsCard_synopsis)?.text = news.synopsis
            if (news.affectsLines.isNotEmpty()) {
                val lineStringId = when (news.affectsLines.count { it == ',' }) {
                    1 -> R.string.line
                    else -> R.string.lines
                }
                findViewById<TextView>(R.id.newsCard_lines)?.text = context.getString(lineStringId).format(news.affectsLines)
            }
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}