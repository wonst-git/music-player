package `in`.wonst.flo.application

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FLOApplication : Application() {
    init {
        application = this
    }

    companion object {
        lateinit var application: Application
        fun applicationContext(): Context = application.applicationContext
    }
}