package `in`.wonst.flo.ui.main

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.wonst.domain.base.ApiResult
import `in`.wonst.domain.datamodel.SongData
import `in`.wonst.domain.usecase.SongUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val songUseCase: SongUseCase) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _songData: MutableStateFlow<SongData?> = MutableStateFlow(null)
    val songData: StateFlow<SongData?> = _songData.asStateFlow()

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showLyrics: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLyrics: StateFlow<Boolean> get() = _showLyrics

    private val _parseLyrics: MutableStateFlow<List<Pair<Long, String>>> = MutableStateFlow(listOf())
    val parseLyrics: StateFlow<List<Pair<Long, String>>> get() = _parseLyrics

    private val _currentPosition: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> get() = _currentPosition

    private val _isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _lyricsPosition: MutableStateFlow<Int> = MutableStateFlow(0)
    val lyricsPosition: StateFlow<Int> get() = _lyricsPosition

    private val _lyricsClickable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val lyricsClickable: StateFlow<Boolean> get() = _lyricsClickable

    val mediaPlayer = MediaPlayer()

    fun getSong() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val response = songUseCase.invoke()) {
                is ApiResult.Success -> {
                    _songData.emit(response.response)
                }
                is ApiResult.Failure -> {
                    _errorMessage.emit(response.failure)
                }
                is ApiResult.Exception -> {
                    _errorMessage.emit(response.exception.message)
                }
            }

            _songData.value?.file?.let {
                mediaPlayer.setDataSource(it)
                mediaPlayer.prepareAsync()
            }

            _songData.value?.lyrics?.split("\n")?.map {
                it.split("]").run {
                    (SimpleDateFormat("[mm:ss:SSS", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("asia/seoul") }.parse(get(0)) ?: Date()).time to get(1)
                }
            }?.apply {
                _parseLyrics.emit(this)
            }

            _isLoading.value = false
        }
    }

    fun setShowLyrics(value: Boolean) {
        viewModelScope.launch {
            _showLyrics.emit(value)
        }
    }

    fun setCurrentPosition(value: Int) {
        viewModelScope.launch {
            _currentPosition.emit(value)


            if ((parseLyrics.value.getOrNull(1)?.first ?: 0) > value) {
                _lyricsPosition.value = 0
            } else if ((parseLyrics.value.lastOrNull()?.first ?: 0) <= value) {
                _lyricsPosition.value = parseLyrics.value.lastIndex
            } else {
                _lyricsPosition.value = maxOf(parseLyrics.value.indexOfFirst {
                    value < it.first
                } - 1, 0)
            }
        }
    }

    fun setMediaPlayerSeek(value: Int) {
        viewModelScope.launch {
            setCurrentPosition(value)
            mediaPlayer.seekTo(value)
        }
    }

    fun setPlaying(value: Boolean) {
        viewModelScope.launch {
            _isPlaying.emit(value)
        }
    }

    fun setLyricsClickable(value: Boolean) {
        viewModelScope.launch {
            _lyricsClickable.emit(value)
        }
    }
}