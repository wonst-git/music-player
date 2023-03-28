package `in`.wonst.flo.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import `in`.wonst.data.datasource.SongDatasource
import `in`.wonst.data.datasource.SongDatasourceImpl
import `in`.wonst.data.service.SongService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideSongDatasource(songService: SongService): SongDatasource = SongDatasourceImpl(songService)
}