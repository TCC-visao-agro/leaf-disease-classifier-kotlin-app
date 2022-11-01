package com.example.leafdiseaseclassificationkotlin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class DiseaseFragment : Fragment(){

    private lateinit var confidence: TextView
    private lateinit var result: TextView
    private lateinit var imageView: ImageView
    private lateinit var highest_prob: String
    private lateinit var classification: String
    private lateinit var picture: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //argumentos passados da ultima fragment!!!
        highest_prob = arguments?.getString("highest_prob")!!
        classification = arguments?.getString("classification")!!
        picture = arguments?.getString("picture")!!

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disease, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //decodando a string para virar bitmap e poder ser exibida
        val imageBytes = Base64.decode(picture, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        confidence = view.findViewById(R.id.confidence)
        result = view.findViewById(R.id.result)
        imageView = view.findViewById(R.id.imageView)

        confidence.text = highest_prob
        result.text = classification
        imageView.setImageBitmap(image)

    }


}