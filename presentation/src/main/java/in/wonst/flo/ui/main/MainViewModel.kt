package `in`.wonst.flo.ui.main

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.wonst.domain.datamodel.SongData
import `in`.wonst.domain.usecase.SongUseCase
import kotlinx.coroutines.delay
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

    val mediaPlayer = MediaPlayer()

    val testValue = """{
    "singer": "챔버오케스트라",
    "album": "캐롤 모음",
    "title": "We Wish You A Merry Christmas",
    "duration": 198,
    "image": "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/cover.jpg",
    "file": "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/music.mp3",
    "lyrics": "[00:16:200]we wish you a merry christmas\n[00:18:300]we wish you a merry christmas\n[00:21:100]we wish you a merry christmas\n[00:23:600]and a happy new year\n[00:26:300]we wish you a merry christmas\n[00:28:700]we wish you a merry christmas\n[00:31:400]we wish you a merry christmas\n[00:33:600]and a happy new year\n[00:36:500]good tidings we bring\n[00:38:900]to you and your kin\n[00:41:500]good tidings for christmas\n[00:44:200]and a happy new year\n[00:46:600]Oh, bring us some figgy pudding\n[00:49:300]Oh, bring us some figgy pudding\n[00:52:200]Oh, bring us some figgy pudding\n[00:54:500]And bring it right here\n[00:57:000]Good tidings we bring \n[00:59:700]to you and your kin\n[01:02:100]Good tidings for Christmas \n[01:04:800]and a happy new year\n[01:07:400]we wish you a merry christmas\n[01:10:000]we wish you a merry christmas\n[01:12:500]we wish you a merry christmas\n[01:15:000]and a happy new year\n[01:17:700]We won't go until we get some\n[01:20:200]We won't go until we get some\n[01:22:800]We won't go until we get some\n[01:25:300]So bring some out here\n[01:29:800]연주\n[02:11:900]Good tidings we bring \n[02:14:000]to you and your kin\n[02:16:500]good tidings for christmas\n[02:19:400]and a happy new year\n[02:22:000]we wish you a merry christmas\n[02:24:400]we wish you a merry christmas\n[02:27:000]we wish you a merry christmas\n[02:29:600]and a happy new year\n[02:32:200]Good tidings we bring \n[02:34:500]to you and your kin\n[02:37:200]Good tidings for Christmas \n[02:40:000]and a happy new year\n[02:42:400]Oh, bring us some figgy pudding\n[02:45:000]Oh, bring us some figgy pudding\n[02:47:600]Oh, bring us some figgy pudding\n[02:50:200]And bring it right here\n[02:52:600]we wish you a merry christmas\n[02:55:300]we wish you a merry christmas\n[02:57:900]we wish you a merry christmas\n[03:00:500]and a happy new year"
}"""

    fun getSong() {
        viewModelScope.launch {
            _isLoading.value = true

            delay(1000)
//            when (val response = songUseCase.invoke()) {
//                is ApiResult.Success -> {
//                    _songData.emit(response.response)
//                }
//                is ApiResult.Failure -> {
//                    _errorMessage.emit(response.failure)
//                }
//                is ApiResult.Exception -> {
//                    _errorMessage.emit(response.exception.message)
//                }
//            }
            _songData.emit(Gson().fromJson(testValue, SongData::class.java))

            _songData.value?.lyrics?.split("\n")?.map {
                it.split("]").run {
                    (SimpleDateFormat("[mm:ss:SSS", Locale.getDefault()).parse(get(0)) ?: Date()).time to get(1)
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
            _currentPosition.emit(value)
            mediaPlayer.seekTo(value)
        }
    }

    fun setPlaying(value: Boolean) {
        viewModelScope.launch {
            _isPlaying.emit(value)
        }
    }
}