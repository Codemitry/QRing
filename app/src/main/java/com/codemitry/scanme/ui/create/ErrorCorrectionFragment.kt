package com.codemitry.scanme.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.scanme.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class ErrorCorrectionFragment(private val onCorrectionChosen: (correction: ErrorCorrectionLevels) -> Unit) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_correction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.helpCorrection).setOnClickListener {
            HelpCorrectionFragment().show(requireFragmentManager(), HelpCorrectionFragment::class.simpleName)
        }

        view.findViewById<MaterialButtonToggleGroup>(R.id.correctionButtonGroup).addOnButtonCheckedListener(::onCorrectionChecked)
    }

    private fun onCorrectionChecked(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        if (isChecked) {
            when (group.findViewById<Button>(checkedId).tag) {
                "L" -> onCorrectionChosen(ErrorCorrectionLevels.L)
                "M" -> onCorrectionChosen(ErrorCorrectionLevels.M)
                "Q" -> onCorrectionChosen(ErrorCorrectionLevels.Q)
                "H" -> onCorrectionChosen(ErrorCorrectionLevels.H)
            }
        }
    }
}