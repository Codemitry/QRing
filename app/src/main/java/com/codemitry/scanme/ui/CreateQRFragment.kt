package com.codemitry.scanme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.scanme.R

class CreateQRFragment : Fragment() {

    private var qrFormat: Formats? = null
    private var qrData: FormattedData? = null
    private var qrErrorCorrectionLevel: ErrorCorrectionLevels? = null
    private var qrMask: Int? = null

    private lateinit var container: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view.findViewById(R.id.createCodeContainer)

        showFormatFragment()
        showCorrectionFragment()
        showMaskFragment()
    }

    private fun showFormatFragment() {
        fragmentManager?.beginTransaction()
                ?.add(container.id, FormatFragment(::onDataEntered), FormatFragment::class.simpleName)
                ?.commit()
    }

    private fun showCorrectionFragment() {
        fragmentManager?.beginTransaction()
                ?.add(container.id, ErrorCorrectionFragment(::onCorrectionChosen), ErrorCorrectionFragment::class.simpleName)
                ?.commit()
    }

    private fun showMaskFragment() {
        fragmentManager?.beginTransaction()
                ?.add(container.id, MaskFragment(::onMaskChosen), MaskFragment::class.simpleName)
                ?.commit()
    }


    private fun onDataEntered(format: Formats, data: FormattedData) {
        this.qrFormat = format
        this.qrData = data

        showCorrectionFragment()
    }

    private fun onCorrectionChosen(correction: ErrorCorrectionLevels) {
        this.qrErrorCorrectionLevel = correction
    }

    private fun onMaskChosen(mask: Int) {
        this.qrMask = mask;
    }

}