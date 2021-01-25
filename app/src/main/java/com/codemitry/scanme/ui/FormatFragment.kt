package com.codemitry.scanme.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.scanme.BarcodeDataAdapter
import com.codemitry.scanme.R
import com.codemitry.scanme.wifiEncryptions
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout


class FormatFragment(private val onChangeValidityInput: (isInputValid: Boolean) -> Unit) : Fragment() {

    private lateinit var formatCard: CardView
    private lateinit var clearFormatButton: MaterialButton

    private lateinit var dataCard: CardView

    // qrcode code fields
    private var format: Formats? = null
    private var data: FormattedData? = null

    private var isInputValid = false
        set(value) {
            onChangeValidityInput(value)
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_format, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatCard = view.findViewById(R.id.formatCard)
        clearFormatButton = view.findViewById(R.id.formatButton)
        dataCard = view.findViewById(R.id.dataCard)

        // set click listeners to all buttons from format selector
        for (child in view.findViewById<FlexboxLayout>(R.id.flexFormat).children) {
            if (child is Button) {
                child.setOnClickListener(::onFormatClick)
            }

            // init state
            onChangeValidityInput(false)
        }


        if (data != null) {
            formatCard.visibility = View.GONE
            formatCard.animate()
                    .translationXBy(-700f)
                    .setDuration(0)
                    .start()
            showDataCard(format!!); BarcodeDataAdapter.fillLayout(data!!, dataCard)
            showClearFormatButton()
        } else if (format != null) {
            showFormatCard()
        }
    }

    fun getData(): FormattedData {
        return BarcodeDataAdapter(dataCard, format ?: Formats.TEXT).formatted
    }

    private fun onFormatClick(v: View) {
        if (v is MaterialButton) {

            format = when (v.id) {
                R.id.text -> Formats.TEXT
                R.id.link -> Formats.URL
                R.id.wifi -> Formats.WIFI
                R.id.email -> Formats.EMAIL
                R.id.sms -> Formats.SMS
                R.id.vcard -> Formats.CONTACT_INFO
                R.id.location -> Formats.LOCATION
                else -> error("Unreachable state")
            }

            hideFormatCard { showDataCard(format!!) }

            clearFormatButton.text = getFormatName(format!!)

            showClearFormatButton()
        }
    }

    private fun showFormatCard() {
        formatCard.visibility = View.VISIBLE

        formatCard.animate()
                .translationXBy(700f)
                .setDuration(500)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }

    private fun hideFormatCard(endAction: (() -> Unit)?) {
        formatCard.animate()
                .translationXBy(-700f)
                .setDuration(500)
                .scaleX(0.75f)
                .scaleY(0.75f)
                .alpha(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    formatCard.visibility = View.GONE
                    if (endAction != null) endAction()
                }
                .start()
    }

    private fun getFormatName(format: Formats): String = when (format) {
        Formats.TEXT -> getString(R.string.text)
        Formats.URL -> getString(R.string.link)
        Formats.WIFI -> getString(R.string.wifi)
        Formats.EMAIL -> getString(R.string.email)
        Formats.SMS -> getString(R.string.sms)
        Formats.CONTACT_INFO -> getString(R.string.vcard)
        Formats.LOCATION -> getString(R.string.location)
        else -> error("Unreachable situation")
    }

    private fun showDataCard(qrFormat: Formats) {

        val layout = when (qrFormat) {
            Formats.TEXT -> R.layout.input_text
            Formats.URL -> R.layout.input_url
            Formats.WIFI -> R.layout.input_wifi
            Formats.EMAIL -> R.layout.input_email
            Formats.SMS -> R.layout.input_sms
            Formats.CONTACT_INFO -> R.layout.input_vcard
            Formats.LOCATION -> R.layout.input_location

            else -> R.layout.input_text // TODO: Fix layout
        }

        layoutInflater.inflate(layout, dataCard)

        configNextButtonState(qrFormat)

        dataCard.visibility = View.VISIBLE

        dataCard.animate()
                .translationX(700f)
                .setDuration(0)
                .alpha(0f)
                .start()
        dataCard.animate()
                .translationXBy(-700f)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }

