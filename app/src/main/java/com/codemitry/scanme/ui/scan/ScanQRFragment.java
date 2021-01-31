package com.codemitry.scanme.ui.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.codemitry.scanme.OnHistoryClickListener;
import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.BarcodeAdapter;
import com.codemitry.scanme.barcode.BarcodeAnalyzer;
import com.codemitry.scanme.barcode.GraphicOverlay;
import com.codemitry.scanme.history.HistoryAction;
import com.codemitry.scanme.history.HistoryActionsManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.concurrent.ExecutionException;

public class ScanQRFragment extends Fragment implements BarcodeAnalyzer.OnChangeStatesListener {

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSIONS = 9182;
    private static final int REQUEST_PICK_IMAGE = 9192;

    private static final String FLASH_KEY = "Flash";
    private ImageButton flashButton;
    private boolean isFlashEnabled = false;

    private androidx.camera.core.Camera camera;

    private GraphicOverlay graphicOverlay;

    private OnHistoryClickListener onHistoryClickListener;

    private boolean isPreviewStarted;

    private HistoryActionsManager historyActionsManager;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FLASH_KEY, isFlashEnabled);
    }


    @Override
    public void onStart() {
        super.onStart();

        // enable the flash if it's true
        if (camera != null)
            camera.getCameraControl().enableTorch(isFlashEnabled);

    }

    @Override
    public void onStop() {
        super.onStop();

        // disable the flash is required
        if (isFlashEnabled)
            camera.getCameraControl().enableTorch(false);

//        stopCamera();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_qr, container, false);
        flashButton = view.findViewById(R.id.flash);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);
        // должно убрать черный квадрат при сканировании
        graphicOverlay.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.findViewById(R.id.history).setOnClickListener((View v) -> onHistoryClickListener.onHistoryClick());

        historyActionsManager = new ViewModelProvider(requireActivity()).get(HistoryActionsManager.class);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(FLASH_KEY)) {
            isFlashEnabled = savedInstanceState.getBoolean(FLASH_KEY);
        }

//        assert getActivity() != null; // *** assert ***

        if (allPermissionsGranted()) {
            startCamera();
            this.flashButton.setOnClickListener((View view) -> {
                isFlashEnabled = !isFlashEnabled;
                onFlashChanged(isFlashEnabled);
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.attach).setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getContext(), R.string.permissions_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {

//            assert getContext() != null;  // *** assert ***

            int permissionStatus = ContextCompat.checkSelfPermission(requireContext(), permission);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    ProcessCameraProvider cameraProvider;
    CameraSelector cameraSelector;
    Preview preview;
    ImageAnalysis imageAnalysis;

    private void startCamera() {

        assert getContext() != null;  // *** assert ***
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                preview = new Preview.Builder().build();
                cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                cameraProvider.unbindAll();

                imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

                camera = cameraProvider.bindToLifecycle(ScanQRFragment.this, cameraSelector, preview, imageAnalysis);
                isPreviewStarted = true;
                onFlashChanged(isFlashEnabled);

                assert getActivity() != null;  // *** assert ***

                PreviewView previewView = getActivity().findViewById(R.id.previewView);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // the default resolution for camera2 is 640x480 for landscape and 480x640 for portrait
                graphicOverlay.setPreviewSize(480, 640);


                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getContext()), new BarcodeAnalyzer(graphicOverlay, this));

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

//    private void stopPreview() {
//        cameraProvider.unbind(preview);
//        isPreviewStarted = false;
//    }

    private void startPreview() {
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        isPreviewStarted = true;
    }

    private void stopCamera() {
//        cameraProvider.unbindAll();
        cameraProvider.unbind(preview, imageAnalysis);
    }

    private void onFlashChanged(boolean isFlashEnabled) {
        if (isFlashEnabled) {
            flashButton.setImageResource(R.drawable.flash_on);
        } else {
            flashButton.setImageResource(R.drawable.flash_off);
        }
        camera.getCameraControl().enableTorch(isFlashEnabled);
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.onHistoryClickListener = listener;
    }

    @Override
    public void onChangeState(BarcodeAnalyzer.States newState) {
        switch (newState) {
            case NOT_STARTED:
            case DETECTED:
                if (isPreviewStarted)
                    stopCamera();
                break;
            case DETECTING:
                if (!isPreviewStarted)
                    startPreview();
                break;
//            case DETECTED:
//                if (isPreviewStarted)
//                    stopCamera();
//                break;
        }
    }

    @Override
    public void onBarcodeSearched(Barcode barcode) {
        requireActivity().runOnUiThread(() -> {
            startBarcodeResultFragment(BarcodeAdapter.Companion.barcode(barcode));
            historyActionsManager.addHistoryAction(new HistoryAction(HistoryAction.Actions.SCAN, barcode));
            historyActionsManager.saveHistoryActions();
        });

    }

    private void startBarcodeResultFragment(com.codemitry.qr_code_generator_lib.qrcode.Barcode barcode) {
        BarcodeResultFragment barcodeResultFragment = new BarcodeResultFragment(barcode);
        barcodeResultFragment.setOnCancelListener(this::startCamera);

        barcodeResultFragment.show(getChildFragmentManager(), barcodeResultFragment.getClass().getSimpleName());
    }
}