package com.codemitry.scanme.history;

import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.Barcode;

import java.io.Serializable;


public class HistoryAction implements Serializable {
    private Actions action;
    private Barcode barcode;

    public HistoryAction(Actions action, Barcode barcode) {
        this.action = action;
        this.barcode = barcode;
    }

    public HistoryAction(Actions action, com.google.mlkit.vision.barcode.Barcode barcode) {
        this(action, Barcode.getBarcode(barcode));
    }

    public enum Actions {
        SCAN, CREATE;

        public static int getString(Actions action) {
            switch (action) {
                case SCAN:
                    return R.string.qr_scanning;
                case CREATE:
                    return R.string.qr_creation;
                default:
                    return -1;
            }
        }
    }

    public Actions getAction() {
        return action;
    }

    public Barcode getBarcode() {
        return barcode;
    }

}
