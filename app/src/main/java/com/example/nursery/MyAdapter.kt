package com.example.nursery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val plantList: ArrayList<PlantItem>, var context: Context?, val navController: NavController) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pName: TextView = view.findViewById(R.id.PlantName)
        val availability: TextView = view.findViewById(R.id.avail)
        val pPrice: TextView = view.findViewById(R.id.cost)
        val pImg: ImageView =view.findViewById(R.id.item_image_view)
        lateinit var pId: String
    }
  //var mContext = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       // mContext=parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val plantItem: PlantItem = plantList[position]
        val plant: Plant = plantItem.plant
        //https://www.simplyfresh.co.in/wp-content/uploads/2019/11/3_Oregano.png
       Glide.with(context).load(plant.pImg).into(holder.pImg)
        holder.pName.text = plant.pName
        holder.pPrice.text = "₹"+plant.pPrice
        holder.availability.text = if (plant.availability == true) {
            "Yes"
        } else {
            "No"
        }
        holder.pId = plantItem.pId
       // holder.itemView.
    }

    override fun getItemCount(): Int {
        return plantList.size
    }
}