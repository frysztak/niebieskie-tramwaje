package software.orpington.rozkladmpk.locationmap

import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape

interface LocationMapContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun pushMessage(msg: Message, putAtTop: Boolean = false)
        fun popMessage(msg: Message)

        fun onMessageButtonClicked(index: Int)
    }

    interface View {
        fun setMessages(msgs: List<Message>)

        fun drawShape(shape: Shape, colour: Int)
        fun clearShapes()

        fun drawStops(stops: List<MapData.Stop>)
        fun clearStops()
    }
}