package com.example.leafdiseaseclassificationkotlin

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.File

class NameFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var inputName: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        inputName = view.findViewById(R.id.input_name)
        view.findViewById<Button>(R.id.avancarNome).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.avancarNome -> {
                if (!TextUtils.isEmpty(inputName.text.toString())) {
                    val bundle = bundleOf("name" to inputName.text.toString())
                    navController.navigate(R.id.action_nameFragment_to_confirmationName, bundle)
                }
            }
        }
    }

}