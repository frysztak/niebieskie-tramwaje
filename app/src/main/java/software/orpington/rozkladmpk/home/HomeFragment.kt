package software.orpington.rozkladmpk.home

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.home_home_layout.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.routeVariants.RouteVariantsActivity
import software.orpington.rozkladmpk.stopsForRoute.StopsForRouteActivity
import software.orpington.rozkladmpk.utils.onQueryChanged

class HomeFragment : Fragment(), HomeFragmentContract.View {

    private lateinit var presenter: HomeFragmentPresenter
    private lateinit var searchAdapter: SearchAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val httpClient = ApiClient.getHttpClient(context!!.cacheDir)
        presenter = HomeFragmentPresenter(Injection.provideDataSource(httpClient))
        searchAdapter = SearchAdapter(context!!, presenter)
        presenter.loadData()

        home_searchResultsRecycler.apply {
            adapter = this@HomeFragment.searchAdapter
            layoutManager = LinearLayoutManager(context)
        }

        home_searchView.onQueryChanged { query ->
            presenter.queryTextChanged(query)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_home_layout, container, false)
        view.findViewById<TextView>(R.id.newsCard_date)?.text = "17.09.2018"
        view.findViewById<TextView>(R.id.newsCard_synopsis)?.text = "Something hurrible"
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun showProgressBar() {
    }

    override fun hideProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    override fun showSearchResults(data: List<StopOrRoute>) {
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

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}