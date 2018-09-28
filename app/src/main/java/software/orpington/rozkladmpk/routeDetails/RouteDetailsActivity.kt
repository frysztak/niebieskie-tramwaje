package software.orpington.rozkladmpk.routeDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.route_details_header.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.RouteInfo
import software.orpington.rozkladmpk.data.source.ApiClient
import java.util.*


class RouteDetailsActivity : AppCompatActivity(),
    RouteDetailsContract.InfoView,
    AppBarLayout.OnOffsetChangedListener {

    private lateinit var presenter: RouteDetailsContract.Presenter
    private lateinit var stopName: String
    private lateinit var routeID: String

    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200L

    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        appBarLayout.addOnOffsetChangedListener(this)

        routeID = intent.getStringExtra("routeID")
        stopName = intent.getStringExtra("stopName")
        val direction = if (intent.hasExtra("direction")) intent.getStringExtra("direction") else null
        val departureTime = if (intent.hasExtra("departureTime")) intent.getStringExtra("departureTime") else null

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = RouteDetailsPresenter(Injection.provideDataSource(httpClient))
        presenter.attachInfoView(this)
        if (savedInstanceState == null) {
            presenter.setRouteID(routeID)
            presenter.setStopName(stopName)
            if (direction != null) {
                presenter.setDirection(direction)
            }

            if (departureTime != null) {
                val tripID = intent.getIntExtra("tripID", -1)
                presenter.setDepartureTime(departureTime, tripID)
            }
        } else {
            val state = savedInstanceState.getParcelable<RouteDetailsState>("state")
            presenter.setState(state)
        }

        viewPager.adapter = RouteDetailsPagerAdapter(
            this,
            supportFragmentManager
        )
        viewPager.offscreenPageLimit = 1
        tabLayout.setupWithViewPager(viewPager)

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                    if (f is RouteDetailsContract.DirectionsView) {
                        f.attachPresenter(presenter)
                    }

                    if (f is RouteDetailsContract.TimetableView) {
                        f.attachPresenter(presenter)
                    }

                    if (f is RouteDetailsContract.TimelineView) {
                        f.attachPresenter(presenter)
                    }
                }
            }, false)

        presenter.loadRouteInfo()

        if (departureTime != null) {
            viewPager.currentItem = 2 // timeline
        } else if (direction != null) {
            viewPager.currentItem = 1 // timetable
        }
    }

    override fun onDestroy() {
        presenter.detachInfoView()
        progressBarHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("state", presenter.getState())
    }

    @SuppressLint("SetTextI18n")
    override fun showRouteInfo(routeInfo: RouteInfo) {
        val routeText = "${getString(R.string.route)} ${routeInfo.routeID}"
        route_textview.text = routeText
        stopName_textview.text = stopName
        toolbarTitle.text = "$routeText — $stopName"

        carrier_textview.text = routeInfo.agencyName
        icon_imageview.setImageResource(when (routeInfo.isBus) {
            true -> R.drawable.bus
            false -> R.drawable.tram
        })
        routeType_textview.text = translateRouteType(routeInfo.routeType)
    }

    private var skeletonScreen: SkeletonScreen? = null
    private val progressBarHandler = Handler()
    override fun showProgressBar() {
        val runnable = Runnable {
            skeletonScreen = Skeleton.bind(headerLayout)
                .load(R.layout.route_details_skeleton_header)
                .show()
        }
        progressBarHandler.postDelayed(runnable, 500)
    }

    override fun hideProgressBar() {
        progressBarHandler.removeCallbacksAndMessages(null)
        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        // there's really no space in the header for any kind of error reporting
        // just try to load again
        presenter.loadRouteInfo()
    }

    override fun switchToTimetableTab() {
        viewPager.currentItem = 1
    }

    override fun switchToTimelineTab() {
        viewPager.currentItem = 2
    }

    private fun translateRouteType(routeType: String): String {
        val currentLanguage = Locale.getDefault().displayLanguage
        if (currentLanguage == "polski") {
            // API already serves route types in Polish
            return routeType
        }

        val map = mapOf(
            "Normalna autobusowa" to R.string.normalBus,
            "Normalna tramwajowa" to R.string.normalTram,
            "Okresowa autobusowa" to R.string.seasonalBus,
            "Podmiejska autobusowa" to R.string.suburbanBus,
            "Pospieszna autobusowa" to R.string.expressBus,
            "Strefowa autobusowa" to R.string.zoneBus,
            "Nocna autobusowa" to R.string.nightBus
        )

        if (!map.containsKey(routeType)) {
            // Unknown key
            // TODO: logging?
            return routeType
        }

        return getString(map[routeType]!!)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbarTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleVisible = true
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbarTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(headerLayout, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(headerLayout, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
