package `in`.wonst.flo.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import `in`.wonst.data.datasource.SongDatasource
import `in`.wonst.data.repository.SongRepositoryImpl
import `in`.wonst.domain.repository.SongRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSongRepository(songDatasource: SongDatasource): SongRepository = SongRepositoryImpl(songDatasource)
}