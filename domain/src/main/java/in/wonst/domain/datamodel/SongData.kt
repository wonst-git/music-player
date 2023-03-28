package `in`.wonst.domain.datamodel

data class SongData(
    val singer: String = "",
    val album: String = "",
    val title: String = "",
    val duration: Int = 0,
    val image: String = "",
    val file: String = "",
    val lyrics: String = ""
)