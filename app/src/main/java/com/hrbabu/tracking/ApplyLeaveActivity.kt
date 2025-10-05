package com.hrbabu.tracking

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.databinding.ActivityApplyLeaveBinding
import com.hrbabu.tracking.fragments.ApplyLeaveFragment
import com.hrbabu.tracking.utils.CommonUtils


class ApplyLeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplyLeaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplyLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = ApplyLeaveFragment()

        addFragment(fragment, binding.leaveContainer.id)

    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    fun addFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean = true) {
        try {
            CommonUtils.showLog(
                "FRAGMENT_NAME",
                fragment.javaClass.simpleName + " Count->" + getSupportFragmentManager().getBackStackEntryCount()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fragmentManager = supportFragmentManager
        val fragmentTranscation = fragmentManager.beginTransaction()
        fragmentTranscation.replace(containerId, fragment)
        if (addToBackStack) {
            fragmentTranscation.addToBackStack("fragment")
        }
        fragmentTranscation.commit()


    }


}