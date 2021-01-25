package com.codemitry.scanme.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.codemitry.scanme.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GeneratedQRCodeFragment(val qrcode: Bitmap) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generated_qr_code, container, false);

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("imageview: ${view.findViewById<ImageView>(R.id.qrcode)}")
        view.findViewById<ImageView>(R.id.qrcode).setImageBitmap(qrcode)
    }
}