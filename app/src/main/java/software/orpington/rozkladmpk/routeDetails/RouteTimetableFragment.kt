package software.orpington.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.utils.HeaderItemDecoration
import software.orpington.rozkladmpk.utils.forceRippleAnimation
import software.orpington.rozkladmpk.utils.smoothScrollWithOffset
import software.orpington.rozkladmpk.utils.whenScrollStateIdle
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timetable.*
import kotlinx.android.synthetic.main.route_timetable_list_header.view.*


class RouteTimetableFragment : Fragment(), RouteDetailsContract.TimetableView {

    private lateinit var adapter: RouteTimetableAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var presenter: RouteDetailsContract.Presenter? = null

    private var highlightColor: Int = -1
    private var normalColor: Int = -1

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimetableView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_timetable, container, false)
    }

    override fun onDestroyView() {
        presenter?.detachTimetableView()
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        highlightColor = ContextCompat.getColor(context!!, R.color.primary_dark)
        normalColor = ContextCompat.getColor(context!!, R.color.primary_text)

        adapter = RouteTimetableAdapter(context!!, presenter!!)
        layoutManager = LinearLayoutManager(context)

        val stickyHeader = object : HeaderItemDecoration.StickyHeaderInterface {
            override fun isHeader(itemPosition: Int): Boolean {
                if (adapter.itemCount <= itemPosition) return false

                return adapter.getItem(itemPosition).type == TimetableViewHelper.ViewType.HEADER
            }

            override fun getHeaderPositionForItem(itemPosition: Int): Int {
                var headerPosition = 0
                var itemPos = itemPosition
                do {
                    if (this.isHeader(itemPos)) {
                        headerPosition = itemPos
                        break
                    }
                    itemPos -= 1
                } while (itemPos >= 0)
                return headerPosition
            }

            override fun getHeaderLayout(headerPosition: Int): Int {
                return R.layout.route_timetable_list_header
            }

            override fun bindHeaderData(header: View?, headerPosition: Int) {
                if (adapter.itemCount <= headerPosition) return

                val item = adapter.getItem(headerPosition) as TimetableViewHelper.HeaderItem?
                    ?: return

                header?.mainText?.text = item.dayType.name
                header?.additionalText?.visibility =
                    if (item.today) View.VISIBLE else View.GONE
            }
        }
        timetable_recyclerview.addItemDecoration(HeaderItemDecoration(stickyHeader))

        timetable_recyclerview?.adapter = adapter
        timetable_recyclerview?.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter?.loadTimeTable()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadTimeTable()
    }

    override fun showTimeTable(
        items: List<TimetableViewHelper.ViewItem>,
        timeToHighlight: String,
        hourToScrollTo: HourCoordinates?
    ) {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE
        activity?.appBarLayout?.setExpanded(false, true)

        adapter.setItems(items)
        if (hourToScrollTo != null) {

            val activity = activity as RouteDetailsActivity?
            val appBarHeight = activity?.appBarLayout?.height ?: 0

            // 1) smooth scroll to an item
            // 2) when recyclerview stops scrolling, show ripple animation
            timetable_recyclerview.whenScrollStateIdle {
                if (timeToHighlight.isNotEmpty()) {
                    highlightTime(timeToHighlight)
                }

                val v = timetable_recyclerview.findViewWithTag<LinearLayout>(hourToScrollTo.rowTag)
                v?.forceRippleAnimation()
            }
            layoutManager.smoothScrollWithOffset(timetable_recyclerview, hourToScrollTo.hourIdx, appBarHeight)
        }
    }


    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        skeletonScreen = Skeleton
            .bind(timetable_recyclerview)
            .adapter(adapter)
            .load(R.layout.route_timetable_skeleton_list_item)
            .show()
    }

    override fun hideProgressBar() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        timetable_recyclerview.visibility = View.GONE
    }

    override fun highlightTime(tag: String) {
        val textView =
            timetable_recyclerview?.findViewWithTag<TextView>(tag)
        textView?.setTextColor(highlightColor)
    }

    override fun unhighlightTime(tag: String) {
        val textView =
            timetable_recyclerview?.findViewWithTag<TextView>(tag)
        textView?.setTextColor(normalColor)
    }

    companion object {
        fun newInstance(): RouteTimetableFragment {
            return RouteTimetableFragment()
        }
    }

}