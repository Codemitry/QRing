package com.codemitry.scanme;

import android.annotation.SuppressLint;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private BarcodeScanner scanner;

    public BarcodeAnalyzer() {
        scanner = BarcodeScanning.getClient();

    }


    @Override
    public void analyze(@NonNull ImageProxy image) {
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();


        assert mediaImage != null;
        Task<List<Barcode>> scanResult = scanner.process(InputImage.fromMediaImage(mediaImage, 0))
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.size() > 0) {
                            System.out.println("Complete!");
                            for (Barcode barcode : barcodes) {
                                System.out.println("type: " + barcode.getValueType());
                                System.out.println("info: " + barcode.getDisplayValue());
                            }

                        }
                        image.close();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failure!");

                        image.close();
                    }
                });
    }
}
