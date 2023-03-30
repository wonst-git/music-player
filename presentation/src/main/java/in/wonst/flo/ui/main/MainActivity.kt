package `in`.wonst.flo.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import `in`.wonst.flo.R
import `in`.wonst.flo.ui.common.theme.FLOTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.showLyrics.value) {
                viewModel.setShowLyrics(false)
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            FLOTheme {
                window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isSystemInDarkTheme()

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    MainView()

                    if (viewModel.isLoading.collectAsState().value) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .wrapContentSize()
                            )
                        }
                    }
                }
            }
        }

        initData()
        initListener()
        initObserve()
    }

    private fun initData() {
        if (viewModel.songData.value == null) {
            viewModel.getSong()
        }
    }

    private fun initListener() {
        viewModel.mediaPlayer.setOnCompletionListener {
            viewModel.setCurrentPosition(0)
            viewModel.setPlaying(false)
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun initObserve() {
        lifecycleScope.launch {
            launch {
                while (true) {
                    if (viewModel.mediaPlayer.isPlaying) viewModel.setCurrentPosition(viewModel.mediaPlayer.currentPosition)
                    delay(500)
                }
            }
        }
    }
}

@Composable
fun MainView(viewModel: MainViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(modifier = Modifier.weight(1f)) {
            MusicView()

            AnimatedVisibility(visible = viewModel.showLyrics.collectAsState().value, enter = slideInVertically(initialOffsetY = {
                it
            }) + expandVertically(expandFrom = Alignment.Top) + fadeIn(), exit = slideOutVertically(targetOffsetY = {
                it
            }) + shrinkVertically() + fadeOut()
            ) {
                LyricsView()
            }
        }

        ControlView()
    }
}

