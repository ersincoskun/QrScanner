package com.ttech.qrscanner.ui

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import androidx.fragment.app.viewModels
import com.ttech.qrscanner.R
import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.databinding.FragmentResultBinding
import com.ttech.qrscanner.utils.onSingleClickListener
import com.ttech.qrscanner.utils.printErrorLog
import com.ttech.qrscanner.utils.showErrorSnackBar
import com.ttech.qrscanner.viewModel.QrCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(), View.OnClickListener {

    private val viewModel: QrCodeViewModel by viewModels()
    private var isFavorite: Boolean = false

    override fun subLivData() {
        super.subLivData()
        viewModel.qrCodeResultData.observe(viewLifecycleOwner) { qrCodeResultData ->
            qrCodeResultData?.let { safeQrCodeResultData ->
                binding.apply {
                    tvQrResultData.text = safeQrCodeResultData.result
                    tvQrResultDate.text = safeQrCodeResultData.date
                    when {
                        safeQrCodeResultData.isUrl -> {
                            tvQrResultType.text = getString(R.string.result_fragment_url_type_text)
                            ivQrResultLogo.setImageResource(R.drawable.link_icon)
                            tvQrResultData.movementMethod = LinkMovementMethod.getInstance()
                            tvQrResultData.autoLinkMask = Linkify.WEB_URLS
                            tvQrResultData.onSingleClickListener {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(tvQrResultData.text.toString())
                                startActivity(intent)
                            }
                            tvQrResultData.paintFlags = tvQrResultData.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                        }

                        safeQrCodeResultData.isQr -> {
                            ivQrResultLogo.setImageResource(R.drawable.qr_scan_icon)
                            tvQrResultType.text = getString(R.string.result_fragment_qr_type_text)
                        }

                        else -> {
                            ivQrResultLogo.setImageResource(R.drawable.go_gallery_icon)
                            tvQrResultType.text = getString(R.string.result_fragment_barcode_type_text)
                        }
                    }
                    isFavorite = safeQrCodeResultData.isFavorite
                    if (isFavorite) {
                        binding.ivAddFavorite.setImageResource(R.drawable.star_filled_icon)
                    } else {
                        binding.ivAddFavorite.setImageResource(R.drawable.star_empty_icon)
                    }
                }
            } ?: kotlin.run {
                printErrorLog("qrCodeResultData null")
                showErrorSnackBar(binding.ivQrResultLogo, context)
            }
        }
    }

    override fun setListeners() {
        super.setListeners()
        binding.ivAddFavorite.onSingleClickListener(this)
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        arguments?.getLong("id")?.let { safeId ->
            viewModel.getQrCodeResultDataById(safeId)
        } ?: kotlin.run {
            printErrorLog("id from scanner null")
            showErrorSnackBar(binding.ivQrResultLogo, context)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivAddFavorite -> {
                arguments?.getLong("id")?.let { safeId ->
                    if (isFavorite) binding.ivAddFavorite.setImageResource(R.drawable.star_empty_icon)
                    else binding.ivAddFavorite.setImageResource(R.drawable.star_filled_icon)
                    viewModel.updateIsFavorite(safeId, !isFavorite)
                    isFavorite = !isFavorite
                } ?: kotlin.run {
                    printErrorLog("id from scanner null")
                    showErrorSnackBar(binding.ivQrResultLogo, context)
                }

            }
        }
    }

}