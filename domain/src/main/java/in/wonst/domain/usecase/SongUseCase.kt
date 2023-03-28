package `in`.wonst.domain.usecase

import `in`.wonst.domain.base.ApiResult
import `in`.wonst.domain.datamodel.SongData
import `in`.wonst.domain.repository.SongRepository
import javax.inject.Inject

class SongUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): ApiResult<SongData> = songRepository.getSong()
}