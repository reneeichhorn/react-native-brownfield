package com.callstack.reactnativebrownfield

import android.app.Application
import android.util.Log
import com.facebook.react.ReactDelegate
import com.facebook.react.ReactHost
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.ReactContext
import com.facebook.react.config.ReactFeatureFlags
import com.facebook.react.defaults.DefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.soloader.SoLoader
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import java.util.concurrent.atomic.AtomicBoolean

interface InitializedCallback {
  operator fun invoke(initialized: Boolean)
}

class ReactNativeBrownfield private constructor(val reactHost: ReactHost, val reactNativeHost: ReactNativeHost) {
  companion object {
    private lateinit var instance: ReactNativeBrownfield
    private val initialized = AtomicBoolean()

    @JvmStatic
    val shared: ReactNativeBrownfield get() = instance

    @JvmStatic
    fun initialize(application: Application, rHost: ReactHost, reactNativeHost: ReactNativeHost) {
      if(!initialized.getAndSet(true)) {
        instance = ReactNativeBrownfield( rHost, reactNativeHost)
      }
    }

    @JvmStatic
    fun initialize(application: Application, options: HashMap<String, Any>) {
      SoLoader.init(application.applicationContext, OpenSourceMergedSoMapping)
      
      val rnHost = object : DefaultReactNativeHost(application) {
        override fun getUseDeveloperSupport(): Boolean {
          return options["useDeveloperSupport"] as? Boolean ?: BuildConfig.DEBUG
        }

        override fun getPackages(): List<ReactPackage> {
          return (options["packages"] as? List<*> ?: emptyList<ReactPackage>())
            .filterIsInstance<ReactPackage>()
        }

        override fun getJSMainModuleName(): String {
          return options["mainModuleName"] as? String ?: super.getJSMainModuleName()
        }

        override val isNewArchEnabled: Boolean = true
        override val isHermesEnabled: Boolean = true
      }

      var rHost = DefaultReactHost.getDefaultReactHost(application.applicationContext, rnHost)


      initialize(application, rHost, rnHost)
    }

    @JvmStatic
    fun initialize(application: Application, packages: List<ReactPackage>) {
      val options = hashMapOf("packages" to packages, "mainModuleName" to "index")

      initialize(application, options)
    }
  }

  fun hasContext(): Boolean {
    return reactHost.currentReactContext?.currentActivity != null
  }

  fun hasDevSupport(): Boolean {
    return ReactNativeBrownfield.shared.reactHost.devSupportManager != null
  }

  fun startReactNative(callback: InitializedCallback?) {
    startReactNative { callback?.invoke(it) }
  }

  @JvmName("startReactNativeKotlin")
  fun startReactNative(callback: ((initialized: Boolean) -> Unit)?) {
    reactHost.addReactInstanceEventListener(object : ReactInstanceEventListener {
      override fun onReactContextInitialized(reactContext: ReactContext) {
        callback?.let { it(true) }
        reactHost.removeReactInstanceEventListener(this)
      }
    })
    reactHost.start()
  }
}

