package com.codemitry.scanme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codemitry.scanme.R

class Dialog : DialogFragment() {

    private var actionTextView: TextView? = null
    private var buttonOk: Button? = null
    private var buttonCancel: Button? = null

    private var title: String? = null
    private var titleRes: Int? = null

    private var onConfirmAction: (() -> Unit)? = null
    private var onDismissAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionTextView = view.findViewById(R.id.textView1)

        actionTextView?.text = if (title != null) title else (if (titleRes != null) getString(titleRes!!) else "")

        buttonOk = view.findViewById(R.id.btnYes)
        buttonOk?.setOnClickListener { onConfirmAction?.invoke(); dialog?.dismiss() }
        buttonCancel = view.findViewById(R.id.btnNo)
        buttonCancel?.setOnClickListener { onDismissAction?.invoke(); dialog?.dismiss() }


        buttonCancel?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    fun setTitle(title: Int) {
        this.title = null
        titleRes = title
    }

    fun setTitle(title: String) {
        this.title = title
        titleRes = null
    }

    fun setOnConfirmClick(action: () -> Unit) {
        onConfirmAction = action
    }

    fun setOnDismissClick(action: () -> Unit) {
        onDismissAction = action
    }

}