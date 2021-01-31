package com.codemitry.scanme.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.scanme.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton


class FormatFragment(private val onFormatSelected: (format: Formats) -> Unit) : Fragment() {

    private lateinit var formatCard: CardView
//    private lateinit var clearFormatButton: MaterialButton

//    private lateinit var dataCard: CardView

    // qrcode code fields
    private var format: Formats? = null
//    private var data: FormattedData? = null

//        set(value) {
//            onChangeValidityInput(value)
//            field = value
//        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_format, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatCard = view.findViewById(R.id.formatCard)
//        clearFormatButton = view.findViewById(R.id.formatButton)

        // set click listeners to all buttons from format selector
        for (child in view.findViewById<FlexboxLayout>(R.id.flexFormat).children) {
            if (child is Button) {
                child.setOnClickListener(::onFormatClick)
            }

            // init state
//            onChangeValidityInput(false)
        }


//        if (data != null) {
//            formatCard.visibility = View.GONE
//            formatCard.animate()
//                    .translationXBy(-700f)
//                    .setDuration(0)
//                    .start()
//            showDataCard(format!!); BarcodeDataAdapter.fillLayout(data!!, dataCard)
//            showClearFormatButton()
//        } else if (format != null) {
//            hideFormatCard { showDataCard(format!!) }
//            showClearFormatButton()
//        }
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
            }.also {
                onFormatSelected(it)
            }

//            hideFormatCard {
//                showDataCard(format!!)
//            }

//            clearFormatButton.text = getFormatName(format!!)

//            showClearFormatButton()
        }
    }

//    private fun showFormatCard() {
//        formatCard.visibility = View.VISIBLE

//        formatCard.animate()
//                .translationXBy(700f)
//                .setDuration(500)
//                .scaleX(1f)
//                .scaleY(1f)
//                .alpha(1f)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .start()
//    }

//    private fun hideFormatCard(endAction: (() -> Unit)?) {
//        formatCard.animate()
//                .translationXBy(-700f)
//                .setDuration(500)
//                .scaleX(0.75f)
//                .scaleY(0.75f)
//                .alpha(0f)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .withEndAction {
//                    formatCard.visibility = View.GONE
//                    if (endAction != null) endAction()
//                }
//                .start()
//    }

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

//    private fun showDataCard(qrFormat: Formats) {
//
//        val layout = when (qrFormat) {
//            Formats.TEXT -> R.layout.input_text
//            Formats.URL -> R.layout.input_url
//            Formats.WIFI -> R.layout.input_wifi
//            Formats.EMAIL -> R.layout.input_email
//            Formats.SMS -> R.layout.input_sms
//            Formats.CONTACT_INFO -> R.layout.input_vcard
//            Formats.LOCATION -> R.layout.input_location
//
//            else -> R.layout.input_text // TODO: Fix layout
//        }
//
////        layoutInflater.inflate(layout, dataCard)
//
//        configNextButtonState(qrFormat)
//
////        dataCard.visibility = View.VISIBLE
//
////        dataCard.animate()
////                .translationX(700f)
////                .setDuration(0)
////                .alpha(0f)
////                .start()
////        dataCard.animate()
////                .translationXBy(-700f)
////                .setDuration(500)
////                .alpha(1f)
////                .setInterpolator(AccelerateDecelerateInterpolator())
////                .start()
//    }

//    private fun hideDataCard(endAction: (() -> Unit)?) {
////        dataCard.animate()
////                .translationXBy(700f)
////                .setDuration(500)
////                .alpha(0f)
////                .withEndAction {
//                    dataCard.visibility = View.GONE
//                    dataCard.removeAllViews()
//                    if (endAction != null)
//                        endAction()
////                }
////                .setInterpolator(AccelerateDecelerateInterpolator())
////                .start()
//    }

//
//    private fun showClearFormatButton() {
//        clearFormatButton.text = getFormatName(format!!)
//
//        clearFormatButton.visibility = View.VISIBLE
//
////        clearFormatButton.animate()
////                .translationX(700f)
////                .setDuration(0)
////                .start()
////        clearFormatButton.animate()
////                .translationXBy(-700f)
////                .setDuration(500)
////                .setInterpolator(AccelerateDecelerateInterpolator())
////                .withEndAction {
//                    clearFormatButton.setOnClickListener { onClearFormatButtonClick() }
////                }
////                .start()
//    }

//    private fun hideFormatButton() {
////        clearFormatButton.animate()
////                .translationXBy(700f)
////                .setDuration(500)
////                .setInterpolator(AccelerateDecelerateInterpolator())
////                .withEndAction {
//            clearFormatButton.visibility = View.GONE
////                }
////                .start()
//    }

//    // when format already chosen and user clicks to clear format
//    private fun onClearFormatButtonClick() {
//        format = null
//        data = null
//
//        isInputValid = false
//
//        hideDataCard(::showFormatCard)
//        hideFormatButton()
//        clearFormatButton.setOnClickListener { }
//
//    }


}