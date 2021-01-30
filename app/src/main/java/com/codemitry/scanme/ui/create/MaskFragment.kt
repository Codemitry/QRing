package com.codemitry.scanme.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.codemitry.scanme.R
import com.google.android.material.button.MaterialButton

class MaskFragment(private val onMaskChosen: (mask: Int) -> Unit) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mask, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.helpMask).setOnClickListener {
            // TODO: Show normal help
            HelpCorrectionFragment().show(requireFragmentManager(), HelpCorrectionFragment::class.simpleName)
        }

        val picker = view.findViewById<NumberPicker>(R.id.maskPicker)

        picker.minValue = 1
        picker.maxValue = 7

        picker.setOnValueChangedListener { picker, old, new ->
            if (old != new)
                onMaskChosen(new)
        }

        val noMatterCheckbox = view.findViewById<CheckBox>(R.id.no_matter_checkbox)

        noMatterCheckbox.setOnCheckedChangeListener { compoundButton, checked ->
            println("called")
            picker.isEnabled = !checked

            // TODO: If mask no matter, default mask is 1. Redo
            if (checked)
                onMaskChosen(1)
        }

    }

}