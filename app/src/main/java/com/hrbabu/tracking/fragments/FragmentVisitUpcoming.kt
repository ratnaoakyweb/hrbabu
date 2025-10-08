package com.hrbabu.tracking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrbabu.tracking.adapter.VisitAdapter
import com.hrbabu.tracking.databinding.FragmentVisitUpcomingBinding
import com.hrbabu.tracking.request_response.empvisit.VisitsItem


class FragmentVisitUpcoming : Fragment() {

    private lateinit var binding: FragmentVisitUpcomingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisitUpcomingBinding.inflate(inflater, container, false)



//        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewUpcoming.adapter = VisitAdapter(dummyList)
        return binding.root
    }

    fun setupVisit(visitList : List<VisitsItem?>?){
//        val dummyList = listOf(
//            Schedule("06 December 2023", "Alexandro", "-", "Pending"),
//            Schedule("06 December 2023", "Alexandro", "Joshua", "Approved"),
//            Schedule("06 December 2023", "Alexandro", "Joshua", "Approved")
//        )

        //set visit list to recycler view
        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUpcoming.adapter = VisitAdapter(visitList)

    }

}