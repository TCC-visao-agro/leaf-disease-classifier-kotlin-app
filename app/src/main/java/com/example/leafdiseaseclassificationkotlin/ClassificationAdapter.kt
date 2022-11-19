package com.example.leafdiseaseclassificationkotlin

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ClassificationAdapter(private val classificationList: ArrayList<Classification>):
    RecyclerView.Adapter<ClassificationAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = classificationList[position]
        //turning base64 to bitmap from current item
        val imageBytes = Base64.decode(currentItem.picture, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.titleImage.setImageBitmap(image)
        holder.tvHeading.text = currentItem.classification
        holder.description.text = currentItem.highestProb
    }

    override fun getItemCount(): Int {
        return classificationList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val titleImage : ShapeableImageView = itemView.findViewById(R.id.title_image)
        val tvHeading : TextView = itemView.findViewById(R.id.tvHeading)
        val description : TextView = itemView.findViewById(R.id.description)
    }

}