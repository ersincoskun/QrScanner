package com.ttech.qrscanner.core.base

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.ego.bicycle.R
import com.ego.bicycle.ui.fragment.registration.WelcomeFragment
import com.ttech.qrscanner.utils.onSingleClickListener
import com.ttech.qrscanner.utils.printSbsErrorLog
import com.ttech.qrscanner.utils.remove
import com.ttech.qrscanner.utils.show
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding?> : BaseTemplateFragment<VB>() {

    var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var mActivePostRunnableHandlers: ArrayList<Handler>

    override fun onCreated() {
        super.onCreated()
        printSbsErrorLog("current fragment: $this")
        mActivePostRunnableHandlers = ArrayList()
    }

    override fun onDestroyed() {
        super.onDestroyed()
        mActivePostRunnableHandlers.forEach { handler -> handler.removeMessages(0) }
        mActivePostRunnableHandlers.clear()
    }

    fun setRegistrationToolbar(titleText: String = "", isBackButtonHidden: Boolean = false, allToolbarHide: Boolean = false) {
        val generalTitleTV = requireActivity().findViewById<TextView>(R.id.registrationGeneralTitle)
        val layoutRegisterBackButtonToolbar = requireActivity().findViewById<ConstraintLayout>(R.id.layoutRegisterBackButtonToolbar)
        generalTitleTV.text = titleText
        val generalBackBtn = requireActivity().findViewById<ImageView>(R.id.registrationBackBtn)
        generalBackBtn.onSingleClickListener {
            navigateBackStackForNav()
        }
        if (isBackButtonHidden) generalBackBtn.remove()
        else {
            generalBackBtn.show()
            generalBackBtn.bringToFront()
        }
        if (this is WelcomeFragment) layoutRegisterBackButtonToolbar.remove()
        else layoutRegisterBackButtonToolbar.show()
        if (allToolbarHide) layoutRegisterBackButtonToolbar.remove()
    }

    fun setHomeToolbar(titleText: String = "", isBackButtonHidden: Boolean = false, allToolbarHide: Boolean = false) {
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
    }

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

    fun navigate(fragment: Fragment, isAddToStack: Boolean = true) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.pop_enter_anim, R.anim.pop_exit_anim)
        transaction.replace(R.id.mainFragmentContainer, fragment)
        if (isAddToStack) transaction.addToBackStack(null)
        transaction.commit()
    }

    fun showProgressForHomePage() {
        requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).show()
        requireActivity().findViewById<View>(R.id.mainPageDarknessView).show()
    }

    fun hideProgressForHomePage() {
        requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).remove()
        requireActivity().findViewById<View>(R.id.mainPageDarknessView).remove()
    }

    fun showProgressForRegistration() {
        requireActivity().findViewById<ProgressBar>(R.id.registrationPB).show()
        requireActivity().findViewById<View>(R.id.registrationDarknessView).show()
    }

    fun hideProgressForRegistration() {
        requireActivity().findViewById<ProgressBar>(R.id.registrationPB).remove()
        requireActivity().findViewById<View>(R.id.registrationDarknessView).remove()
    }

    fun openCompletedBottomSheet(text: String) {
        bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bottomSheetView =
            LayoutInflater.from(requireActivity()).inflate(
                R.layout.bottom_sheet_sms_verify_completed,
                bottomSheetDialog?.findViewById(R.id.smsVerifyCompletedBottomSheetContainer)
            )
        bottomSheetView.findViewById<TextView>(R.id.completedBottomSheetTV)?.text = text
        bottomSheetDialog?.setContentView(bottomSheetView)
        bottomSheetDialog?.setCancelable(false)
        bottomSheetDialog?.show()
    }

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