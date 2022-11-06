package com.hussein.bleapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hussein.bleapp.viewmodel.FactoryViewModel
import java.lang.reflect.ParameterizedType


/**Base Activity with custom header and data binding*/
abstract class BaseActivity<B : ViewBinding,VM : ViewModel> : AppCompatActivity() {

    lateinit var viewModel: VM
    lateinit var binding: B
    //private var tvTitle: TextView?=null

//    override fun setContentView(v: View) {
//        val layout: LinearLayout = layoutInflater.inflate(R.layout.activity_base, null) as LinearLayout
//        val activityContainer: FrameLayout = layout.findViewById(R.id.layout_container)
//        tvTitle = layout.findViewById(R.id.tvTitle) as TextView
//        //activityContainer.addView(v,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
//        layoutInflater.inflate(getResourceLayout(), activityContainer, true)
//
//        super.setContentView(layout)
//    }
//    fun setScreenTitle(str: String) {
//        if(!TextUtils.isEmpty(str)&&tvTitle!=null)
//        {
//            tvTitle!!.text = str
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,FactoryViewModel(application))[getViewModelClass()]
        binding = getViewBinding()
        setContentView(binding.root)
    }


    private fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return type as Class<VM>
    }

    abstract fun getViewBinding(): B

    abstract fun getResourceLayout(): Int

}