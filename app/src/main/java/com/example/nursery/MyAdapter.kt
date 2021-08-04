package com.example.nursery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(
    private val plantList: ArrayList<Plant>,
    var context: Context?,
    val clickListener: (Plant, Int) -> Unit
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pName: TextView = view.findViewById(R.id.PlantName)
        val availability: TextView = view.findViewById(R.id.avail)
        val pPrice: TextView = view.findViewById(R.id.cost)
        val pImg: ImageView = view.findViewById(R.id.item_image_view)
        val cardContainer: CardView = view.findViewById(R.id.cardView)

    }

    //var mContext = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // mContext=parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val plant: Plant = plantList[position]
        //https://www.simplyfresh.co.in/wp-content/uploads/2019/11/3_Oregano.png
        Glide.with(context).load(plant.pImg).into(holder.pImg)
        holder.pName.text = plant.pName
        holder.pPrice.text = "₹" + plant.pPrice
        holder.availability.text = if (plant.availability == true) {
            "Yes"
        } else {
            "No"
        }
        // holder.itemView.
        holder?.cardContainer?.setOnClickListener { clickListener(plant, position) }
    }

    override fun getItemCount(): Int {
        return plantList.size
    }


}