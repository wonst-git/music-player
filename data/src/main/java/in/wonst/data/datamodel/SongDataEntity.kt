package `in`.wonst.data.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongDataEntity(
    val singer: String = "",
    val album: String = "",
    val title: String = "",
    val duration: Int = 0,
    val image: String = "",
    val file: String = "",
    val lyrics: String = ""
) : Parcelable