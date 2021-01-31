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
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Text
import com.codemitry.scanme.BarcodeDataAdapter.Companion.tableToBitmap
import com.codemitry.scanme.OnHistoryClickListener
import com.codemitry.scanme.R
import com.codemitry.scanme.history.HistoryAction
import com.codemitry.scanme.history.HistoryActionsManager

class CreateQRFragment : Fragment() {

    private var qrFormat: Formats? = null
    private var qrData: FormattedData? = null
    private var qrErrorCorrectionLevel: ErrorCorrectionLevels? = null
    private var qrMask: Int? = null

    private lateinit var container: ViewGroup

    private var createQRCodeButton: Button? = null

    private var clearFormatButton: Button? = null

    private var formatFragment: FormatFragment? = null
    private var dataFragment: DataFragment? = null

    private var dataInputValid = false

    private var historyActionsManager: HistoryActionsManager? = null

    private var onHistoryClickListener: OnHistoryClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view.findViewById(R.id.createCodeContainer)

        clearFormatButton = view.findViewById(R.id.formatButton)

        if (qrFormat != null && childFragmentManager.findFragmentByTag(DataFragment::class.simpleName) == null) {
            showClearFormatButton(qrFormat!!)
            showDataFragment()
        } else
            if (qrFormat == null && childFragmentManager.findFragmentByTag(FormatFragment::class.simpleName) == null)
                showFormatFragment()

        if (qrFormat != null)
            showClearFormatButton(qrFormat!!)

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
            qrData = dataFragment!!.getData()

            showQRCode(Bitmap.createScaledBitmap(createQRCode(
                    qrData ?: Text(""),
                    qrErrorCorrectionLevel ?: ErrorCorrectionLevels.default(),
                    qrMask ?: 1), 512, 512, false))
        }

        // init state
        onChangeDataInputValidity(false)

    }

    private fun showFormatFragment() {
        if (formatFragment == null)
            formatFragment = FormatFragment(::onFormatSelected)

        val transaction = childFragmentManager.beginTransaction()
        if (childFragmentManager.fragments.size > 0)
            transaction.replace(container.id, formatFragment!!, FormatFragment::class.simpleName)
        else
            transaction.add(container.id, formatFragment!!, FormatFragment::class.simpleName)

        transaction.commit()
    }

    private fun showDataFragment() {
        if (dataFragment == null)
            dataFragment = DataFragment(::onChangeDataInputValidity)

        dataFragment?.format = qrFormat

        childFragmentManager.beginTransaction()
                .replace(container.id, dataFragment!!, DataFragment::class.simpleName)
                .commit()
    }

    private fun showCorrectionFragment() {
        childFragmentManager.beginTransaction()
                .add(R.id.staticContainer, ErrorCorrectionFragment(::onCorrectionChosen), ErrorCorrectionFragment::class.simpleName)
                .commit()
    }

    private fun showMaskFragment() {
        childFragmentManager.beginTransaction()
                .add(R.id.staticContainer, MaskFragment(::onMaskChosen), MaskFragment::class.simpleName)
                .commit()
    }

    private fun createQRCode(data: FormattedData, correction: ErrorCorrectionLevels, mask: Int): Bitmap {
        val qrCode = Barcode(data, correction, mask)

        qrCode.create()

        addActionToHistory(qrCode)

        return tableToBitmap(qrCode.getCode())
    }

    private fun showQRCode(qr: Bitmap) {
        GeneratedQRCodeFragment(qr).show(parentFragmentManager, GeneratedQRCodeFragment::class.simpleName)
    }

    private fun onFormatSelected(format: Formats) {
        this.qrFormat = format

        showClearFormatButton(format)
        showDataFragment()
    }

    private fun showClearFormatButton(format: Formats) {
        clearFormatButton?.let {
            it.text = formatNameFor(format)
            it.visibility = View.VISIBLE
            it.setOnClickListener { onClearFormatButtonClick() }
        }
    }

    private fun hideClearFormatButton() {
        clearFormatButton?.let {
            it.setOnClickListener { }
            it.text = ""
            it.visibility = View.GONE
        }
    }

    private fun onCorrectionChosen(correction: ErrorCorrectionLevels) {
        this.qrErrorCorrectionLevel = correction
    }

    private fun onMaskChosen(mask: Int) {
        this.qrMask = mask;
    }

    private fun onChangeDataInputValidity(isValid: Boolean) {
        dataInputValid = isValid

        createQRCodeButton?.isEnabled = isValid
    }

    fun setOnHistoryClickListener(listener: OnHistoryClickListener) {
        this.onHistoryClickListener = listener
    }

    private fun addActionToHistory(qrCode: Barcode) {
        // determite 1 barcode class
        historyActionsManager?.addHistoryAction(HistoryAction(HistoryAction.Actions.CREATE, qrCode))
        historyActionsManager?.saveHistoryActions()
    }

    // when format already chosen and user clicks to clear format
    private fun onClearFormatButtonClick() {
        qrFormat = null
        qrData = null
        dataFragment?.format = null

        dataInputValid = false

        showFormatFragment()
        hideClearFormatButton()
    }

    private fun formatNameFor(format: Formats): String = when (format) {
        Formats.TEXT -> getString(R.string.text)
        Formats.URL -> getString(R.string.link)
        Formats.WIFI -> getString(R.string.wifi)
        Formats.EMAIL -> getString(R.string.email)
        Formats.SMS -> getString(R.string.sms)
        Formats.CONTACT_INFO -> getString(R.string.vcard)
        Formats.LOCATION -> getString(R.string.location)
        else -> error("Unreachable situation")
    }

}