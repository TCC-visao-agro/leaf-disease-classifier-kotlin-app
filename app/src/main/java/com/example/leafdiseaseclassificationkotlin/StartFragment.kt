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

class StartFragment : Fragment(), View.OnClickListener {

    private lateinit var name: String
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        name = arguments?.getString("name")!!
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.avancarComeco).setOnClickListener(this)

        saveUser(name)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.avancarComeco -> {
                navController.navigate(R.id.action_startFragment_to_mainFragment)
            }
        }
    }

    private fun saveUser(name: String) {
        val fileName = "userName.txt"
        val file = File(context?.filesDir, fileName)

        if (!file.exists()) {
            file.writeText(name)
        }
    }
}