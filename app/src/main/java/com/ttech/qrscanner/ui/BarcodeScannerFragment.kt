package com.ttech.qrscanner.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.ttech.qrscanner.core.base.BaseActivity
import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.core.helpers.BarcodeAnalyser
import com.ttech.qrscanner.core.helpers.PermissionHelper
import com.ttech.qrscanner.core.helpers.PreferencesHelper
import com.ttech.qrscanner.core.manager.BannerAdManager
import com.ttech.qrscanner.core.manager.InterstitialAdManager
import com.ttech.qrscanner.data.QrCodeResultData
import com.ttech.qrscanner.databinding.FragmentBarcodeScannerBinding
import com.ttech.qrscanner.utils.*
import com.ttech.qrscanner.utils.Constants.CAMERA_PERMISSION_REQUEST_CODE
import com.ttech.qrscanner.utils.Constants.SELECT_IMAGE_REQUEST_CODE
import com.ttech.qrscanner.viewModel.QrCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class BarcodeScannerFragment : BaseFragment<FragmentBarcodeScannerBinding>(), View.OnClickListener {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private val viewModel: QrCodeViewModel by viewModels()

    private var isOpenFlash = false
    private var camera: Camera? = null
    private var isCameFromPermissionSettings = false
    private lateinit var vibrator: Vibrator

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var executor: ExecutorService? = null
    private var mIsBarcodeProcessing = AtomicBoolean(false)
    private var webUrl: String? = null

    override fun assignObjects() {
        super.assignObjects()
        mIsBarcodeProcessing.compareAndSet(true, false)
        executor = Executors.newSingleThreadExecutor()
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    showPermissionDeniedDialog()
                } else {
                    initCamera()
                }
            }
    }

    override fun subLivData() {
        super.subLivData()
        viewModel.insertedItemId.observe(viewLifecycleOwner) { id ->
            id?.let { safeId ->
                val navDirection = BarcodeScannerFragmentDirections.actionBarcodeScannerFragmentToResultFragment(safeId)
                navigate(navDirections = navDirection)
                if (webUrl != null && preferencesHelper.isAutoOpenWebEnable) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(webUrl)
                    startActivity(i)
                }
            } ?: kotlin.run {
                printErrorLog("add barcode return id null")
                showErrorSnackBar(binding.ivFlashlightIcon, context)
            }
        }
    }

    override fun setListeners() {
        super.setListeners()
        binding.ivSelectPicture.onSingleClickListener(this)
        binding.ivFlashlightIcon.onSingleClickListener(this)
    }

    override fun prepareUI() {
        super.prepareUI()
        val isFlashAvailable = requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!isFlashAvailable) {
            binding.ivFlashlightIcon.remove()
        }
    }

    override fun onStarted() {
        super.onStarted()
        if (!PermissionHelper.isPermissionsGranted(
                Manifest.permission.CAMERA,
               requireActivity()
            )
        ) {
            PermissionHelper.requestPermissions(
                Manifest.permission.CAMERA,
                requestPermissionLauncher
            )
        } else {
            if (isCameFromPermissionSettings) initCamera()
        }
        setFlashState()

    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        if (PermissionHelper.isPermissionsGranted(
                Manifest.permission.CAMERA,
                requireActivity()
            )
        ) initCamera()
        if (InterstitialAdManager.interstitialAd == null) InterstitialAdManager.loadInterstitialAd(context)
        BannerAdManager.loadBannerAd(binding.adViewBarcodeScannerPage)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivFlashlightIcon -> {
                if (isOpenFlash) {
                    binding.ivFlashlightIcon.setBackgroundResource(com.ttech.qrscanner.R.drawable.flashlight_on_icon)
                    turnOnOffFlash()
                } else {
                    binding.ivFlashlightIcon.setBackgroundResource(com.ttech.qrscanner.R.drawable.flashlight_off_icon)
                    turnOnOffFlash()
                }
                isOpenFlash = !isOpenFlash
            }

            binding.ivSelectPicture -> {
                goToGallery()
            }
        }
    }

    override fun onDestroyed() {
        super.onDestroyed()
        executor?.shutdown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_IMAGE_REQUEST_CODE -> {
                if (resultCode === Activity.RESULT_OK) {
                    if (data != null) {
                        try {
                            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, data.data)
                            analyzePhoto(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun onBarcodeDetected(barcode: String?, isQr: Boolean) {
        barcode?.let { safeBarcode ->
            if (mIsBarcodeProcessing.compareAndSet(false, true)) {
                if (preferencesHelper.isBeepEnable) beep()
                if (preferencesHelper.isVibratorEnable) vibrateDevice()
                handleGetBarcodeId(safeBarcode, isQr)
            }
        } ?: kotlin.run {
            printErrorLog("barcode null from onBarcodeDetected")
            showErrorSnackBar(binding.ivFlashlightIcon, context)
        }
    }

    private fun handleGetBarcodeId(barcode: String, isQr: Boolean) {
        val urlPattern = Patterns.WEB_URL
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val isWebUrl = urlPattern.matcher(barcode).matches()
        webUrl = if (isWebUrl) {
            barcode
        } else {
            null
        }
        val qrCodeResultData = QrCodeResultData(barcode, false, isQr, isWebUrl, currentDate)
        viewModel.addQrCodeResultData(qrCodeResultData)
    }

    private fun calculateAspectRatio(width: Int, height: Int): Int {
        val previewRatio = Integer.max(width, height).toDouble() / Integer.min(width, height)
        if (kotlin.math.abs(previewRatio - RATIO_4_3_VALUE) <= kotlin.math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun initializeAnalyzer(screenAspectRatio: Int, rotation: Int): UseCase {
        return ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).setTargetRotation(rotation).build().also {
            executor?.let { it1 ->
                it.setAnalyzer(it1, BarcodeAnalyser { barcode ->

                        var isQr = false
                        val firstBarcode = barcode.firstOrNull()
                        if (firstBarcode?.format == Barcode.FORMAT_QR_CODE) {
                            isQr = true
                        }
                        onBarcodeDetected(firstBarcode?.rawValue, isQr)
                })
            }
        }
    }

    private fun beep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }

    private fun initCamera() {
        binding.previewView.display?.let { safeDisplay ->
            val metrics = DisplayMetrics().also { safeDisplay.getRealMetrics(it) }
            val screenAspectRatio = calculateAspectRatio(metrics.widthPixels, metrics.heightPixels)
            val rotation = safeDisplay.rotation

            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().setTargetAspectRatio(screenAspectRatio).setTargetRotation(rotation).build()

                preview.setSurfaceProvider(binding.previewView.surfaceProvider)

                val textBarcodeAnalyzer = initializeAnalyzer(screenAspectRatio, rotation)
                cameraProvider.unbindAll()

                try {
                    camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, textBarcodeAnalyzer)
                    setFlashState()
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(requireActivity()))
        } ?: kotlin.run {
            printErrorLog("null")
        }
    }

    private fun showPermissionDeniedDialog() {
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(getString(com.ttech.qrscanner.R.string.permission_denied_dialog_title))
            .setMessage(getString(com.ttech.qrscanner.R.string.permission_denied_dialog_description))
            .setPositiveButton(
                getString(com.ttech.qrscanner.R.string.request_permission)
            ) { dialog, _ ->
                IntentUtils.openAppDetailSettings(
                    requireActivity() as BaseActivity<*>,
                    CAMERA_PERMISSION_REQUEST_CODE
                )
                isCameFromPermissionSettings = true
                dialog.cancel()
            }.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                alertDialog.cancel()
                navigateBackStack()
                true
            } else {
                false
            }
        }
        alertDialog.show()
    }

    private fun turnOnOffFlash() {
        camera?.cameraInfo?.torchState?.value?.let { torchState ->
            if (torchState == TorchState.OFF) {
                camera?.cameraControl?.enableTorch(true)
            } else {
                camera?.cameraControl?.enableTorch(false)
            }
        }
    }

    private fun setFlashState() {
        camera?.cameraInfo?.torchState?.value?.let { torchState ->
            if (torchState == TorchState.OFF) {
                binding.ivFlashlightIcon.setBackgroundResource(com.ttech.qrscanner.R.drawable.flashlight_on_icon)
            } else {
                binding.ivFlashlightIcon.setBackgroundResource(com.ttech.qrscanner.R.drawable.flashlight_off_icon)
            }
        }
    }

    private fun analyzePhoto(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)

        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                var isQr = false
                val firstBarcode = barcodes.firstOrNull()
                if (firstBarcode?.format == Barcode.FORMAT_QR_CODE) {
                    isQr = true
                }
                onBarcodeDetected(firstBarcode?.rawValue, isQr)
            }
            .addOnFailureListener {
                printErrorLog("scanner failure: $it")
                showErrorSnackBar(binding.ivFlashlightIcon, context)
            }
    }

    private fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun goToGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(com.ttech.qrscanner.R.string.barcode_scanner_fragment_select_picture)), SELECT_IMAGE_REQUEST_CODE)
    }

}