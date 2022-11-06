package com.hussein.bleapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.hussein.bleapp.MainActivity
import com.hussein.bleapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // remove title and make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Data binding views
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Click listeners of buttons
        setClickListener()


    }
    private fun setClickListener(){
        //sender button
        binding.senderDeviceButton.setOnClickListener {
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        }
        //receiver button
        binding.receiverDeviceButton.setOnClickListener {
            finish()
            startActivity(Intent(this,ReceiverActivity::class.java))
        }
    }

}