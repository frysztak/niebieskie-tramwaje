package software.orpington.rozkladmpk.locationmap

data class Message(
    val progressBarVisible: Boolean,
    val messageTextId: Int,
    val buttonTextId: Int,
    var buttonAction: () -> Unit
)


