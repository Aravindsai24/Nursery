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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PlantViewFragment : Fragment() {

    companion object {
        fun newInstance() = PlantViewFragment()
    }
    lateinit var navController: NavController
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: PlantViewViewModel
    private lateinit var plant: Plant
    private lateinit var pId: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plant_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        pId = arguments?.getString("pId").toString()
        navController = findNavController()
        val pName = view.findViewById<TextView>(R.id.pv_name)
        val pAvailability = view.findViewById<TextView>(R.id.pv_availability)
        val pPrice_tv = view.findViewById<TextView>(R.id.pv_price)
        val pImage = view.findViewById<ImageView>(R.id.pv_image)
        val btn_add_to_cart = view.findViewById<Button>(R.id.btn_add_to_cart)
        val pDesc = view.findViewById<TextView>(R.id.pv_desc)
        val userRef = db.collection("users").document(userId)
        val cartRef = userRef.collection("cart")
        val plantRef = cartRef.document(pId)
        val snapshot: Task<DocumentSnapshot> = plantRef.get()
        snapshot.addOnSuccessListener {
            if(!it.exists()) {
                btn_add_to_cart.text = "ADD TO CART"
                btn_add_to_cart.setOnClickListener {
                    val data = hashMapOf(
                        "pId" to pId,
                        "pQuantity" to 1
                    )
                    plantRef.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(context,"added to cart",Toast.LENGTH_SHORT).show()
                            addGoToCart(btn_add_to_cart)
                            Log.d("firebase add to cart", "DocumentSnapshot successfully added!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("firebase add to cart", "Error adding documents: ", e)
                        }
                }
            } else {
                addGoToCart(btn_add_to_cart)
            }
        }
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
                            pPrice_tv.text = "Rs." + plant.pPrice
                            pDesc.text = plant.pDesc
                        }
                        Log.d("plant view firebase", "documents loaded succesfully")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("plant view firebase", "Error getting documents: ", e)
                }
        }

    }

    private fun addGoToCart(btnAddToCart: Button) {
        btnAddToCart.text = "GO TO CART"
        btnAddToCart.setOnClickListener{
            navController.navigate(R.id.nav_cart)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlantViewViewModel::class.java)
    }

}