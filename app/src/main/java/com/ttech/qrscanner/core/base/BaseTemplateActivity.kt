package com.ttech.qrscanner.core.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @Author: ibrahim.menekse
 */
//We worked together with ibrahim menekşe on my previous job and he taught me this structure.
abstract class BaseTemplateActivity<T : ViewBinding> : FragmentActivity() {

    val binding get() = mBinding!!

    private var vgRoot: ViewGroup? = null

    private var mBinding: T? = null
    private var mIsRecreated: Boolean = false

    @CallSuper open fun createViews() {}
    @CallSuper open fun assignObjects() {}
    @CallSuper open fun setListeners() {}
    @CallSuper open fun prepareUI() {}
    @CallSuper open fun onLayoutReady() {}

    @CallSuper open fun onCreated() {}
    @CallSuper open fun onStarted() {}
    @CallSuper open fun onResumed() {}
    @CallSuper open fun onPaused() {}
    @CallSuper open fun onStopped() {}
    @CallSuper open fun onDestroyed() {}

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRecreated = savedInstanceState != null
        if (mIsRecreated) {
            restartApp()
        } else {
            onCreated()
            initialize()
        }
    }

    final override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    final override fun onStart() {
        super.onStart()
        if (mIsRecreated.not()) {
            onStarted()
        }
    }

    final override fun onResume() {
        super.onResume()
        if (mIsRecreated.not()) {
            onResumed()
        }
    }

    final override fun onPause() {
        super.onPause()
        if (mIsRecreated.not()) {
            onPaused()
        }
    }

    final override fun onStop() {
        super.onStop()
        if (mIsRecreated.not()) {
            onStopped()
        }
    }

    final override fun onDestroy() {
        super.onDestroy()
        if (mIsRecreated.not()) {
            onDestroyed()
            mBinding = null
        }
    }

    fun isRecreated() = mIsRecreated

    private fun initialize() {
        prepareBinding()
        createViews()
        assignObjects()
        setListeners()
        prepareUI()
        listenOnLayoutReady()
    }

    private fun restartApp() {
        baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun prepareBinding() {
        var genericSuperclass = javaClass.genericSuperclass
        while ((genericSuperclass is ParameterizedType).not()) {
            genericSuperclass = (genericSuperclass as Class<*>).genericSuperclass
        }
        val viewBindingClassType = (genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val viewBindingClass = viewBindingClassType as Class<T>
        val inflateMethod = viewBindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        val layoutInflater = LayoutInflater.from(this)
        mBinding = inflateMethod.invoke(null, layoutInflater, null, false) as T
        vgRoot = mBinding?.root as ViewGroup
        setContentView(vgRoot)
    }

    private fun listenOnLayoutReady() {
        vgRoot?.run {
            viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    post { onLayoutReady() }
                }
            })
        }
    }
}