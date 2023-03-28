package `in`.wonst.domain.repository

import `in`.wonst.domain.base.ApiResult
import `in`.wonst.domain.datamodel.SongData

interface SongRepository {
    suspend fun getSong(): ApiResult<SongData>
}