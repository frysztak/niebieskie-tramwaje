package software.orpington.rozkladmpk.home.newsList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.home_news_error.view.*
import kotlinx.android.synthetic.main.home_news_item.view.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.NewsItem

class NewsListAdapter(
    private val context: Context,
    private val presenter: NewsListPresenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_PROGRESS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_news_progress, parent, false)
                DummyViewHolder(view)
            }
            ITEM_TYPE_FAILED -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_news_error, parent, false)
                ErrorViewHolder(view, presenter)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_news_item, parent, false)
                NewsViewHolder(view, presenter) { isClickable }
            }
        }
    }

    private var items: MutableList<ViewModel> = mutableListOf()
    override fun getItemCount(): Int = items.size

    fun addItems(data: List<NewsItem>) {
        val position = items.size
        items.addAll(data.map { it -> ViewModel.News(it) })
        notifyItemRangeInserted(position, data.size)
    }

    private fun appendItem(item: ViewModel) {
        val position = items.size
        items.add(item)
        notifyItemInserted(position)
    }

    private fun removeItem(item: ViewModel) {
        val idx = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(idx)
    }

    fun showProgressBar() = appendItem(ViewModel.Progress)
    fun hideProgressBar() = removeItem(ViewModel.Progress)
    fun showError() = appendItem(ViewModel.Error)
    fun hideDataFailedToLoad() = removeItem(ViewModel.Error)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is ViewModel.News -> (holder as NewsViewHolder).apply {
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
            is ViewModel.News -> ITEM_TYPE_NEWS
            is ViewModel.Error -> ITEM_TYPE_FAILED
            is ViewModel.End -> ITEM_TYPE_END
            is ViewModel.Progress -> ITEM_TYPE_PROGRESS
        }
    }

    private val ITEM_TYPE_NEWS = 0
    private val ITEM_TYPE_FAILED = 1
    private val ITEM_TYPE_END = 2
    private val ITEM_TYPE_PROGRESS = 2
    var isClickable: Boolean = true

    sealed class ViewModel {
        data class News(val data: NewsItem) : ViewModel()
        object Error : ViewModel()
        object End : ViewModel()
        object Progress: ViewModel()
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

    internal class ErrorViewHolder(
        private val view: View,
        private val presenter: NewsListPresenter
    ) : RecyclerView.ViewHolder(view) {
        private val retry: Button = view.retryButton

        init {
            retry.setOnClickListener {
                presenter.retryButtonClicked()
            }
        }
    }

    internal class DummyViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)
}