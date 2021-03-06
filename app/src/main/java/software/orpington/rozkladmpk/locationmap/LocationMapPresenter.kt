package software.orpington.rozkladmpk.locationmap

class LocationMapPresenter: LocationMapContract.Presenter {
    private var view: LocationMapContract.View? = null
    override fun attachView(view: LocationMapContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private val messages: MutableList<Message> = mutableListOf()

    override fun pushMessage(msg: Message, putAtTop: Boolean) {
        if (putAtTop || msg.progressBarVisible) {
            messages.add(0, msg)
        } else {
            messages.add(msg)
        }
        view?.setMessages(messages)
    }

    override fun popMessage(msg: Message) {
        if (messages.contains(msg)) {
            messages.remove(msg)
        }
        view?.setMessages(messages)
    }

    override fun getMessages(): List<Message> = messages

    override fun onMessageButtonClicked(index: Int) {
        messages[index].buttonAction()
    }

}