@Composable
fun MusicView(viewModel: MainViewModel = viewModel()) {
    val musicState by viewModel.songData.collectAsState()
    val parseLyrics by viewModel.parseLyrics.collectAsState()
    val lyricsPosition by viewModel.lyricsPosition.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = musicState?.title ?: "", fontSize = TextUnit(16f, TextUnitType.Unspecified), fontWeight = FontWeight.Bold)

        Text(text = musicState?.singer ?: "", fontSize = TextUnit(14f, TextUnitType.Unspecified), fontWeight = FontWeight.Normal)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = musicState?.image, contentDescription = "", modifier = Modifier
                    .defaultMinSize(minHeight = 0.dp)
                    .wrapContentHeight(), contentScale = ContentScale.Fit
            )
        }

        Column(modifier = Modifier.clickable { viewModel.setShowLyrics(true) }, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.alpha(
                    if ((parseLyrics.getOrNull(lyricsPosition)?.first ?: 0) < currentPosition) {
                        1f
                    } else {
                        0.5f
                    }
                ),
                text = parseLyrics.getOrNull(lyricsPosition)?.second ?: "",
                maxLines = 1,
                fontSize = TextUnit(12f, TextUnitType.Unspecified),
                fontWeight = if ((parseLyrics.getOrNull(lyricsPosition)?.first ?: 0) < currentPosition) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            )
            Text(
                modifier = Modifier.alpha(0.5f),
                text = parseLyrics.getOrNull(lyricsPosition + 1)?.second ?: "",
                maxLines = 1,
                fontSize = TextUnit(12f, TextUnitType.Unspecified),
                fontWeight = FontWeight.Normal,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LyricsView(viewModel: MainViewModel = viewModel()) {
    val musicState by viewModel.songData.collectAsState()
    val lyricsPosition by viewModel.lyricsPosition.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val parseLyrics by viewModel.parseLyrics.collectAsState()
    val lyricsClickable by viewModel.lyricsClickable.collectAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Box(Modifier.padding(horizontal = 10.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = musicState?.title ?: "", fontSize = TextUnit(16f, TextUnitType.Unspecified), fontWeight = FontWeight.Bold)

                Text(text = musicState?.singer ?: "", fontSize = TextUnit(14f, TextUnitType.Unspecified), fontWeight = FontWeight.Normal)
            }
            Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = null, modifier = Modifier
                .clickable {
                    viewModel.setShowLyrics(false)
                }
                .size(36.dp)
                .align(Alignment.CenterEnd))
        }
        Row(modifier = Modifier.padding(10.dp)) {
            parseLyrics.let {
                LazyColumn(Modifier.weight(1f), scrollState) {
                    itemsIndexed(it) { index, item ->
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.dp)
                                .alpha(
                                    if (index == lyricsPosition) {
                                        if (index == 0 && (parseLyrics.getOrNull(index)?.first ?: 0) > currentPosition) {
                                            0.5f
                                        } else {
                                            1f
                                        }
                                    } else {
                                        0.5f
                                    }
                                )
                                .clickable(enabled = lyricsClickable) {
                                    viewModel.setMediaPlayerSeek(item.first.toInt())
                                }, text = item.second, textAlign = TextAlign.Center, fontWeight = if (index == lyricsPosition) {
                                if (index == 0 && (parseLyrics.getOrNull(index)?.first ?: 0) > currentPosition) {
                                    FontWeight.Normal
                                } else {
                                    FontWeight.Bold
                                }
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
            IconToggleButton(modifier = Modifier.size(36.dp), checked = lyricsClickable, onCheckedChange = { viewModel.setLyricsClickable(it) }) {
                Icon(painter = painterResource(id = R.drawable.ic_click), contentDescription = "")
            }
        }
    }

    coroutineScope.launch {
        scrollState.scrollToItem(index = lyricsPosition)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlView(viewModel: MainViewModel = viewModel()) {
    val sliderInteraction = remember { MutableInteractionSource() }
    val position = remember { mutableStateOf(0f) }
    val thumbOffset = remember { mutableStateOf(0f) }
    val textWidth = remember { mutableStateOf(0f) }

    val duration = viewModel.songData.collectAsState().value?.duration ?: 1
    val currentPosition by viewModel.currentPosition.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(Modifier.wrapContentHeight()) {
        Text(text = SimpleDateFormat("mm:ss", Locale.getDefault()).format((position.value * 1000).toLong()),
            textAlign = TextAlign.Center,
            fontSize = TextUnit(10f, TextUnitType.Unspecified),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = with(LocalDensity.current) {
                    ((thumbOffset.value - textWidth.value) / duration * position.value).toDp()
                })
                .onGloballyPositioned {
                    textWidth.value = it.size.width.toFloat()
                }
                .alpha(if (sliderInteraction.collectIsDraggedAsState().value) 1f else 0f)
                .padding(8.dp))

        Slider(modifier = Modifier
            .scale(
                1f, if (sliderInteraction.collectIsDraggedAsState().value) {
                    3f
                } else {
                    1f
                }
            )
            .height(8.dp)
            .onGloballyPositioned {
                thumbOffset.value = it.size.width.toFloat()
            }, interactionSource = sliderInteraction,
            value = if (sliderInteraction.collectIsDraggedAsState().value) position.value else currentPosition.toFloat() / 1000, onValueChange = {
                position.value = it
            }, onValueChangeFinished = {
                viewModel.setMediaPlayerSeek(position.value.toInt() * 1000)
            }, valueRange = 0f..duration.toFloat(), thumb = {
                Spacer(
                    modifier = Modifier
                        .size(10.dp)
                        .shadow(0.dp)
                        .background(Color.Transparent)
                )
            })

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.CenterStart), text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition), color = MaterialTheme.colorScheme.primary)
            Text(modifier = Modifier.align(Alignment.CenterEnd), text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration * 1000))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
        ) {
            IconToggleButton(
                checked = isPlaying,
                onCheckedChange = {
                    viewModel.setPlaying(it)
                    if (it) {
                        viewModel.mediaPlayer.start()
                    } else {
                        viewModel.mediaPlayer.pause()
                    }
                },
                colors = IconButtonDefaults.iconToggleButtonColors(contentColor = MaterialTheme.colorScheme.onSurface, checkedContentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play), contentDescription = "", modifier = Modifier.size(60.dp))
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FLOTheme {
        Surface {
            MainView()
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize()
                )
            }
        }
    }
}