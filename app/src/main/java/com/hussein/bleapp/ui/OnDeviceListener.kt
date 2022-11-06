package com.hussein.bleapp.ui

import android.bluetooth.BluetoothDevice

interface OnDeviceListener {
    fun onSelectDevice(devicePosition: Int)
}