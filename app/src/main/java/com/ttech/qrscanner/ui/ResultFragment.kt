package com.ttech.qrscanner.ui

import androidx.fragment.app.viewModels
import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.databinding.FragmentResultBinding
import com.ttech.qrscanner.viewModel.QrCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>() {

    private val viewModel: QrCodeViewModel by viewModels()

    override fun onLayoutReady() {
        super.onLayoutReady()
        val id = arguments?.getLong("id")
    }

}