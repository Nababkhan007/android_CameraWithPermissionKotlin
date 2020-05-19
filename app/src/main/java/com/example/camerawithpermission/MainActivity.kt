package com.example.camerawithpermission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cameraBtn: Button
    private lateinit var galleryBtn: Button
    private var REQUEST_CODE_FOR_CAMERA = 1
    private var REQUEST_CODE_FOR_GALLERY = 2
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialization()

        onClick()
    }

    private fun onClick() {
        cameraBtn.setOnClickListener {
            cameraPermission()
        }

        galleryBtn.setOnClickListener {
            galleryPermission()
        }
    }

    private fun galleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_CODE_FOR_GALLERY
                )
            } else {
                galleryIntent()
            }
        } else {
            galleryIntent()
        }
    }

    private fun galleryIntent() {
        val callGalleryIntent = Intent()
        callGalleryIntent.type = "image/*"
        callGalleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(callGalleryIntent, "Select image"),
            REQUEST_CODE_FOR_GALLERY
        )
    }

    private fun cameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA
                )
                requestPermissions(permission, REQUEST_CODE_FOR_CAMERA)
            } else {
                cameraIntent()
            }
        } else {
            cameraIntent()
        }
    }

    private fun cameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_CODE_FOR_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_FOR_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent()

            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == REQUEST_CODE_FOR_GALLERY) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent()

            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FOR_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                bitmap = data!!.extras!!.get("data") as Bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 500, 500, true)
                imageView.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_CODE_FOR_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 500, 500, true)
                imageView.setImageURI(imageUri)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun initialization() {
        imageView = findViewById(R.id.imageViewId)
        cameraBtn = findViewById(R.id.cameraBtnId)
        galleryBtn = findViewById(R.id.galleryBtnId)
    }
}
