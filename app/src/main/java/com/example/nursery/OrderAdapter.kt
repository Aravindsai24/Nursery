package com.example.nursery

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class OrderAdapter(
    private var orderList: ArrayList<Order>,
    var context: Context?,
    var db: FirebaseFirestore,
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pName: TextView = view.findViewById(R.id.ci_plant_name)
        val pQuantity: TextView = view.findViewById(R.id.ci_availability)
        val pPrice: TextView = view.findViewById(R.id.ci_price)
        val pCost: TextView = view.findViewById(R.id.ci_cost)
        lateinit var pId: String
        val pImg: ImageView = view.findViewById(R.id.ci_plant_image)
        val returnBtn: Button = view.findViewById(R.id.btn_ci_return)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val order: Order = orderList[position]
        Glide.with(context).load(order.plant.pImg).into(holder.pImg)
        holder.pName.text = order.plant.pName
        holder.pPrice.text = "₹" + order.plant.pPrice
        holder.pQuantity.text = "Quantity : " + order.pQuantity.toString()
        holder.pCost.text = "₹" + (order.pQuantity * (order.plant.pPrice)!!.toLong()).toString()
        holder.returnBtn.setOnClickListener {
            deleteItem(order,position)
        }
    }

    private fun deleteItem(order: Order, position: Int) {
        order.oRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase", "DocumentSnapshot successfully deleted!")
                orderList.remove(order)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                Log.w("quantity update", "Error deleting documents: ", e)
            }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}