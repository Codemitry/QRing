package com.codemitry.scanme.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.Barcode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.google.mlkit.vision.barcode.Barcode;

public class BarcodeResultFragment extends BottomSheetDialogFragment {

    private Barcode barcode;

    private OnCancelListener onCancelListener;

    public BarcodeResultFragment(Barcode barcode) {
        this.barcode = barcode;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        if (this.onCancelListener != null) {
            this.onCancelListener.onCancel();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        switch (barcode.getValueType()) {
            case Barcode.TEXT:
                view = inflater.inflate(R.layout.text, container, false);
                ((TextView) view.findViewById(R.id.text)).setText(barcode.getDisplayValue());
                break;

            default:
                // TODO: убрать это
                view = inflater.inflate(R.layout.text, container, false);
        }
        return view;
    }

    public void setOnCancelListener(OnCancelListener cancelListener) {
        this.onCancelListener = cancelListener;
    }

    public interface OnCancelListener {
        void onCancel();
    }
}