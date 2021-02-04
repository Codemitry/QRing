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

    private var format: Formats? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_format, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatCard = view.findViewById(R.id.formatCard)

        // set click listeners to all buttons from format selector
        for (child in view.findViewById<FlexboxLayout>(R.id.flexFormat).children) {
            if (child is Button) {
                child.setOnClickListener(::onFormatClick)
            }
        }
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

        }
    }

}