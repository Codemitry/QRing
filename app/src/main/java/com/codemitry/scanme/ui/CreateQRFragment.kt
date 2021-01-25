package com.codemitry.scanme.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.scanme.BarcodeDataAdapter.Companion.tableToBitmap
import com.codemitry.scanme.R

class CreateQRFragment : Fragment() {

    private var qrFormat: Formats? = null
    private var qrData: FormattedData? = null
    private var qrErrorCorrectionLevel: ErrorCorrectionLevels? = null
    private var qrMask: Int? = null

    private lateinit var container: ViewGroup

    private var createQRCodeButton: Button? = null

    private var formatFragment: FormatFragment? = null

    private var formatInputValid = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view.findViewById(R.id.createCodeContainer)

        showFormatFragment()
        showCorrectionFragment()
        showMaskFragment()

        createQRCodeButton = view.findViewById(R.id.create_button)
        createQRCodeButton?.setOnClickListener {
            showQRCode(Bitmap.createScaledBitmap(createQRCode(), 512, 512, false))
        }
    }

    private fun showFormatFragment() {
        if (formatFragment == null)
            formatFragment = FormatFragment(::onChangeFormatInputValidity)

        fragmentManager?.beginTransaction()
                ?.add(container.id, formatFragment!!, FormatFragment::class.simpleName)
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

    private fun createQRCode(): Bitmap {
        qrData = formatFragment!!.getData()
        val qrcode = Barcode(qrData?.formatted ?: "", qrErrorCorrectionLevel
                ?: ErrorCorrectionLevels.default(), qrMask ?: 0)

        return tableToBitmap(qrcode.getArray())
    }

    private fun showQRCode(qr: Bitmap) {
        GeneratedQRCodeFragment(qr).show(requireFragmentManager(), GeneratedQRCodeFragment::class.simpleName)
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

    private fun onChangeFormatInputValidity(isValid: Boolean) {
        formatInputValid = isValid

        createQRCodeButton?.isEnabled = isValid
    }

}