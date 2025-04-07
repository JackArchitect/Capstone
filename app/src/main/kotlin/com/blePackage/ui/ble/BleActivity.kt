package com.blePackage.ui.ble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blePackage.bluetooth.BluetoothPermissionsManager

class BleActivity: AppCompatActivity() {
    private lateinit var bluetoothPermissionsManager: BluetoothPermissionsManager: BluetoothPermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothPermissionsManager = BluetoothPermissionsManager(this) {
            startBleScanning()
        }

        binding.enableBluetoothButton.setOnClickListener {
            bluetoothPermissionsManager.requestBluetooth()
        }
    }

    private fun startBleScanning() {

    }
}