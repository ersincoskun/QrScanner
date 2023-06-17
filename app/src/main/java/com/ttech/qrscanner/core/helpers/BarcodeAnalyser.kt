package com.ttech.qrscanner.core.helpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyser(private val barcodeListener: (barcode: MutableList<Barcode>) -> Unit) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_CODE_39,
        Barcode.FORMAT_CODE_93,
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_CODABAR,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_ITF,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E,
        Barcode.FORMAT_QR_CODE
    ).build()

    private val scanner by lazy {
        BarcodeScanning.getClient(options)
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            val height = mediaImage.height
            val width = mediaImage.width

            //Since in the end the image will rotate clockwise 90 degree
            //left -> top, top -> right, right -> bottom, bottom -> left

            //Top    : (far) -value > 0 > +value (closer)
            val c1x = (width * 0.125).toInt() + 150
            //Right  : (far) -value > 0 > +value (closer)
            val c1y = (height * 0.25).toInt() - 25
            //Bottom : (closer) -value > 0 > +value (far)
            val c2x = (width * 0.875).toInt() - 150
            //Left   : (closer) -value > 0 > +value (far)
            val c2y = (height * 0.75).toInt() + 25

            val rect = Rect(c1x, c1y, c2x, c2y)

            val ori: Bitmap = imageProxy.toBitmap()!!
            val crop = Bitmap.createBitmap(ori, rect.left, rect.top, rect.width(), rect.height())
            val rImage = crop.rotate(90F)

            val image: InputImage =
                InputImage.fromBitmap(rImage, imageProxy.imageInfo.rotationDegrees)

            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    if (barcodes.isEmpty().not()) {
                        barcodeListener(barcodes)
                    }
                }
                .addOnFailureListener {
                    // You should really do something about Exceptions
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }
}