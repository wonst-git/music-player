package `in`.wonst.data.repository

import `in`.wonst.data.datasource.SongDatasource
import `in`.wonst.domain.base.ApiResult
import `in`.wonst.domain.datamodel.SongData
import `in`.wonst.domain.repository.SongRepository
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(private val songDatasource: SongDatasource) : SongRepository {
    override suspend fun getSong(): ApiResult<SongData> {
        return try {
            val result = songDatasource.getSong()
            if (result.isSuccessful) {
                ApiResult.Success(
                    SongData(
                        result.body()?.singer ?: "", result.body()?.album ?: "", result.body()?.title ?: "",
                        result.body()?.duration ?: 0, result.body()?.image ?: "", result.body()?.file ?: "",
                        result.body()?.lyrics ?: ""
                    )
                )
            } else {
                ApiResult.Failure(result.errorBody().toString())
            }
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
    }
}