package ml.sky233.suiteki.sqlite.bean

data class Device(
    val id: Int?,
    val name: String,
    val mac: String,
    val authKey: String,
    val type: String,
    val app:String
)
