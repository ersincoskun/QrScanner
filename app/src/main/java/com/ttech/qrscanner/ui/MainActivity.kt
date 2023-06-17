package com.ttech.qrscanner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ttech.qrscanner.R
import com.ttech.qrscanner.core.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}