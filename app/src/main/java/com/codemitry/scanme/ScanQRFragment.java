package com.codemitry.scanme;

import android.Manifest;
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

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ScanQRFragment extends Fragment {

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSIONS = 9182;

    private static final String FLASH_KEY = "Flash";
    private ImageButton flashButton;
    private boolean isFlashEnabled = false;

    private androidx.camera.core.Camera camera;

    private GraphicOverlay graphicOverlay;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FLASH_KEY, isFlashEnabled);
    }

    @Override
    public void onPause() {
        super.onPause();

        // disable the flash is required
        if (isFlashEnabled)
            camera.getCameraControl().enableTorch(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // enable the flash if it's true
        if (camera != null)
            camera.getCameraControl().enableTorch(isFlashEnabled);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_qr, container, false);
        flashButton = view.findViewById(R.id.flash);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(FLASH_KEY)) {
            isFlashEnabled = savedInstanceState.getBoolean(FLASH_KEY);
        }

        assert getActivity() != null; // *** assert ***

        if (allPermissionsGranted()) {
            startCamera();
            this.flashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFlashEnabled = !isFlashEnabled;
                    onFlashChanged(isFlashEnabled);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
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

            assert getContext() != null;  // *** assert ***

            int permissionStatus = ContextCompat.checkSelfPermission(getContext(), permission);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    private void startCamera() {

        assert getContext() != null;  // *** assert ***
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                cameraProvider.unbindAll();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

                camera = cameraProvider.bindToLifecycle(ScanQRFragment.this, cameraSelector, preview, imageAnalysis);
                onFlashChanged(isFlashEnabled);

                assert getActivity() != null;  // *** assert ***

                PreviewView previewView = getActivity().findViewById(R.id.previewView);
                preview.setSurfaceProvider(previewView.createSurfaceProvider());
                System.out.println("width: ");
                // the default resolution for camera2 is 640x480 for landscape and 480x640 for portrait
                graphicOverlay.setPreviewSize(480, 640);


                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getContext()), new BarcodeAnalyzer(graphicOverlay));
                System.out.println("analyzer orientation: " + imageAnalysis.getTargetRotation());
                System.out.println("preview orientation: " + preview.getTargetRotation());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void onFlashChanged(boolean isFlashEnabled) {
        if (isFlashEnabled) {
            flashButton.setImageResource(R.drawable.flash_on);
        } else {
            flashButton.setImageResource(R.drawable.flash_off);
        }
        camera.getCameraControl().enableTorch(isFlashEnabled);
    }

}