package com.example.leafdiseaseclassificationkotlin

import android.Manifest
import android.content.ContentResolver
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var contentResolver: ContentResolver
    private lateinit var navController: NavController
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private var imageSize = 240

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        //view.findViewById<Button>(R.id.buttonPhoto).setOnClickListener(this)
        //view.findViewById<Button>(R.id.buttonCamera).setOnClickListener(this)
    }

    fun classifyImage(image: Bitmap) {
        try {
            //println(assetFilePath(this, "model_v9_v5.pt"))
            val module =
                LiteModuleLoader.load(this.assetFilePath(this.requireContext(), "model_v9.ptl"));
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

            // searching for the index with maximum score
            var maxScore = -Float.MAX_VALUE
            var maxScoreIdx = -1
            for (i in scores.indices) {
                if (scores[i] > maxScore) {
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
            val bundle = bundleOf("picture" to base64Image, "highest_prob" to maxScore.toString(), "classification" to className)
            navController.navigate(R.id.action_mainFragment_to_diseaseFragment, bundle)

            //vai enviar pra outra tela
            //result.text = className
//            var s = ""
//            for (i in classes.indices) {
//                s += String.format("%s: %.1f%%\n", classes[i], scores[i] * 100)
//            }
            //vai enviar pra outra tela
            //confidence.text = s

        } catch (e: IOException) {
            // TODO Handle the exception
            println("caiu aqui")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 3) {
                var image = data!!.extras!!["data"] as Bitmap?
                val dimension = Math.min(image!!.width, image.height)
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                //imageView.setImageBitmap(image)
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                classifyImage(image)
            } else {
                val dat = data!!.data
                var image: Bitmap? = null
                try {
                    image = getBitmap(this.contentResolver, dat)
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

    override fun onClick(v: View?) {


    }

}