package com.blePackage.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.blePackage.utils.showToast

class BluetoothPermissionsManager (
    private val activity: FragmentActivity,
    private val onBluetoothReady: () -> Unit

) {
    private val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) // For Android 12.0 or greater
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        ) // For Android 7.0 to 11.0
    }

    private val requestBluetoothPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions -> handlePermissionResults(permissions)
    }

    private val requestEnableBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> handleBluetoothEnableResult(result.resultCode)
    }

    fun requestBluetooth() {
        when {
            !packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ->
                {showToast("This device does not support BLE)"}
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (hasAllPermissions(*bluetoothPermissions)) {
                    onBluetoothReady()
                } else {
                    requestBluetoothPermissions.launch(bluetoothPermissions)
                }
            }
            else -> {
                if (bluetoothAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    requestEnableBluetooth.launch(enableBtIntent)
                } else if (!hasAllPermissions(*bluetoothPermissions)) {
                    requestBluetoothPermissions.launch(bluetoothPermissions)
                } else {
                    onBluetoothReady()
                }
            }
        }
    }

    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        val allGranted = permissions.all {it.value}

        if (allGranted) {
            onBluetoothReady()
        } else {
            val deniedList = permissions.filter {!it.value}.keys
            showToast("Following access is denied: ${deniedList.joinToString()}")
            // Expansion here
        }
    }

    private fun handleBluetoothEnableResult(resultCode: Int) {
        when (resultCode) {
            RESULT_OK -> {
                if (hasAllPermissions(*bluetoothPermissions)) {
                    onBluetoothReady()
                } else {
                    requestBluetoothPermissions.launch(bluetoothPermissions)
                }
            }
            else -> showToast("Bluetooth denied")
        }
    }

    private fun hasAllPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun onBluetoothReady() {
        showToast("Bluetooth is ready")
    }

}