    private fun hideDataCard(endAction: (() -> Unit)?) {
        dataCard.animate()
                .translationXBy(700f)
                .setDuration(500)
                .alpha(0f)
                .withEndAction {
                    dataCard.visibility = View.GONE
                    dataCard.removeAllViews()
                    if (endAction != null)
                        endAction()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }


    private fun showClearFormatButton() {
        clearFormatButton.text = getFormatName(format!!)

        clearFormatButton.visibility = View.VISIBLE

        clearFormatButton.animate()
                .translationX(700f)
                .setDuration(0)
                .start()
        clearFormatButton.animate()
                .translationXBy(-700f)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { clearFormatButton.setOnClickListener { onClearFormatButtonClick() } }
                .start()
    }

    private fun hideFormatButton() {
        clearFormatButton.animate()
                .translationXBy(700f)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { clearFormatButton.visibility = View.GONE }
                .start()
    }

    // when format already chosen and user clicks to clear format
    private fun onClearFormatButtonClick() {
        format = null
        data = null

        isInputValid = false

        hideDataCard(::showFormatCard)
        hideFormatButton()
        clearFormatButton.setOnClickListener { }

    }


    private fun configNextButtonState(format: Formats) {
        when (format) {
            Formats.TEXT -> {
                dataCard.findViewById<TextInputLayout>(R.id.textInput).editText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0
                    }

                })
            }
            Formats.URL -> {
                dataCard.findViewById<TextInputLayout>(R.id.linkInput).editText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0
                    }

                })
            }
            Formats.WIFI -> {
                // TODO: Fix troubles with list items backgrounds and selected edittext background
                val autocompleteTextEncryption = dataCard.findViewById<TextInputLayout>(R.id.encryptionInput).editText as MaterialAutoCompleteTextView

                ArrayAdapter(context
                        ?: requireContext(), android.R.layout.simple_dropdown_item_1line, wifiEncryptions.values.toList()).also { adapter ->
                    autocompleteTextEncryption.setAdapter(adapter)
                }

                val ssidEditText = dataCard.findViewById<TextInputLayout>(R.id.ssidInput).editText

                ssidEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0 && autocompleteTextEncryption.text.isNotEmpty()
                    }

                })

                autocompleteTextEncryption.onItemClickListener = object : AdapterView.OnItemClickListener {
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        isInputValid = ssidEditText?.text?.length ?: 0 > 0
                    }

                }
            }
            Formats.EMAIL -> {
                val addressEditText = dataCard.findViewById<TextInputLayout>(R.id.addressInput).editText
                val messageEditText = dataCard.findViewById<TextInputLayout>(R.id.messageInput).editText

                addressEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = (text?.length ?: 0 > 0 && messageEditText?.text?.isNotEmpty() ?: false)
                    }
                })

                messageEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = (text?.length ?: 0 > 0 && addressEditText?.text?.isNotEmpty() ?: false)
                    }
                })
            }
            Formats.SMS -> {
                dataCard.findViewById<TextInputLayout>(R.id.messageInput).editText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0
                    }

                })
            }
            Formats.CONTACT_INFO -> {
                dataCard.findViewById<TextInputLayout>(R.id.nameInput).editText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0
                    }

                })
            }
            Formats.LOCATION -> {
                val latitudeEditText = dataCard.findViewById<TextInputLayout>(R.id.latitudeInput).editText
                val longitudeEditText = dataCard.findViewById<TextInputLayout>(R.id.longitudeInput).editText

                latitudeEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        val validFormat = (if (text?.isNotEmpty() == true) text.toString().toDouble() else 0.0) in -90.0..90.0
                        latitudeEditText.error = if (!validFormat)
                            getString(R.string.incorrect_latitude)
                        else
                            null

                        isInputValid = (text?.length ?: 0 > 0 && longitudeEditText?.text?.isNotEmpty() ?: false && validFormat)
                    }
                })

                longitudeEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        val validFormat = (if (text?.isNotEmpty() == true) text.toString().toDouble() else 0.0) in -180.0..180.0
                        longitudeEditText.error = if (!validFormat)
                            getString(R.string.incorrect_longitude)
                        else
                            null

                        isInputValid = (text?.length ?: 0 > 0 && latitudeEditText?.text?.isNotEmpty() ?: false && validFormat)
                    }
                })
            }

            else -> {
            }
        } // TODO: Fix setting input
    }
}