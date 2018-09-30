package software.orpington.rozkladmpk.locationmap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.location_map_fragment_message.view.*
import software.orpington.rozkladmpk.R

class MessagesAdapter(
    private val context: Context,
    private val presenter: LocationMapContract.Presenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: List<Message> = emptyList()

    fun setMessages(msgs: List<Message>) {
        messages = msgs
        notifyDataSetChanged()
    }

    private val clickListener = object : ClickListener {
        override fun buttonClicked(index: Int) = presenter.onMessageButtonClicked(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_PROGRESS_ID -> {
                val view = LayoutInflater.from(context).inflate(R.layout.location_map_fragment_progress, parent, false)
                ProgressViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.location_map_fragment_message, parent, false)
                MessageViewHolder(view, clickListener)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (msg.progressBarVisible) {
            true -> { // do nothing
            }
            false -> {
                (holder as MessageViewHolder).apply {
                    if (msg.messageTextId != -1) {
                        message.text = context.getString(msg.messageTextId)
                    }
                    if (msg.buttonTextId != -1) {
                        button.text = context.getString(msg.buttonTextId)
                    }
                }
            }
        }
    }

    private val MESSAGE_PROGRESS_ID = 0
    private val MESSAGE_REGULAR_ID = 1
    override fun getItemViewType(position: Int): Int {
        return when (messages[position].progressBarVisible) {
            true -> MESSAGE_PROGRESS_ID
            false -> MESSAGE_REGULAR_ID
        }
    }

    internal interface ClickListener {
        fun buttonClicked(index: Int)
    }

    internal class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)
    internal class MessageViewHolder(view: View, private val clickListener: ClickListener) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.messageText
        val button: Button = view.messageButton

        init {
            button.setOnClickListener {
                clickListener.buttonClicked(adapterPosition)
            }
        }
    }

}
