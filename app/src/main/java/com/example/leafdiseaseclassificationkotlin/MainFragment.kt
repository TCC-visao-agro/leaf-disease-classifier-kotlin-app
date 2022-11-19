package com.example.leafdiseaseclassificationkotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.exp
import kotlin.math.min

class MainFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var helloName: TextView
    private var imageSize = 240
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassificationAdapter
    private lateinit var classificationHistoryList: ArrayList<Classification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        helloName = view.findViewById(R.id.textViewHello)
        dataInitialize()
        recyclerView = view.findViewById(R.id.recyclerView)
        if (this::classificationHistoryList.isInitialized) {
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            adapter = ClassificationAdapter(classificationHistoryList)
            recyclerView.adapter = adapter
        }
        navController = Navigation.findNavController(view)
        camera = view.findViewById(R.id.buttonCamera)
        gallery = view.findViewById(R.id.buttonPhoto)


        camera.setOnClickListener {
            if (checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.CAMERA
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
        gallery.setOnClickListener {
            val cameraIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val module =
                LiteModuleLoader.load(this.assetFilePath(this.requireContext(), "modelv11.ptl"))
            // Creates inputs for reference.

            // preparing input tensor
            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                image,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST
            )
            // running the model
            val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()

            // getting tensor content as java array of floats
            val scores = outputTensor.dataAsFloatArray
            val softmaxValues = toSoftmax(scores)

            // searching for the index with maximum score]
            var maxScoreSoftmax: Float? = null
            var maxScore = -Float.MAX_VALUE
            var maxScoreIdx = -1
            for (i in scores.indices) {
                if (scores[i] > maxScore) {
                    maxScoreSoftmax = softmaxValues[i]
                    maxScore = scores[i]
                    maxScoreIdx = i
                }
            }

            val classes = arrayOf(
                "Tomato_Bacterial_spot",
                "Tomato_Early_blight",
                "Tomato_Late_blight",
                "Tomato_Leaf_Mold",
                "Tomato_Septoria_leaf_spot",
                "Tomato_Spider_mites_Two_spotted_spider_mite",
                "TomatoTarget_Spot",
                "TomatoTomato_YellowLeafCurl_Virus",
                "TomatoTomato_mosaic_virus",
                "Tomato_healthy"
            )

            val className: String = classes[maxScoreIdx]

            //transformando o bitmap em string para poder ser passado como argumento
            val base64Image = encodeImage(image)

            //importante nunca esquecer que a tipagem aqui no bundle pode QUEBRAR O APP INTEIRO!!!
            val bundle = bundleOf(
                "picture" to base64Image,
                "highest_prob" to maxScoreSoftmax.toString(),
                "classification" to className
            )
            navController.navigate(R.id.action_mainFragment_to_diseaseFragment, bundle)

        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun toSoftmax(scores: FloatArray?): FloatArray {
        var expValues = arrayOf<Float>()

        for (value in scores!!) expValues += exp(value)

        val sumExpValues = expValues.sum()
        var softmaxValues = arrayOf<Float>()

        for (value in expValues) softmaxValues += value / sumExpValues

        return softmaxValues.toFloatArray()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 3) {
                var image = data!!.extras!!["data"] as Bitmap?
                val dimension = min(image!!.width, image.height)
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                //imageView.setImageBitmap(image)
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                classifyImage(image)
            } else {
                val dat = data?.data
                var image: Bitmap? = null
                try {
                    image = getBitmap(activity?.contentResolver, dat)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //imageView.setImageBitmap(image)
                image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, false)
                classifyImage(image)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun dataInitialize() {
        if (File(context?.filesDir, "classifications.json").exists()) {
            val jsonFileString = File(context?.filesDir, "classifications.json").readText()

            val gson = Gson()
            val listClassificationType = object : TypeToken<List<Classification>>() {}.type
            val classifications: ArrayList<Classification> =
                gson.fromJson(jsonFileString, listClassificationType)
            classificationHistoryList = classifications
        }

        val fileName = "userName.txt"
        val file = File(context?.filesDir, fileName)
        val name = file.readText()

        helloName.text = "Olá, $name!"
    }


    @Throws(IOException::class)
    fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }

}