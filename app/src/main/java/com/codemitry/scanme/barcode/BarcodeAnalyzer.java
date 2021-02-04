package com.codemitry.scanme.barcode;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.Timer;
import java.util.TimerTask;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    public enum States {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        SEARCHING,
        SEARCHED
    }

    public void process(InputImage image, ImageProxy imageProxy) {
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    graphicOverlay.clear();

                    if (barcodes.size() <= 0) {
                        graphicOverlay.add(new BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator));
                        cameraReticleAnimator.start();

                        // scanning image from gallery
                        if (imageProxy == null) {
                            onChangeStatesListener.onBarcodeNotFound();
                        }


                        if (state != States.DETECTING)
                            changeState(States.DETECTING);

                    } else {
                        cameraReticleAnimator.cancel();

                        changeState(States.DETECTED);

                        Barcode barcode = barcodes.get(0);
                        ValueAnimator searchingAnimator = createSearchingAnimator(graphicOverlay, barcode);
                        searchingAnimator.start();

                        graphicOverlay.add(new BarcodeSearchingGraphic(graphicOverlay, searchingAnimator, barcode.getBoundingBox(), imageProxy == null));
                        changeState(States.SEARCHING);
                        searchBarcode(barcode);

                    }
                    if (imageProxy != null)
                        imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(graphicOverlay.getContext(), "Unable to detect the barcode!", Toast.LENGTH_SHORT).show();
                    onChangeStatesListener.onBarcodeSearched(null);
                    if (imageProxy != null)
                        imageProxy.close();
                });
    }

    private States state;
    private OnChangeStatesListener onChangeStatesListener;

    private BarcodeScanner scanner;
    private GraphicOverlay graphicOverlay;

    private CameraReticleAnimator cameraReticleAnimator;

    public BarcodeAnalyzer(GraphicOverlay graphicOverlay, OnChangeStatesListener changeStatesListener) {
        scanner = BarcodeScanning.getClient();
        this.graphicOverlay = graphicOverlay;
        this.onChangeStatesListener = changeStatesListener;

        cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
    }


    private static final int SEARCH_DELAY = 500;

    private ValueAnimator createSearchingAnimator(GraphicOverlay graphicOverlay, Barcode barcode) {
        float endProgress = 1.1f;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, endProgress);
        valueAnimator.setDuration(SEARCH_DELAY);
        valueAnimator.addUpdateListener((ValueAnimator animator) -> {
            if ((float) (animator.getAnimatedValue()) >= endProgress) {
                graphicOverlay.clear();
            } else {
                graphicOverlay.invalidate();
            }
        });
        return valueAnimator;
    }

//    private fun createLoadingAnimator(graphicOverlay: GraphicOverlay, barcode: Barcode): ValueAnimator {
//        val endProgress = 1.1f
//        return ValueAnimator.ofFloat(0f, endProgress).apply {
//            duration = 2000
//            addUpdateListener {
//                if ((animatedValue as Float).compareTo(endProgress) >= 0) {
//                    graphicOverlay.clear()
//                    workflowModel.setWorkflowState(WorkflowState.SEARCHED)
//                    workflowModel.detectedBarcode.setValue(barcode)
//                } else {
//                    graphicOverlay.invalidate()
//                }
//            }
//        }
//    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();

        assert mediaImage != null;
        process(InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees()), image);
    }

    public interface OnChangeStatesListener {
        void onChangeState(States newState);

        void onBarcodeSearched(Barcode barcode);

        void onBarcodeNotFound();
    }

    private void searchBarcode(Barcode barcode) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onChangeStatesListener.onBarcodeSearched(barcode);

                changeState(States.SEARCHED);
            }
        }, SEARCH_DELAY);
    }

    private void changeState(States newState) {
        this.state = newState;
        onChangeStatesListener.onChangeState(newState);
    }
}
