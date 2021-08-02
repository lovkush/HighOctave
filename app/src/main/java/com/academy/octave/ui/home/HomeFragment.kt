package com.academy.octave.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.academy.octave.HomeCardAdapter
import com.academy.octave.R
import com.academy.octave.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), HomeCardAdapter.OnItemClickListener   {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val  title = ArrayList<String>()
    private val  description = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val recyclerView = binding.mainRecycler

        //adding a layoutmanager
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        title.add("My Class ")
        title.add("MY CALENDER")
        title.add("MY NOTES")
        title.add("MY PROFILE")
        title.add("SHOPS")
        title.add("METRONOME")
        title.add("YOUTUBE")
        title.add("EVENTS")
        title.add("CHANGE TEACHER")
        title.add("FEEDBACK")

        description.add("Check Details all about your class")
        description.add("CHECK YOUR ATTENTION HERE")
        description.add("CHECK ALL YOUR NOTES AND HOMEWORK")
        description.add("CHECK YOUR PROFILE DETAILS")
        description.add("155 ITEMS")
        description.add("SYNC YOUR INSTRUMENT")
        description.add("155 ITEMS")
        description.add("CHECK LATEST EVENTS")
        description.add("REQUEST FOR CHANGE IN TEACHER")
        description.add("PROVIDE US YOUR FEEDBACK")


        val adapter = HomeCardAdapter(title,description,this)


        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, title[position],Toast.LENGTH_SHORT).show()
    }


}


