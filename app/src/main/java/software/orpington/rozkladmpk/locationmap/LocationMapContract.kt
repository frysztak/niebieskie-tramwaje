package software.orpington.rozkladmpk.locationmap

interface LocationMapContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun pushMessage(msg: Message)
        fun popMessage(msg: Message)

        fun onMessageButtonClicked(index: Int)
    }

    interface View {
        fun setMessages(msgs: List<Message>)
    }
}