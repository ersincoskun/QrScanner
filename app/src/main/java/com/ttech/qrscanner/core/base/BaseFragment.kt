package com.ttech.qrscanner.core.base

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.ttech.qrscanner.utils.printErrorLog

abstract class BaseFragment<VB : ViewBinding?> : BaseTemplateFragment<VB>() {

    private lateinit var mActivePostRunnableHandlers: ArrayList<Handler>
    lateinit var activity: BaseActivity<*>

    override fun onCreated() {
        super.onCreated()
        printErrorLog("current fragment: $this")
        activity = context as BaseActivity<*>
        mActivePostRunnableHandlers = ArrayList()
    }

    override fun onDestroyed() {
        super.onDestroyed()
        mActivePostRunnableHandlers.forEach { handler -> handler.removeMessages(0) }
        mActivePostRunnableHandlers.clear()
    }

  /*  fun setToolbar(titleText: String = "", isBackButtonHidden: Boolean = false, allToolbarHide: Boolean = false) {
        val toolbarTitleTV = requireActivity().findViewById<TextView>(R.id.mainToolbarTitle)
        val mainToolbarTitleWithBackButtonTV = requireActivity().findViewById<TextView>(R.id.mainToolbarTitleWithBackButton)
        val mainToolbar =
            requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainActivityToolbar)

        val layoutMainToolbarWithBackButton = requireActivity().findViewById<ConstraintLayout>(R.id.layoutMainToolbarWithBackButton)
        val generalBackBtn = requireActivity().findViewById<ImageView>(R.id.mainBackBtn)
        generalBackBtn.onSingleClickListener {
            navigateBackStack()
        }
        if (isBackButtonHidden) {
            toolbarTitleTV.text = titleText
            layoutMainToolbarWithBackButton.remove()
            mainToolbar.show()
        } else {
            mainToolbarTitleWithBackButtonTV.text = titleText
            mainToolbar.remove()
            layoutMainToolbarWithBackButton.show()
            generalBackBtn.bringToFront()
        }

        if (allToolbarHide) {
            mainToolbar.remove()
            layoutMainToolbarWithBackButton.remove()
        }
    }*/

    fun showToast(message: String, duration: Int) {
        context?.let { safeContext ->
            Toast.makeText(safeContext, message, duration).show()
        }
    }

    fun showSnackBar(message: String, duration: Int = 2000) {
        view?.let { safeView ->
            Snackbar.make(safeView, message, duration).show()
        }
    }

    fun navigateBackStack() {
        this.fragmentManager?.popBackStack()
    }

    fun navigateBackStackForNav(){
        Navigation.findNavController(binding.root).popBackStack()
    }

    fun navigate(action: Int? = null, navDirections: NavDirections? = null) {
        action?.let {
            Navigation.findNavController(binding.root).navigate(it)
        }
        navDirections?.let {
            Navigation.findNavController(binding.root).navigate(it)
        }
    }

 /*   fun showProgressForHomePage() {
        requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).show()
        requireActivity().findViewById<View>(R.id.mainPageDarknessView).show()
    }

    fun hideProgressForHomePage() {
        requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).remove()
        requireActivity().findViewById<View>(R.id.mainPageDarknessView).remove()
    }*/

    fun postRunnable(runnable: Runnable, delay: Long): Handler {
        val handler = Handler(Looper.getMainLooper()).apply {
            postDelayed({
                mActivePostRunnableHandlers.remove(this)
                runnable.run()
            }, delay)
        }
        mActivePostRunnableHandlers.add(handler)
        return handler
    }

}