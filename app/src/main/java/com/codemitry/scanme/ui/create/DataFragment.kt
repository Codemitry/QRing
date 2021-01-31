package com.codemitry.scanme.ui.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.scanme.BarcodeDataAdapter
import com.codemitry.scanme.R
import com.codemitry.scanme.wifiEncryptions
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class DataFragment(private val onChangeValidityInput: (isInputValid: Boolean) -> Unit) : Fragment() {

    private var isInputValid = false
        set(value) {
            onChangeValidityInput(value)
            field = value
        }

    private var dataCard: CardView? = null
    var format: Formats? = null
        set(value) {
            field = value

            if (value == null) {
                data = null
                onChangeValidityInput(false)
            }
//        if (format != null) {
//            layoutInflater.inflate(layoutFor(format!!), dataCard)
//            configInputValidity(format!!)
//        }
        }

    private var data: FormattedData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataCard = view.findViewById(R.id.dataCard)


        format?.let {
            layoutInflater.inflate(layoutFor(format!!), dataCard)
            configInputValidity(format!!)
        }

        data?.let { data -> dataCard?.let { dataCard -> BarcodeDataAdapter.fillLayout(data, dataCard) } }

    }

    fun getData(): FormattedData? {
        format?.let { format -> dataCard?.let { dataCard -> return@getData BarcodeDataAdapter(dataCard, format).formatted } }

        println("YOU SHOULD NOT BE HERE!!!!")
        println("YOU SHOULD NOT BE HERE!!!!")
        println("YOU SHOULD NOT BE HERE!!!!")
        return null
    }

    override fun onPause() {
        super.onPause()

        if (format != null)
            data = getData()
    }

    private fun layoutFor(format: Formats): Int = when (format) {
        Formats.TEXT -> R.layout.input_text
        Formats.URL -> R.layout.input_url
        Formats.WIFI -> R.layout.input_wifi
        Formats.EMAIL -> R.layout.input_email
        Formats.SMS -> R.layout.input_sms
        Formats.CONTACT_INFO -> R.layout.input_vcard
        Formats.LOCATION -> R.layout.input_location

        else -> R.layout.input_text // TODO: Fix layout
    }

    private fun configInputValidity(format: Formats) {
        when (format) {
            Formats.TEXT -> {
                dataCard?.findViewById<TextInputLayout>(R.id.textInput)?.editText?.addTextChangedListener(object : TextWatcher {
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
                dataCard?.findViewById<TextInputLayout>(R.id.linkInput)?.editText?.addTextChangedListener(object : TextWatcher {
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
                val autocompleteTextEncryption = dataCard?.findViewById<TextInputLayout>(R.id.encryptionInput)?.editText as MaterialAutoCompleteTextView

                ArrayAdapter(context
                        ?: requireContext(), android.R.layout.simple_dropdown_item_1line, wifiEncryptions.values.toList()).also { adapter ->
                    autocompleteTextEncryption.setAdapter(adapter)
                }

                val ssidEditText = dataCard?.findViewById<TextInputLayout>(R.id.ssidInput)?.editText

                ssidEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(text: Editable?) {
                        isInputValid = text?.length ?: 0 > 0 && autocompleteTextEncryption.text.isNotEmpty()
                    }

                })

                autocompleteTextEncryption.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                    isInputValid = ssidEditText?.text?.length ?: 0 > 0
                }
            }
            Formats.EMAIL -> {
                val addressEditText = dataCard?.findViewById<TextInputLayout>(R.id.addressInput)?.editText
                val messageEditText = dataCard?.findViewById<TextInputLayout>(R.id.messageInput)?.editText

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
                dataCard?.findViewById<TextInputLayout>(R.id.messageInput)?.editText?.addTextChangedListener(object : TextWatcher {
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
                dataCard?.findViewById<TextInputLayout>(R.id.nameInput)?.editText?.addTextChangedListener(object : TextWatcher {
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
                val latitudeEditText = dataCard?.findViewById<TextInputLayout>(R.id.latitudeInput)?.editText
                val longitudeEditText = dataCard?.findViewById<TextInputLayout>(R.id.longitudeInput)?.editText

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