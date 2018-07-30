package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.route_details_header.*


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

        presenter = RouteDetailsPresenter(Injection.provideDataSource(cacheDir))
        presenter.apply {
            setRouteID(routeID)
            setStopName(stopName)
            attachInfoView(this@RouteDetailsActivity)
        }
        viewPager.adapter = RouteDetailsPagerAdapter(
            presenter,
            supportFragmentManager
        )

        presenter.loadRouteInfo()
    }

    override fun showRouteInfo(routeInfo: RouteInfo) {
        val routeText = "${getString(R.string.route)} ${routeInfo.routeID}"
        route_textview.text = routeText
        toolbarTitle.text = routeText

        carrier_textview.text = routeInfo.agencyName
        icon_imageview.setImageResource(when (isBus(routeInfo.typeID)) {
            true -> R.drawable.bus
            false -> R.drawable.tram
        })
        routeType_textview.text = getRouteTypeString(routeInfo.typeID)
    }

    private fun isBus(routeTypeID: Int): Boolean {
        val busRouteTypeIDs = listOf(30, 34, 35, 39, 40)
        return busRouteTypeIDs.contains(routeTypeID)
    }

    private fun getRouteTypeString(routeTypeID: Int): String {
        return getString(when (routeTypeID) {
            30 -> R.string.normalBus
            31 -> R.string.normalTram
            34 -> R.string.suburbanBus
            35 -> R.string.expressBus
            39 -> R.string.zoneBus
            40 -> R.string.nightBus
            else -> -1
        })
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
