package com.hussein.bleapp.util

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult

class PermissionUtil {
    companion object{
        fun checkBluetoothPermission(mContext:Context):Boolean
        {
            // sets the text to the textview from our itemHolder class
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                        &&ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else {
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED&&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
//            } else {
//                val deniedPermissions: ArrayList<String> = ArrayList()
//                if(!checkPermission(mContext,Manifest.permission.BLUETOOTH_SCAN))
//                    deniedPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
//                if(!checkPermission(mContext,Manifest.permission.BLUETOOTH_CONNECT))
//                    deniedPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
//                if(deniedPermissions.isEmpty())
//                    if(MmcDeviceCapabilities.bluetoothEnabled()) //check if bluetooth is enabled
//                        return true;
//                    else {
//                        requestEnableBluetooth(); //method to request enable bluetooth
//                        return false;
//                    }
//                else {
//                    requestRuntimePermissions(
//                        "Bluetooth permissions request",
//                        "Bluetooth permissions request rationale",
//                        CONNECT_PERMISSIONS_CODE,
//                        deniedPermissions.toArray(new String[0]));
//                    return false;
//                }
//            }
        }

        fun requestBluetoothPermission(mContext:Context)
        {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(mContext as Activity,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 2)
                }
            }
            else
            {
                ActivityCompat.requestPermissions(mContext as Activity,
                    arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 2)

            }
        }

        fun checkPermission(context: Context,permission: String): Boolean {
            return (ActivityCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED)
        }
        fun requestPermission(mContext: Context,permission: String) {
            ActivityCompat.requestPermissions(mContext as Activity,
                arrayOf(permission), 2)
        }

//        private fun requestRuntimePermissions(context: Context,title: String,
//            description: String,
//            requestCode: Int,
//            vararg permissions: String
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permissions[0])) {
//                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//                builder.setTitle(title)
//                    .setMessage(description)
//                    .setCancelable(false)
//                    .setNegativeButton(R.string.no) { dialog, id -> }
//                    .setPositiveButton(R.string.ok) { dialog, id ->
//                        ActivityCompat.requestPermissions(
//                            context,
//                            permissions,
//                            requestCode
//                        )
//                    }
//                JColorChooser.showDialog(builder) //method to show a dialog
//            } else ActivityCompat.requestPermissions(context, permissions, requestCode)
//        }

    }
}