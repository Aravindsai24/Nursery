package com.example.nursery

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(val cartItems: ArrayList<OrderItem>, var context: Context, var db: FirebaseFirestore): RecyclerView.Adapter<CartAdapter.cartViewHolder>() {
    class cartViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var pName: TextView = view.findViewById(R.id.ci_plant_name)
        var pImage: ImageView = view.findViewById(R.id.ci_plant_image)
        var pAvailability: TextView = view.findViewById(R.id.ci_availability)
        var pQuantity: TextView = view.findViewById(R.id.ci_quantity)
        var btn_dec: Button = view.findViewById(R.id.ci_quantity_decrease)
        var btn_inc: Button = view.findViewById(R.id.ci_quantity_increase)
        var iCost: TextView = view.findViewById(R.id.ci_cost)
        var pPrice: TextView = view.findViewById(R.id.ci_price)
        var btn_delete: Button = view.findViewById(R.id.ci_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item,parent,false)
        return cartViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: cartViewHolder, position: Int) {
        val orderItem: OrderItem = cartItems[position]
        val plant = orderItem.plant
        holder.pQuantity.text = orderItem.pQuantity.toString()
        holder.pName.text = plant.pName
        Glide.with(context).load(plant.pImg).into(holder.pImage)
        if(plant.availability == true) {
            holder.pAvailability.text = "In Stock"
            holder.pAvailability.setTextColor(Color.GREEN)
        } else {
            holder.pAvailability.text = "Out Of Stock"
            holder.pAvailability.setTextColor(Color.RED)
        }
        holder.pPrice.text = "Rs." + plant.pPrice
        holder.iCost.text = "Rs." + ((plant.pPrice?.toLong() ?: 0) * orderItem.pQuantity).toString()
        holder.btn_delete.setOnClickListener {
            deleteItem(orderItem,position)
        }
        holder.btn_inc.setOnClickListener {
            orderItem.oRef.update("pQuantity",orderItem.pQuantity+1)
                .addOnSuccessListener {
                    Log.d("Firebase", "DocumentSnapshot successfully updated!")
                    orderItem.pQuantity = orderItem.pQuantity+1
                    holder.pQuantity.text = (orderItem.pQuantity).toString()
                    holder.iCost.text = "Rs." + ((plant.pPrice?.toLong() ?: 0) * orderItem.pQuantity).toString()
                }
            .addOnFailureListener { e ->
                Log.w("quantity update", "Error getting documents: ", e)
            }
        }
        holder.btn_dec.setOnClickListener {
            if(orderItem.pQuantity == (1).toLong()) {
                deleteItem(orderItem,position)
            }
            orderItem.oRef.update("pQuantity",orderItem.pQuantity-1)
                .addOnSuccessListener {
                    Log.d("Firebase", "DocumentSnapshot successfully updated!")
                    orderItem.pQuantity = orderItem.pQuantity-1
                    holder.pQuantity.text = (orderItem.pQuantity).toString()
                    holder.iCost.text = "Rs." + ((plant.pPrice?.toLong() ?: 0) * orderItem.pQuantity).toString()
                }
                .addOnFailureListener { e ->
                    Log.w("quantity update", "Error updating documents: ", e)
                }
        }
    }

    private fun deleteItem(orderItem: OrderItem,position: Int) {
        orderItem.oRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase", "DocumentSnapshot successfully deleted!")
                cartItems.remove(orderItem)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                Log.w("quantity update", "Error deleting documents: ", e)
            }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}