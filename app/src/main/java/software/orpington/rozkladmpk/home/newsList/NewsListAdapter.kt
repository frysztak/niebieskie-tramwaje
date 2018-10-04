package software.orpington.rozkladmpk.home.newsList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.home_news_item.view.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem

class NewsListAdapter(
    private val context: Context,
    private val presenter: NewsListPresenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            //ITEM_TYPE_NEWS -> {
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_news_item, parent, false)
                NewsViewHolder(view, presenter) { isClickable }
            }
        }
    }

    private var items: MutableList<ViewModel> = mutableListOf()
    override fun getItemCount(): Int = items.size

    fun addItems(data: List<NewsItem>) {
        val position = items.lastIndex
        items.addAll(data.map { it -> ViewModel.NewsModel(it) })
        notifyItemRangeInserted(position, data.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is ViewModel.NewsModel -> (holder as NewsViewHolder).apply {
                date.text = item.data.affectsDay
                title.text = item.data.title
                synopsis.text = item.data.synopsis

                if (item.data.affectsLines.isNotEmpty()) {
                    val lineStringId = when (item.data.affectsLines.count { it == ',' }) {
                        1 -> R.string.line
                        else -> R.string.lines
                    }
                    lines.text = context.getString(lineStringId).format(item.data.affectsLines)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ViewModel.NewsModel -> ITEM_TYPE_NEWS
            is ViewModel.FailedToLoadModel -> ITEM_TYPE_FAILED
            is ViewModel.EndModel -> ITEM_TYPE_END
        }
    }

    private val ITEM_TYPE_NEWS = 0
    private val ITEM_TYPE_FAILED = 1
    private val ITEM_TYPE_END = 2
    var isClickable: Boolean = true

    sealed class ViewModel {
        data class NewsModel(val data: NewsItem) : ViewModel()
        object FailedToLoadModel : ViewModel()
        object EndModel : ViewModel()
    }

    internal class NewsViewHolder(
        private val view: View,
        presenter: NewsListPresenter,
        private val clickable: () -> Boolean
    ) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.newsCard_date
        val lines: TextView = view.newsCard_lines
        val title: TextView = view.newsCard_title
        val synopsis: TextView = view.newsCard_synopsis

        init {
            view.setOnClickListener {
                if (clickable()) {
                    presenter.itemClicked(adapterPosition)
                }
            }
        }
    }

    internal class FailedViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

    }

    internal class EndViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)
}