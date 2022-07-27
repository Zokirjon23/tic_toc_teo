package com.example.tictocteo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tictocteo.R
import com.example.tictocteo.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

//    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch{
            delay(1500)
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToMenuFragment())
        }
    }
}