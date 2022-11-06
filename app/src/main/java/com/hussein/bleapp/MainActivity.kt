package com.hussein.bleapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.SimpleExpandableListAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.hussein.bleapp.adapter.DevicesAdapter
import com.hussein.bleapp.databinding.ActivityMainBinding
import com.hussein.bleapp.service.BluetoothLeService
import com.hussein.bleapp.service.BluetoothLeService.Companion.ACTION_DATA_AVAILABLE
import com.hussein.bleapp.service.BluetoothLeService.Companion.ACTION_GATT_CONNECTED
import com.hussein.bleapp.service.BluetoothLeService.Companion.ACTION_GATT_DISCONNECTED
import com.hussein.bleapp.service.BluetoothLeService.Companion.ACTION_GATT_DISCOVERED
import com.hussein.bleapp.service.BluetoothLeService.Companion.EXTRA_DATA
import com.hussein.bleapp.ui.BaseActivity
import com.hussein.bleapp.ui.OnDeviceListener
import com.hussein.bleapp.util.GPSHelper
import com.hussein.bleapp.util.PermissionUtil
import com.hussein.bleapp.viewmodel.DeviceViewModel


/**Sender Activity for send image with bluetooth*/
class MainActivity : BaseActivity<ActivityMainBinding,DeviceViewModel>(),OnDeviceListener,GPSHelper.OnLocationEnableListener {

    private var devicesList= ArrayList<BluetoothDevice>()
    private var selectedDevice:BluetoothDevice?=null
    private lateinit var mAdapterDevice: DevicesAdapter
    //Connect with other devices with GATT Server
    private var bluetoothService : BluetoothLeService? = null
    private var connected:Boolean=false

