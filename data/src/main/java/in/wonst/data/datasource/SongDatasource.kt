package `in`.wonst.data.datasource

import `in`.wonst.data.datamodel.SongDataEntity
import `in`.wonst.data.service.SongService
import retrofit2.Response
import javax.inject.Inject

interface SongDatasource {
    suspend fun getSong(): Response<SongDataEntity>
}

class SongDatasourceImpl @Inject constructor(private val songService: SongService) : SongDatasource {
    override suspend fun getSong(): Response<SongDataEntity> = songService.getSong()
}