package com.example.leafdiseaseclassificationkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.File

class WelcomeFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        firstTime()
        //criando botao
        view.findViewById<Button>(R.id.avancar1).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.avancar1 -> {
                navController.navigate(R.id.action_welcomeFragment_to_nameFragment)
            }
        }
    }

    private fun firstTime() {
        val fileName = "userName.txt"
        val file = File(context?.filesDir, fileName)

        if (file.exists()) {
            navController.navigate(R.id.action_welcomeFragment_to_mainFragment)
        }
    }
}