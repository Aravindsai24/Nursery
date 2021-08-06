package com.example.nursery

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PlantViewFragment : Fragment() {

    companion object {
        fun newInstance() = PlantViewFragment()
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: PlantViewViewModel
    private var pPrice: Long = 0
    private var pQuantity:Long = 1;
    private lateinit var plant: Plant
    private lateinit var pId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plant_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        pId = arguments?.getString("pId").toString()
        val pName = view.findViewById<TextView>(R.id.pv_name)
        val pAvailability = view.findViewById<TextView>(R.id.pv_availability)
        val pPrice_tv = view.findViewById<TextView>(R.id.pv_price)
        val pImage = view.findViewById<ImageView>(R.id.pv_image)
        val btn_qn_dec = view.findViewById<Button>(R.id.pv_quantity_decrease)
        val btn_qn_inc = view.findViewById<Button>(R.id.pv_quantity_increase)
        val pQuantity_tv = view.findViewById<TextView>(R.id.pv_quantity)
        val total_cost = view.findViewById<TextView>(R.id.pv_total_cost)
        val btn_add_to_cart = view.findViewById<Button>(R.id.btn_add_to_cart)
        val pDesc = view.findViewById<TextView>(R.id.pv_desc)
        if (pId != null) {
            db.collection("plantscollection").document(pId)
                .get()
                .addOnSuccessListener { doc ->
                    if(doc!=null) {
                        plant = doc.toObject(Plant::class.java)!!
                        if(plant != null) {
                            pName.text = plant.pName
                            if(plant.availability == true) {
                                pAvailability.text = "In Stock"
                                pAvailability.setTextColor(Color.GREEN)
                            } else {
                                pAvailability.text = "Out Of Stock"
                                pAvailability.setTextColor(Color.RED)
                            }
                            Glide.with(activity?.applicationContext).load(plant.pImg).into(pImage)
                            pPrice = plant.pPrice?.toLong() ?: 0
                            pPrice_tv.text = "Rs." + pPrice.toString()
                            pQuantity_tv.text = pQuantity.toString()
                            total_cost.text = "Total: Rs." + (pQuantity * pPrice).toString()
                            pDesc.text = plant.pDesc
                        }
                        Log.d("plant view firebase", "documents loaded succesfully")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("plant view firebase", "Error getting documents: ", e)
                }
        }
        btn_qn_dec.setOnClickListener {
            if(pQuantity>1) {
                pQuantity = pQuantity - 1
                pQuantity_tv.text= pQuantity.toString()
                total_cost.text = "Total: Rs." + (pQuantity * pPrice).toString()
            }
        }
        btn_qn_inc.setOnClickListener {
            pQuantity = pQuantity + 1
            pQuantity_tv.text= pQuantity.toString()
            total_cost.text = "Total: Rs." + (pQuantity * pPrice).toString()
        }
        btn_add_to_cart.setOnClickListener {
            val data = hashMapOf(
                "pId" to pId,
                "pQuantity" to pQuantity
            )
            db.collection("cart")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(context,"added to cart",Toast.LENGTH_SHORT).show()
                    Log.d("firebase add to cart", "DocumentSnapshot successfully added!")
                }
                .addOnFailureListener { e ->
                    Log.w("firebase add to cart", "Error adding documents: ", e)
                }
        }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlantViewViewModel::class.java)
    }

}