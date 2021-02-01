package com.codemitry.scanme.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codemitry.scanme.OnHistoryClickListener
import com.codemitry.scanme.R
import com.codemitry.scanme.barcode.BarcodeAdapter.Companion.barcode
import com.codemitry.scanme.barcode.BarcodeAnalyzer
import com.codemitry.scanme.barcode.BarcodeAnalyzer.OnChangeStatesListener
import com.codemitry.scanme.barcode.BarcodeAnalyzer.States
import com.codemitry.scanme.barcode.GraphicOverlay
import com.codemitry.scanme.history.HistoryAction
import com.codemitry.scanme.history.HistoryActionsManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
const val REQUEST_CODE_CAMERA = 9182
const val REQUEST_PICK_IMAGE = 9192

class ScanFragment : Fragment(), OnChangeStatesListener {

    private var flashButton: ImageButton? = null
    private var isFlashEnabled = false

    private var camera: Camera? = null

    private var historyActionsManager: HistoryActionsManager? = null

    var onHistoryClickListener: OnHistoryClickListener? = null

    private var graphicOverlay: GraphicOverlay? = null
    private var isCameraStarted = false


    private var previewView: PreviewView? = null
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var analyzer: BarcodeAnalyzer? = null

    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    private var imageView: ImageView? = null

    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    override fun onPause() {
        isFlashEnabled = false
        onFlashChanged(false)

        super.onPause()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.galleryImage)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageView?.visibility = View.VISIBLE
                imageView?.setImageURI(result.data?.data ?: Uri.EMPTY)

                imageView?.let { imageView ->
                    analyzer?.process(InputImage.fromBitmap(imageView.drawable.toBitmap(imageView.width, imageView.height, null), 0), null)
                }

            } else {
                bindCameraUseCases()
            }
        }

        view.findViewById<View>(R.id.attach).setOnClickListener {
            stopCamera()

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image_with_qr_code))
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            resultLauncher.launch(chooserIntent)
//            ActivityCompat.startActivityForResult(requireActivity(), chooserIntent, REQUEST_PICK_IMAGE, null)
        }

        historyActionsManager = ViewModelProvider(requireActivity()).get(HistoryActionsManager::class.java)

        graphicOverlay = view.findViewById(R.id.graphicOverlay)
        // должно убрать черный квадрат при сканировании
        graphicOverlay?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        flashButton = view.findViewById(R.id.flash)

        view.findViewById<View>(R.id.history).setOnClickListener { onHistoryClickListener?.onHistoryClick() }


        previewView = view.findViewById(R.id.previewView)

        if (allPermissionsGranted()) {
            setupCamera()

            flashButton?.setOnClickListener {
                isFlashEnabled = !isFlashEnabled
                onFlashChanged(isFlashEnabled)
            }

        } else {
            requestCameraPermissions()
        }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreview() {
        camera = cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */this,
                cameraSelector!!,
                previewUseCase
        )
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        graphicOverlay?.setPreviewSize(480, 640)

        previewUseCase = Preview.Builder()
                // called crashes on Samsung Galaxy A7
//                .setTargetAspectRatio(screenAspectRatio)
//                .setTargetRotation(previewView!!.display.rotation)
                .build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
            bindPreview()
        } catch (illegalStateException: IllegalStateException) {
            Log.e("Exception", illegalStateException.message ?: "")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e("Exception", illegalArgumentException.message ?: "")
        }
    }

    private fun bindAnalyse() {
        camera = cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */this,
                cameraSelector!!,
                analysisUseCase
        )
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder()
                //  called crashes on Samsung Galaxy A7
//                .setTargetAspectRatio(screenAspectRatio)
//                .setTargetRotation(previewView!!.display.rotation)
                .build()


        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analyzer = BarcodeAnalyzer(graphicOverlay!!, this)
        analysisUseCase?.setAnalyzer(cameraExecutor, analyzer!!)

        try {
            bindAnalyse()
        } catch (illegalStateException: IllegalStateException) {
            Log.e("Exception", illegalStateException.message ?: "")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e("Exception", illegalArgumentException.message ?: "")
        }
    }


    private fun setupCamera() {
        val lensFacing = CameraSelector.LENS_FACING_BACK

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        ViewModelProvider(
                this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CameraXViewModel::class.java)
                .processCameraProvider
                .observe(viewLifecycleOwner, { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (allPermissionsGranted()) {
                        bindCameraUseCases()
                    } else {
                        requestCameraPermissions()
                    }
                }
                )

        isCameraStarted = true
    }

    private fun stopCamera() {
//        cameraProvider.unbindAll();
        cameraProvider!!.unbind(previewUseCase, analysisUseCase)
        isCameraStarted = false
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_CAMERA)

    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onChangeState(newState: States?) {
        when (newState) {
            States.NOT_STARTED, States.DETECTED -> if (isCameraStarted) stopCamera()
            States.DETECTING -> if (!isCameraStarted) {
                setupCamera()
            }
        }
    }

    override fun onBarcodeNotFound() {
        Toast.makeText(requireContext(), getString(R.string.qr_code_not_found), Toast.LENGTH_SHORT).show()
        imageView?.setImageURI(Uri.EMPTY)
        imageView?.visibility = View.INVISIBLE

        bindCameraUseCases()
    }

    override fun onBarcodeSearched(barcode: Barcode?) {
        requireActivity().runOnUiThread {
            barcode?.let { barcode ->
                startBarcodeResultFragment(barcode(barcode))
                historyActionsManager?.addHistoryAction(HistoryAction(HistoryAction.Actions.SCAN, barcode))
                historyActionsManager?.saveHistoryActions()
                return@runOnUiThread
            }

        }
    }

    private fun startBarcodeResultFragment(barcode: com.codemitry.qr_code_generator_lib.qrcode.Barcode) {
        val barcodeResultFragment = BarcodeResultFragment(barcode)
        barcodeResultFragment.onCancelListener = object : OnCancelListener {
            override fun onCancel() {

                imageView?.visibility = View.INVISIBLE
                imageView?.setImageURI(Uri.EMPTY)

                bindPreview()
                bindAnalyse()
                isCameraStarted = true

            }
        }
        barcodeResultFragment.show(childFragmentManager, barcodeResultFragment.javaClass.simpleName)
    }

    private fun onFlashChanged(isFlashEnabled: Boolean) {
        flashButton?.setImageResource(if (isFlashEnabled) R.drawable.flash_on else R.drawable.flash_off)
        camera?.cameraControl?.enableTorch(isFlashEnabled)
    }
}


private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0