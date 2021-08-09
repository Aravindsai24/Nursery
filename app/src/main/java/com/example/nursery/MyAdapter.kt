package com.example.nursery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MyAdapter(
    private var plantList: ArrayList<Plant>,
    var context: Context?,
    val navController: NavController,
    val clickListener: (Plant, Int) -> Unit
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>(), Filterable {

    var filterPlantList: ArrayList<Plant>

    init {
        filterPlantList = plantList
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pName: TextView = view.findViewById(R.id.PlantName)
        val availability: TextView = view.findViewById(R.id.avail)
        val pPrice: TextView = view.findViewById(R.id.cost)
        lateinit var pId: String
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
        val plant: Plant = filterPlantList[position]
        //https://www.simplyfresh.co.in/wp-content/uploads/2019/11/3_Oregano.png
        Glide.with(context).load(plant.pImg).into(holder.pImg)
        holder.pName.text = plant.pName
        holder.pPrice.text = "₹" + plant.pPrice
        holder.availability.text = if (plant.availability == true) {
            "Available"
        } else {
            "Out of Stock"
        }
        holder.availability.setTextColor(
            if (plant.availability == true) {
                ContextCompat.getColor(context!!, R.color.green)
            } else {
                ContextCompat.getColor(context!!, R.color.red)
            }
        )
        // holder.itemView.
        holder?.cardContainer?.setOnClickListener { clickListener(plant, position) }
        holder.pId = plant.pId.toString()
        // holder.itemView.
    }

    override fun getItemCount(): Int {
        return filterPlantList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().lowercase()
                if (charSearch.isEmpty()) {
                    filterPlantList = plantList
                } else {
                    val resultList = ArrayList<Plant>()
                    for (row in plantList) {
                        if (row.pName?.lowercase()
                                ?.contains(charSearch)!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterPlantList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterPlantList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterPlantList = results?.values as ArrayList<Plant>
                notifyDataSetChanged()
            }

        }

    }
}