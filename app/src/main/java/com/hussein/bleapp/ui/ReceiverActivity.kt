package com.hussein.bleapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hussein.bleapp.databinding.ActivityReceiverBinding

/**Receiver Activity for receive image with bluetooth*/
class ReceiverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Data binding views
        binding = ActivityReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}