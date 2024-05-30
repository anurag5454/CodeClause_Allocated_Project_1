package com.example.codescanner

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener

class MainActivity : AppCompatActivity() , TorchListener{

    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var cameraManager: CameraManager
    private var lastScanResult: BarcodeResult? = null
    private lateinit var barcodeView: DecoratedBarcodeView
    private var permissions: Array<String> = arrayOf(Manifest.permission.CAMERA)
    private val requestCode = 123

    private var isTorchOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if CAMERA permission is granted
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        } else {
            initializeScanner()
        }
    }
    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializeScanner() {
        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                // Handle the scan result here
                val scannedData = result.text
                handleScanResult(scannedData)

                // Play the default notification sound when a scan is completed
                          }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
    }


    // Handle torch (flashlight) on
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onTorchOn() {
        if (!isTorchOn) {
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, true)
                isTorchOn = true
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    // Handle torch (flashlight) off
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onTorchOff() {
        if (isTorchOn) {
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, false)
                isTorchOn = false
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    // Handle the scan result when it's available

    private fun handleScanResult(result: String) {
        // Here, you can process the scan result as needed.
        val scanText = result

        val i=findViewById<TextView>(R.id.textView3)
        i.text=scanText

        // You can also perform any other actions you want based on the scanned data.
    }


    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }


    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

}