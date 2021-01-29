package com.codemitry.scanme.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.codemitry.scanme.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_STORAGE_PERMISSION_SHARE = 1145
private const val REQUEST_STORAGE_PERMISSION_SAVE = 1245

class GeneratedQRCodeFragment(val qrCode: Bitmap) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generated_qr_code, container, false);

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.qrcode).setImageBitmap(qrCode)

        view.findViewById<Button>(R.id.share).setOnClickListener {
//            shareImage(qrCode)
        }

        view.findViewById<Button>(R.id.download).setOnClickListener {
            saveQrCode(qrCode)
        }
    }

    private fun requestStoragePermission(code: Int) {
        val requiredPermissions =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION)
                else
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        requestPermissions(requiredPermissions, code)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_STORAGE_PERMISSION_SHARE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                shareImage(qrCode)
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        } else
            if (requestCode == REQUEST_STORAGE_PERMISSION_SAVE) {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    saveQrCode(qrCode)
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun allStoragePermissionsGranted(): Boolean =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    (if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    else true) &&
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED
                    else true


    private fun shareImage(img: Bitmap) {

        if (!allStoragePermissionsGranted()) {

            requestStoragePermission(REQUEST_STORAGE_PERMISSION_SHARE)
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"

        val uri = saveMediaToStorage(img)

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_qr)))

    }

    private fun saveQrCode(qrCode: Bitmap) {
        if (!allStoragePermissionsGranted()) {
            requestStoragePermission(REQUEST_STORAGE_PERMISSION_SAVE)
            return
        }

        saveMediaToStorage(qrCode)
    }

    private fun saveMediaToStorage(bitmap: Bitmap): Uri {

        val date: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis())

        val filename = "QR_Code_$date"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {
                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.TITLE, "QR_Code")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}${File.separator}${getString(R.string.app_name)}")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                val fos = imageUri?.let { resolver.openOutputStream(it) }

                fos?.use {
                    //Finally writing the bitmap to the output stream that we opened
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                contentValues.clear()

                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                if (imageUri != null) {
                    requireContext().contentResolver.update(imageUri, contentValues, null, null)
                }

                Toast.makeText(context, getString(R.string.qr_code_saved_successfully), Toast.LENGTH_SHORT).show()
                return imageUri ?: Uri.EMPTY
            }

            return Uri.EMPTY
        } else {
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, filename, "QR Code created with Scan me"))
            Toast.makeText(context, getString(R.string.qr_code_saved_successfully), Toast.LENGTH_SHORT).show()
            return uri
        }

    }

}