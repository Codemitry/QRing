package com.codemitry.scanme.history

import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.scanme.R
import com.codemitry.scanme.barcode.BarcodeAdapter
import java.io.Serializable

data class HistoryAction(val action: Actions, val barcode: Barcode) : Serializable {

    constructor(action: Actions, barcode: com.google.mlkit.vision.barcode.Barcode) : this(action, BarcodeAdapter.barcode((barcode)))


    enum class Actions {
        SCAN, CREATE;

        companion object {
            fun getString(action: Actions) =
                    when (action) {
                        SCAN -> R.string.qr_scanning
                        CREATE -> R.string.qr_creation
                    }
        }
    }

}