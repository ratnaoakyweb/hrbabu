package com.hrbabu.tracking.activity

import android.os.Bundle
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.request_response.profile.Res

class ActivityProfile : BaseActivity() {

    lateinit var binding: com.hrbabu.tracking.databinding.ActivityUserProfileBinding

    lateinit var helper: com.hrbabu.tracking.helpers.ActivityProfileHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = com.hrbabu.tracking.databinding.ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = com.hrbabu.tracking.helpers.ActivityProfileHelper(this)
        helper.init(this)
        helper.hitApi(com.hrbabu.tracking.helpers.ActivityProfileHelper.GET_PROFILE)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setUpData(res: Res?) {

        binding.tvFullName.text = res?.fullName ?: "N/A"
        binding.tvEmail.text = res?.email ?: "N/A"
        binding.tvPhone.text = res?.phone ?: "N/A"
        binding.tvDob.text = res?.dateOfBirth ?: "N/A"
        binding.tvJoiningDate.text = res?.joiningDate ?: "N/A"
        binding.tvCompanyName.text = res?.companyName ?: "N/A"
        binding.tvDepartment.text = res?.departmentName ?: "N/A"
        binding.tvDesignation.text = res?.designationName ?: "N/A"
        binding.tvLocation.text = res?.locationName ?: "N/A"
        binding.tvManager.text = res?.managerName ?: "N/A"
        binding.tvShiftName.text = res?.shiftName ?: "N/A"
        binding.tvShiftCode.text = res?.shiftCode ?: "N/A"
        binding.tvBiometricId.text = res?.biometricId ?: "N/A"

    }


}