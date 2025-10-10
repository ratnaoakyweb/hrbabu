package com.hrbabu.tracking.activity

import android.app.Activity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.databinding.ActivityApplyLeaveBinding
import com.hrbabu.tracking.fragments.ApplyLeaveFragment
import com.hrbabu.tracking.helpers.ApplyLeaveHelper
import com.hrbabu.tracking.request_response.alldropdown.RcItem
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveResponse
import com.hrbabu.tracking.utils.CommonUtils

class ApplyLeaveActivity : AppCompatActivity() {

    lateinit var binding: ActivityApplyLeaveBinding
    lateinit var helper: ApplyLeaveHelper
    var startDate: String = ""
    var endDate: String = ""
    var totalDays: Int = 0
    var leaveId: Int = 0
    var reason: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplyLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = ApplyLeaveFragment()

        addFragment(fragment, binding.leaveContainer.id)

        helper = ApplyLeaveHelper(this)
        helper.init(this)
        helper.hitApi(ApplyLeaveHelper.GET_LEAVE_TYPE)

    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    fun addFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean = false) {
        try {
            CommonUtils.Companion.showLog(
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


    fun setUpData(res: List<RcItem?>?) {
        val fragment = supportFragmentManager.findFragmentById(binding.leaveContainer.id)
        if (fragment is ApplyLeaveFragment) {
            fragment.setUpData(res)
        }
    }

    fun parseLeaveResponse(response: SaveEmpLeaveResponse?){
        if(response?.rs == 1) {
            CommonUtils.Companion.showToast(this, "Leave applied successfully")

            //set result
            setResult(RESULT_OK)
            finish()
        }else{
            CommonUtils.Companion.showToast(this, response?.msgkey.toString())
        }
    }


}