    private lateinit var mGattCharacteristics:MutableList<MutableList<BluetoothGattCharacteristic>>
    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, resources.getString(R.string.enable_bluetooth), Toast.LENGTH_SHORT).show()
        }
        else {
            getAllDevices()
        }
    }

    // Code to manage Service lifecycle.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            bluetoothService?.let { bluetooth ->
                // call functions on service to check connection and connect to devices
                if (!bluetooth.initialize()) {
                    Toast.makeText(this@MainActivity, "Unable to initialize Bluetooth",Toast.LENGTH_SHORT).show()
                    finish()
                }
                // perform device connection
                if(selectedDevice!=null) {
                    bluetooth.connect(selectedDevice!!.address)
                }

            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //PermissionUtil.checkBluetoothPermission(context)
            when (intent.action) {
                ACTION_GATT_CONNECTED -> {
                   setConnect(true)
                }
                ACTION_GATT_DISCONNECTED -> {
                    setConnect(false)
                }
                ACTION_GATT_DISCOVERED -> {
                    // Show all the supported services and characteristics on the user interface.
                   // displayGattServices(bluetoothService?.getSupportedGattServices() as MutableList<BluetoothGattService>?)
                }
                ACTION_DATA_AVAILABLE -> {
                    //connected = true
                    if(intent.hasExtra(EXTRA_DATA))
                    {
                        Toast.makeText(context, intent.extras!!.get(EXTRA_DATA).toString()+" has been written", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun setConnect(isConnect:Boolean)
    {
        if(isConnect)
        {
            PermissionUtil.checkBluetoothPermission(this)
            connected = true
            binding.tvSelectedDevice.visibility = View.VISIBLE
            binding.tvSelectedDevice.text =
                resources.getString(R.string.connected_with) + " " + selectedDevice!!.name
            binding.progressConnect.visibility=View.GONE
        }
        else
        {
            connected = false
            binding.tvSelectedDevice.visibility = View.GONE
            Toast.makeText(this, resources.getString(R.string.could_not_connect), Toast.LENGTH_SHORT).show()
            binding.progressConnect.visibility=View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set title for page
        binding.lyHeader.tvTitle.text=resources.getString(R.string.home_title)
        initView()
        openBluetoothSetting()
        startService()
        setButtonListener()
    }

    private fun initView()
    {
        try {
            showDevicesList()
        }
        catch (e:Exception)
        {
            setButtonListener()
            e.printStackTrace()
        }
    }
    private fun openBluetoothSetting()
    {
        //open bluetooth
        if(PermissionUtil.checkBluetoothPermission(this)) {
            val blueToothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(blueToothIntent)
        }
        else
        {
            PermissionUtil.requestBluetoothPermission(this);
        }
    }
    private fun getAllDevices()
    {
        //Check if this device contains bluetooth and location permissions
        if(!PermissionUtil.checkBluetoothPermission(this)) {
            //Open Bluetooth
            PermissionUtil.requestBluetoothPermission(this)
        }
        if(!GPSHelper.isLocationEnabled(this)) {
            //Open Location
            GPSHelper(this,this)
            return
        }
        binding.tvSelectedDevice.visibility=View.GONE
        binding.progress.visibility= View.VISIBLE

        viewModel.getAllDevices().observe(this) {
            binding.progress.visibility = View.GONE
            if (!devicesList.contains(it)) {
                binding.rvDevices.visibility = View.VISIBLE
                binding.tvNoDevices.visibility = View.GONE
                devicesList.add(it)
                mAdapterDevice.notifyDataSetChanged()
            }
        }

    }
    private fun startService()
    {
        //Start service for connecting devices
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        registerReceiver(gattUpdateReceiver,makeGattUpdateIntentFilter())

    }
    private fun showDevicesList()
    {
        //Show all bounded devices
        mAdapterDevice = DevicesAdapter(this, devicesList,this)
        binding.rvDevices.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvDevices.adapter = mAdapterDevice
    }
    private fun setButtonListener()
    {
        //Set listeners of all buttons in page
        binding.searchBtn.setOnClickListener {
            // List all the bonded devices(paired)
            openBluetoothSetting()
        }

        binding.connectBtn.setOnClickListener {
            //Connect to selected device
            connectToSelectedDevice()
        }
    }

    private fun connectToSelectedDevice()
    {
        //PermissionUtil.checkBluetoothPermission(this)
        if(selectedDevice!=null&&!TextUtils.isEmpty(selectedDevice!!.address))
        {
            binding.progressConnect.visibility=View.VISIBLE
            bluetoothService!!.connect(selectedDevice!!.address)
        }
        else
        {
            binding.tvSelectedDevice.visibility=View.GONE
            Toast.makeText(this,resources.getString(R.string.select_device),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!PermissionUtil.checkBluetoothPermission(this)){
            Toast.makeText(this,resources.getString(R.string.enable_bluetooth),Toast.LENGTH_SHORT).show()
        }
        else {
            openBluetoothSetting()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!PermissionUtil.checkBluetoothPermission(this)){
            Toast.makeText(this,resources.getString(R.string.enable_bluetooth),Toast.LENGTH_SHORT).show()
        }
        if(resultCode == RESULT_OK&&devicesList.isEmpty()) {
            openBluetoothSetting()
        }
    }

    override fun onSelectDevice(devicePosition: Int) {
        selectedDevice=devicesList[devicePosition]
        mAdapterDevice.selectedDevicePos=devicePosition
        mAdapterDevice.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    override fun onRestart() {
        super.onRestart()
        if (bluetoothService != null && selectedDevice!=null && connected) {
            connectToSelectedDevice()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        bluetoothService = null
        unregisterReceiver(gattUpdateReceiver)
    }
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(ACTION_GATT_CONNECTED)
            addAction(ACTION_GATT_DISCONNECTED)
            addAction(ACTION_GATT_DISCOVERED)
            addAction(ACTION_DATA_AVAILABLE)
        }
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getResourceLayout(): Int {
        return R.layout.activity_main
    }

    override fun onLocationEnabled(isEnable: Boolean) {
        openBluetoothSetting()
    }


//    private fun displayGattServices(gattServices: MutableList<BluetoothGattService>?) {
//        if (gattServices == null) return
//        var uuid: String?
//        val unknownServiceString ="Unknow Service"
//        val unknownCharaString = "Unknown Characteristic"
//        val gattServiceData: MutableList<HashMap<String, String>> = mutableListOf()
//        val gattCharacteristicData: MutableList<ArrayList<HashMap<String, String>>> =
//            mutableListOf()
//        mGattCharacteristics = mutableListOf()
//
//        // Loops through available GATT Services.
//        gattServices.forEach { gattService ->
//            val currentServiceData = HashMap<String, String>()
//            uuid = gattService.uuid.toString()
//            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid!!, unknownServiceString)
//            currentServiceData[LIST_UUID] = uuid!!
//            gattServiceData += currentServiceData
//
//            val gattCharacteristicGroupData: ArrayList<HashMap<String, String>> = arrayListOf()
//            val gattCharacteristics = gattService.characteristics
//            val charas: MutableList<BluetoothGattCharacteristic> = mutableListOf()
//
//            // Loops through available Characteristics.
//            gattCharacteristics.forEach { gattCharacteristic ->
//                charas += gattCharacteristic
//                val currentCharaData: HashMap<String, String> = hashMapOf()
//                uuid = gattCharacteristic.uuid.toString()
//                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid!!, unknownCharaString)
//                currentCharaData[LIST_UUID] = uuid!!
//                gattCharacteristicGroupData += currentCharaData
//            }
//            mGattCharacteristics += charas
//            gattCharacteristicData += gattCharacteristicGroupData
//        }
//
//        val gattServiceAdapter = SimpleExpandableListAdapter(
//            this,
//            gattServiceData,
//            android.R.layout.simple_expandable_list_item_2,
//            arrayOf(LIST_NAME, LIST_UUID),
//            intArrayOf(android.R.id.text1, android.R.id.text2),
//            gattCharacteristicData,
//            android.R.layout.simple_expandable_list_item_2,
//            arrayOf(LIST_NAME, LIST_UUID),
//            intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        binding.mGattServicesList.setAdapter(gattServiceAdapter)
//    }


}