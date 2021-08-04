package com.example.nursery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val plantList: ArrayList<Plant>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pName: TextView = view.findViewById(R.id.PlantName)
        val availability: TextView = view.findViewById(R.id.avail)
        val pPrice: TextView = view.findViewById(R.id.cost)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val plant: Plant = plantList[position]
        holder.pName.text = plant.pName
        holder.pPrice.text = plant.pPrice
        holder.availability.text = if (plant.availability == true) {
            "Yes"
        } else {
            "No"
        }
    }

    override fun getItemCount(): Int {
        return plantList.size
    }
}