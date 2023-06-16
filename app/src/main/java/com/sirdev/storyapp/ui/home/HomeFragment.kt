package com.sirdev.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sirdev.storyapp.MainActivity
import com.sirdev.storyapp.databinding.FragmentHomeBinding
import com.sirdev.storyapp.ui.story.AddStoryActivity


class HomeFragment : Fragment() {
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var homeStoryAdapter: HomeStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

        return fragmentHomeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeStoryAdapter = HomeStoryAdapter()
        initViewModel()
        (activity as MainActivity).supportActionBar?.apply {
            title = "INSTAGAM"
            show()
        }

        initView()
    }

    private fun initViewModel() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.apply {

            getAllStoriesData((activity as MainActivity).userLoginPref.getLoginData().token)
            listStoryData.observe(requireActivity()) {
                if (it != null) {
                    homeStoryAdapter.setStoryData(it)
                }
            }
        }
    }

    private fun initView() {
        fragmentHomeBinding?.apply {
            rvStory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = homeStoryAdapter
            }

            fabAddStory.setOnClickListener {

                val intent = Intent(requireActivity(), AddStoryActivity::class.java)
                startActivity(intent)
                FragmentManager.POP_BACK_STACK_INCLUSIVE
                requireActivity().finish()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentHomeBinding = null
    }
}