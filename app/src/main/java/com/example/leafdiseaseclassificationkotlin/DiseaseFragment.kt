package com.example.leafdiseaseclassificationkotlin

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import java.io.*

class DiseaseFragment : Fragment() {
    private var currentDiseaseName = "Healthy"
    private var currentDiseaseInformation = "loren ipsun"
    var navController: NavController? = null
    private lateinit var confidence: TextView
    private lateinit var result: TextView
    private lateinit var imageView: ImageView
    private lateinit var information: TextView
    private lateinit var highestProb: String
    private lateinit var classification: String
    private lateinit var picture: String
    private var disease: DiseaseInformation = DiseaseInformation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //argumentos passados da ultima fragment!!!
        highestProb = arguments?.getString("highest_prob")!!
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        //decodando a string para virar bitmap e poder ser exibida
        val imageBytes = Base64.decode(picture, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val test = (highestProb.toFloat() * 100).toString() + "%"

        confidence = view.findViewById(R.id.confidence)
        result = view.findViewById(R.id.result)
        imageView = view.findViewById(R.id.imageView)
        information = view.findViewById(R.id.diseaseInformation)

        for (i in 0..9) {
            if (classification == disease.diseaseList[i]) {
                currentDiseaseName = disease.translatedDiseaseList[i]
                currentDiseaseInformation = disease.diseaseSymptomsList[i]
            }
        }

        confidence.text = test
        result.text = currentDiseaseName
        information.text = currentDiseaseInformation
        imageView.setImageBitmap(image)

        saveImage(test, currentDiseaseName, picture)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveImage(highestProb: String, classification: String, picture: String) {

        val gson = Gson()

        val saveInfo = Classification(highestProb, classification, picture)

        val json: String = gson.toJson(saveInfo)

        val dir = File(context?.filesDir, "classifications.json")
        //já fez classificação antes?
        if (!dir.exists()) {
            dir.writeText("[$json]")
        }
        else{
            val appendText = dir.readText().replace("]", "")
            dir.writeText("$appendText, $json]")
        }
    }

}
