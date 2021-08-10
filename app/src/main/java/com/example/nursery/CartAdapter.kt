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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartAdapter(val cartItems: ArrayList<OrderItem>,
                  var context: Context,
                  var db: FirebaseFirestore): RecyclerView.Adapter<CartAdapter.cartViewHolder>() {
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
        var btn_buynow: Button = view.findViewById(R.id.btn_ci_return)
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
            holder.btn_dec.visibility = View.VISIBLE
            holder.btn_inc.visibility = View.VISIBLE
            holder.pQuantity.visibility = View.VISIBLE
            holder.btn_buynow.visibility = View.VISIBLE
        } else {
            holder.pAvailability.text = "Out Of Stock"
            holder.pAvailability.setTextColor(Color.RED)
            holder.btn_dec.visibility = View.GONE
            holder.btn_inc.visibility = View.GONE
            holder.pQuantity.visibility = View.GONE
            holder.btn_buynow.visibility = View.GONE
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
        holder.btn_buynow.setOnClickListener {
            var cur_date_time = getDateTime()
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val userRef = db.collection("users").document(userId)
            val myOrderRef = userRef.collection("myOrders")
            val data = hashMapOf(
                "dateAndTime" to cur_date_time,
                "pQuantity" to orderItem.pQuantity,
                "pId" to orderItem.pRef.id,
            )
            myOrderRef.add(data)
                .addOnSuccessListener {
                    Log.d("firebase buy now", "DocumentSnapshot successfully added!")
                    deleteItem(orderItem,position)
                    Toast.makeText(context,"Thank you for the purchase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("firebase buynow", "Error adding documents: ", e)
                }

        }

    }
    private fun getDateTime(): String? {
        val calender = Calendar.getInstance()
        var cur_time: Date = calender.time
        var dateandtime: String = SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.getDefault()).format(cur_time)
        return dateandtime
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