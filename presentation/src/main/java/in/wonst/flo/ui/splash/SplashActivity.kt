package `in`.wonst.flo.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import `in`.wonst.flo.R
import `in`.wonst.flo.ui.common.theme.FLOTheme
import `in`.wonst.flo.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FLOTheme {
                Image(modifier = Modifier.fillMaxSize(), painter = painterResource(id = R.drawable.flo_splash), contentDescription = "", contentScale = ContentScale.Crop)
            }
        }

        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}