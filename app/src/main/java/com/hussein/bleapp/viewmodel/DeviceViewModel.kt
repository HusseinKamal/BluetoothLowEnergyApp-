package com.hussein.bleapp.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hussein.bleapp.util.PermissionUtil


class DeviceViewModel constructor(application: Application) : AndroidViewModel(application){

    //Live data
    private var devicesLiveData: MutableLiveData<BluetoothDevice>
    private var connectDevice: MutableLiveData<Boolean>
    private var disConnectDevice: MutableLiveData<Boolean>
    val ctx:Context

    //Connect parameters
   // var m_myUUID: UUID = UUID.fromString("5034d44a-57a7-11ed-9b6a-0242ac120002")
   // var m_bluetoothSocket: BluetoothSocket? = null
   // var m_isConnected: Boolean = false

    private var bluetoothLeScanner: BluetoothLeScanner?=null
    private var bluetoothAdapter:BluetoothAdapter?=null
    private var scanning = false
    private val handler = Handler()
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000


    init {
        ctx=application.applicationContext
        devicesLiveData= MutableLiveData<BluetoothDevice>()
        connectDevice =MutableLiveData()
        disConnectDevice =MutableLiveData()


        //Bluetooth adapter configuration
        val manager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter=manager.adapter
        if(bluetoothAdapter!=null &&bluetoothAdapter!!.bluetoothLeScanner!=null) {
            bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        }
//        if(PermissionUtil.checkPermission(ctx,"android.permission.READ_PHONE_STATE"))
//        {
//            val tManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//            m_myUUID = UUID.fromString(tManager!!.deviceId)
//        }

    }

    private fun scanLeDevice() {
        PermissionUtil.checkBluetoothPermission(ctx)
        if(bluetoothLeScanner==null) {
            if(bluetoothAdapter!=null)
            {
                bluetoothLeScanner=bluetoothAdapter!!.bluetoothLeScanner
            }
        }
        bluetoothLeScanner!!.startScan(leScanCallback)
    }
    fun getAllDevices():MutableLiveData<BluetoothDevice> {
        scanLeDevice()
        return devicesLiveData
    }
/*
    fun connectToDevice(connectedDevice:BluetoothDevice,bluetoothAdapter: BluetoothAdapter):MutableLiveData<Boolean> {
        var connectSuccess: Boolean = true
        viewModelScope.executeAsyncTask(onPreExecute = {
            // ... runs in Main Thread
        }, doInBackground = {
            try {
                if(PermissionUtil.checkBluetoothPermission(ctx)) {
                    if (m_bluetoothSocket == null || !m_isConnected) {
//                        val device: BluetoothDevice =
//                            bluetoothAdapter.getRemoteDevice(
//                                connectedDevice.address
//                            )
                        m_bluetoothSocket =
                            connectedDevice.createInsecureRfcommSocketToServiceRecord(connectedDevice.uuids[0].uuid//uuid of
                            )
                        bluetoothAdapter.cancelDiscovery()
                        m_bluetoothSocket!!.connect()
                    }
                }
            } catch (e: Exception) {
                connectSuccess = false
                e.printStackTrace()
            }
            // ... runs in Worker(Background) Thread
            "Result" // send data to "onPostExecute"
        }, onPostExecute = {
            // runs in Main Thread
            // ... here "it" is the data returned from "doInBackground"
            if (connectSuccess) {
                m_isConnected = true
            }
            connectDevice.postValue(m_isConnected)
        })
        return connectDevice
    }
    private fun <R> CoroutineScope.executeAsyncTask(onPreExecute: () -> Unit, doInBackground: () -> R, onPostExecute: (R) -> Unit) = launch {
        onPreExecute() // runs in Main Thread
        val result = withContext(Dispatchers.IO) {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        onPostExecute(result) // runs in Main Thread
    }
    private fun disconnect():MutableLiveData<Boolean> {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
                disConnectDevice.postValue(true)
            } catch (e: Exception) {
                disConnectDevice.postValue(false)
                e.printStackTrace()
            }
        }
        return disConnectDevice
    }
 */

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            PermissionUtil.checkBluetoothPermission(ctx)
            if(result.device!=null) {
                if(!TextUtils.isEmpty(result.device.name)) {
                    devicesLiveData.postValue(result.device)
                }
            }

        }
    }
}