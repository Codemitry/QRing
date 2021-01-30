package com.codemitry.scanme.ui.create

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.scanme.BarcodeDataAdapter.Companion.tableToBitmap
import com.codemitry.scanme.OnHistoryClickListener
import com.codemitry.scanme.R
import com.codemitry.scanme.history.HistoryActionsManager

class CreateQRFragment : Fragment() {

    private var qrFormat: Formats? = null
    private var qrData: FormattedData? = null
    private var qrErrorCorrectionLevel: ErrorCorrectionLevels? = null
    private var qrMask: Int? = null

    private lateinit var container: ViewGroup

    private var createQRCodeButton: Button? = null

    private var formatFragment: FormatFragment? = null

    private var formatInputValid = false

    private var historyActionsManager: HistoryActionsManager? = null

    private var onHistoryClickListener: OnHistoryClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view.findViewById(R.id.createCodeContainer)


        if (childFragmentManager.findFragmentByTag(FormatFragment::class.simpleName) == null)
            showFormatFragment()
        if (childFragmentManager.findFragmentByTag(ErrorCorrectionFragment::class.simpleName) == null)
            showCorrectionFragment()
        if (childFragmentManager.findFragmentByTag(MaskFragment::class.simpleName) == null)
            showMaskFragment()

        view.findViewById<ImageButton>(R.id.history).setOnClickListener {
            onHistoryClickListener?.onHistoryClick()
        }

        historyActionsManager = ViewModelProvider(requireActivity()).get(HistoryActionsManager::class.java)

        createQRCodeButton = view.findViewById(R.id.create_button)
        createQRCodeButton?.setOnClickListener {

            showQRCode(Bitmap.createScaledBitmap(createQRCode(), 512, 512, false))
        }
    }

    private fun showFormatFragment() {
        if (formatFragment == null)
            formatFragment = FormatFragment(::onChangeFormatInputValidity)

        childFragmentManager.beginTransaction()
                .add(container.id, formatFragment!!, FormatFragment::class.simpleName)
                .commit()
    }

    private fun showCorrectionFragment() {
        childFragmentManager.beginTransaction()
                .add(container.id, ErrorCorrectionFragment(::onCorrectionChosen), ErrorCorrectionFragment::class.simpleName)
                .commit()
    }

    private fun showMaskFragment() {
        childFragmentManager.beginTransaction()
                .add(container.id, MaskFragment(::onMaskChosen), MaskFragment::class.simpleName)
                .commit()
    }

    private fun createQRCode(): Bitmap {
        qrData = formatFragment!!.getData()
        val qrcode = Barcode(qrData?.formatted ?: "", qrErrorCorrectionLevel
                ?: ErrorCorrectionLevels.default(), qrMask ?: 0)

        return tableToBitmap(qrcode.getArray())
    }

    private fun showQRCode(qr: Bitmap) {
        GeneratedQRCodeFragment(qr).show(parentFragmentManager, GeneratedQRCodeFragment::class.simpleName)
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

    fun setOnHistoryClickListener(listener: OnHistoryClickListener) {
        this.onHistoryClickListener = listener
    }

    private fun addActionToHistory() {
        // determite 1 barcode class
//        historyActionsManager.addHistoryAction(HistoryAction(HistoryAction.Actions.SCAN, barcode))
//        historyActionsManager.saveHistoryActions()
    }

}