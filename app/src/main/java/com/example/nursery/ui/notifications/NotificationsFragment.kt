package com.example.nursery.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nursery.*
import com.example.nursery.databinding.FragmentNotificationsBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: CartAdapter
    private lateinit var db: FirebaseFirestore
    var cartItems: ArrayList<OrderItem> = ArrayList<OrderItem>()

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.rv_cart)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        myAdapter = activity?.applicationContext?.let { CartAdapter(cartItems, it, db) }!!
        recyclerView.adapter = myAdapter
        dataChangeListener()
    }

    private fun dataChangeListener() {
        db.collection("cart")
            .addSnapshotListener { value, error ->
                if(error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                } else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val quantity = dc.document.data["pQuantity"] as Long
                            val pId = dc.document.data["pId"]
                            val oRef = dc.document.reference
                            val pRef = db.collection("plantscollection").document(pId as String)
                            var plant: Plant = Plant()
                            cartItems.add(OrderItem(quantity,plant,pRef,oRef))
                            getPlant(pRef,cartItems.size-1)
                            myAdapter.notifyItemInserted(cartItems.size-1)
                        }
                    }

                }
            }
    }

    private fun getPlant(pRef: DocumentReference, curIndex: Int) {
        pRef.get()
            .addOnSuccessListener { doc ->
                if(doc!=null) {
                    val plant = doc.toObject(Plant::class.java)!!
                    cartItems[curIndex].plant = plant
                    myAdapter.notifyItemChanged(curIndex)
                    Log.d("initial data firebase", "documents loading")
                }
            }
            .addOnFailureListener { e ->
                Log.w("initial data firebase", "Error getting documents: ", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}