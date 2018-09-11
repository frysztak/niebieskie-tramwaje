package software.orpington.rozkladmpk.routeDetails

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timeline.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.Timeline
import software.orpington.rozkladmpk.utils.forceRippleAnimation
import software.orpington.rozkladmpk.utils.smoothScrollWithOffset
import software.orpington.rozkladmpk.utils.whenScrollStateIdle

class RouteTimelineFragment : Fragment(), RouteDetailsContract.TimelineView {

    private lateinit var adapter: RouteTimelineAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimelineView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_timeline, container, false)
    }

    override fun onDestroyView() {
        presenter?.detachTimelineView()
        progressBarHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RouteTimelineAdapter(context!!)
        layoutManager = LinearLayoutManager(context)

        timeline_recyclerview?.adapter = adapter
        timeline_recyclerview?.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter?.loadTimeline()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadTimeline()
    }

    private var skeletonScreen: SkeletonScreen? = null
    private val progressBarHandler = Handler()
    override fun showProgressBar() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        val runnable = Runnable {
            skeletonScreen = Skeleton
                .bind(timeline_recyclerview)
                .adapter(adapter)
                .load(R.layout.route_timeline_skeleton_list_item)
                .show()
        }
        progressBarHandler.postDelayed(runnable, 500)
    }

    override fun hideProgressBar() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        progressBarHandler.removeCallbacksAndMessages(null)
        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        timeline_recyclerview.visibility = View.GONE
    }

    override fun showTimeline(
        timeline: Timeline,
        itemToHighlight: Int,
        itemToScrollTo: Int
    ) {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE
        activity?.appBarLayout?.setExpanded(false, true)

        adapter.setItems(timeline.timeline, itemToHighlight)
        if (itemToScrollTo != -1) {
            val activity = activity as RouteDetailsActivity?
            val appBarHeight = activity?.appBarLayout?.height ?: 0

            // 1) smooth scroll to an item
            // 2) when recyclerview stops scrolling, show ripple animation
            timeline_recyclerview.whenScrollStateIdle {
                val v = timeline_recyclerview.findViewHolderForAdapterPosition(itemToScrollTo)?.itemView
                v?.forceRippleAnimation()
            }
            layoutManager.smoothScrollWithOffset(timeline_recyclerview, itemToScrollTo, appBarHeight)
        }
    }

    companion object {
        fun newInstance(): RouteTimelineFragment {
            return RouteTimelineFragment()
        }
    }
}