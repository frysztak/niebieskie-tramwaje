package software.orpington.rozkladmpk.home.newsList

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_news_details.*
import m7mdra.com.htmlrecycler.HtmlRecycler
import m7mdra.com.htmlrecycler.adapter.DefaultElementsAdapter
import m7mdra.com.htmlrecycler.source.StringSource
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem
import java.text.SimpleDateFormat

class NewsDetailsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_news_details, null)
    }

    fun setData(data: NewsItem) {
        val context = context ?: return
        publishedOn.text = context.getString(R.string.published_on).format(processPublishedOn(data.publishedOn))
        affectsDay.text = data.affectsDay
        affectsLines.text = data.affectsLines
        title.text = data.title

        HtmlRecycler.Builder(context)
            .setSource(StringSource(data.body))
            .setAdapter(DefaultElementsAdapter(context) { _, _, _ -> })
            .setRecyclerView(body)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnTouchListener { v, event ->
            true
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun processPublishedOn(datetime: String): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val time = fmt.parse(datetime)
        return SimpleDateFormat("HH:mm dd.MM.yyyy").format(time)
    }

    companion object {
        fun newInstance(): NewsDetailsFragment {
            return NewsDetailsFragment()
        }
    }
}