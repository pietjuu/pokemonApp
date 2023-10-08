package com.example.pokemonmobileapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class DeviceInfoActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val deviceInfoRef = database.getReference("device_info")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)

        // Get the device information
        val deviceInfo = getDeviceInfo()

        // Save the device information to Firebase
        saveDeviceInfo(deviceInfo)
    }

    private fun getDeviceInfo(): DeviceInfo {
        val brand = Build.BRAND
        val model = Build.MODEL
        val androidVersion = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT

        return DeviceInfo(brand, model, androidVersion, sdkVersion)
    }

    private fun saveDeviceInfo(deviceInfo: DeviceInfo) {
        // Generate a unique key for the device info entry
        val key = deviceInfoRef.push().key

        // Save the device info to the database using the generated key
        key?.let {
            deviceInfoRef.child(it).setValue(deviceInfo)
                .addOnSuccessListener {
                    println("Device info saved successfully")
                }
                .addOnFailureListener {
                    println("Failed to save device info: ${it.message}")
                }
        }
    }

    data class DeviceInfo(
        val brand: String,
        val model: String,
        val androidVersion: String,
        val sdkVersion: Int
    )
}