package com.hrbabu.tracking.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.databinding.ActivityVisitListBinding
import com.hrbabu.tracking.fragments.FragmentVisitUpcoming
import com.hrbabu.tracking.helpers.ActivityVisitListHelper

class ActivityVisitList : BaseActivity() {

    private lateinit var binding: ActivityVisitListBinding
    //ActivityVisitListHelper
    private lateinit var helper: ActivityVisitListHelper
    private val tabTitles = arrayOf("Visits")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = ActivityVisitListHelper(this)
        helper.init(this)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 1
            override fun createFragment(position: Int): Fragment {
                return FragmentVisitUpcoming()

            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


        binding.btnAddVisit.setOnClickListener {

            addVisitLauncher.launch(Intent(this, AddVisitActivity::class.java))
        }

        helper.hitApi(ActivityVisitListHelper.GET_VISIT_LIST)
    }

    fun setUpVisits(visitList: List<com.hrbabu.tracking.request_response.empvisit.VisitsItem?>?) {
        val fragment = supportFragmentManager.findFragmentByTag("f0") as? FragmentVisitUpcoming
        fragment?.setupVisit(visitList)
    }

    private val addVisitLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
           helper.hitApi(ActivityVisitListHelper.GET_VISIT_LIST)
        }
    }

}