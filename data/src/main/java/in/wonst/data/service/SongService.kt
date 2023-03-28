package `in`.wonst.data.service

import `in`.wonst.data.datamodel.SongDataEntity
import retrofit2.Response
import retrofit2.http.GET

interface SongService {
    @GET(ApiConstants.URL)
    suspend fun getSong(): Response<SongDataEntity>

    object ApiConstants {
        const val BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/"
        const val URL = "2020-flo/song.json"
        const val TIME_OUT = 15L
    }
}