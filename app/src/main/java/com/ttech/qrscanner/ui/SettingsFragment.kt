package com.ttech.qrscanner.ui

import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.core.helpers.PreferencesHelper
import com.ttech.qrscanner.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun prepareUI() {
        super.prepareUI()
        binding.switchVibrator.isChecked = preferencesHelper.isVibratorEnable
        binding.switchBeep.isChecked = preferencesHelper.isBeepEnable
    }

    override fun setListeners() {
        super.setListeners()
        binding.apply {
            switchVibrator.setOnCheckedChangeListener { compoundButton, b ->
                preferencesHelper.isVibratorEnable = b
            }

            switchBeep.setOnCheckedChangeListener { compoundButton, b ->
                preferencesHelper.isBeepEnable = b
            }

            switchOpenWebAuto.setOnCheckedChangeListener { compoundButton, b ->
                preferencesHelper.isAutoOpenWebEnable = b
            }
        }
    }

}