package software.orpington.rozkladmpk.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.home_news_card.*
import software.orpington.rozkladmpk.R

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_home_layout, container, false)
        view.findViewById<TextView>(R.id.newsCard_date)?.text = "17.09.2018"
        view.findViewById<TextView>(R.id.newsCard_synopsis)?.text = "Something hurrible"
        return view
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}