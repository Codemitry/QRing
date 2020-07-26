package com.codemitry.scanme;

import android.annotation.SuppressLint;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private BarcodeScanner scanner;
    private GraphicOverlay graphicOverlay;

    public BarcodeAnalyzer(GraphicOverlay graphicOverlay) {
        scanner = BarcodeScanning.getClient();
        this.graphicOverlay = graphicOverlay;

    }


    @Override
    public void analyze(@NonNull ImageProxy image) {
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();


        assert mediaImage != null;
        scanner.process(InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees()))
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        graphicOverlay.clear();
                        if (barcodes.size() > 0) {
                            System.out.println("Complete!");
                            for (Barcode barcode : barcodes) {
                                RectOverlay barcodeOverlay = new RectOverlay(graphicOverlay, barcode.getBoundingBox());
                                graphicOverlay.add(barcodeOverlay);
                                System.out.println("type: " + barcode.getValueType());
                                System.out.println("info: " + barcode.getDisplayValue());

                                System.out.println("rect: " + barcode.getBoundingBox().left + "; " + barcode.getBoundingBox().top + " - " + barcode.getBoundingBox().right + "; " + barcode.getBoundingBox().bottom);
                            }

                        }
                        image.close();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failure!");
                        Toast.makeText(graphicOverlay.getContext(), "Unable to detect the bardcode!", Toast.LENGTH_SHORT).show();

                        image.close();
                    }
                });
    }
}
