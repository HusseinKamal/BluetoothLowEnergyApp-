package com.hussein.bleapp.util

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.widget.Toast
import com.hussein.bleapp.R

class Constant {
    companion object{
        fun isEnableBluetooth(bluetoothAdapter: BluetoothAdapter):Boolean
        {
            return bluetoothAdapter.isEnabled
        }
        fun isHasBluetooth(context: Context):Boolean
        {
            return if(BluetoothAdapter.getDefaultAdapter()==null) {
                Toast.makeText(context,context.resources.getString(R.string.no_bluetooth),Toast.LENGTH_SHORT).show()
                false
            } else {
                true
            }
        }
    }
}