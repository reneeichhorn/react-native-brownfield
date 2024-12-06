package com.callstack.kotlinexample

import android.app.Application
import android.util.Log
import com.callstack.reactnativebrownfield.ReactNativeBrownfield
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost

import java.util.HashMap

class MainApplication : Application(), ReactApplication {
    override fun onCreate() {
        super.onCreate()
        val packages = PackageList(this).packages
        val options = HashMap<String, Any>()
        options["packages"] = packages
        options["mainModuleName"] = "example/index"

        ReactNativeBrownfield.initialize(this, options)
        ReactNativeBrownfield.shared.startReactNative {
            Log.d("test", "test")
        }
    }

    override val reactNativeHost: ReactNativeHost
        get() = ReactNativeBrownfield.shared.reactNativeHost

    override val reactHost: ReactHost?
        get() = ReactNativeBrownfield.shared.reactHost
}
