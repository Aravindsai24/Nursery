package com.example.nursery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class OrderFragment : Fragment() {

    companion object {
        fun newInstance() = OrderFragment()
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var empty_tv: TextView
    private lateinit var myAdapter: OrderAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    var orderItems: ArrayList<Order> = ArrayList<Order>()

    private lateinit var viewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        recyclerView = view.findViewById(R.id.rv_order)
        empty_tv = view.findViewById(R.id.order_empty_tv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        myAdapter = activity?.applicationContext?.let { OrderAdapter(orderItems, it, db) }!!
        recyclerView.adapter = myAdapter

        dataChangeListener()
    }

    private fun dataChangeListener() {
        db.collection("users").document(userId).collection("myOrders")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val quantity = dc.document.data["pQuantity"] as Long
                            val pId = (dc.document.data["pId"]).toString()
                            val date = dc.document.data["dateAndTime"].toString()
                            val oRef = dc.document.reference
                            var plant: Plant = Plant()
                            val pRef = db.collection("plantscollection").document(pId)
                            orderItems.add(Order(date as String?,quantity,plant,oRef))
                            getOrder(pRef,orderItems.size-1)
                        }
                    }
                    if (orderItems.isEmpty())
                    {
                        empty_tv.setVisibility(View.VISIBLE)
                    }
                    else{
                        empty_tv.setVisibility(View.GONE)
                    }
                }
            })
    }
    private fun getOrder(pRef: DocumentReference, curIndex: Int) {
        pRef.get()
            .addOnSuccessListener { doc ->
                if(doc!=null) {
                    val plant = doc.toObject(Plant::class.java)!!
                    orderItems[curIndex].plant = plant
                    myAdapter.notifyItemChanged(curIndex)
                    Log.d("plant data firebase", "documents loading")
                }
            }
            .addOnFailureListener { e ->
                Log.w("plant data firebase", "Error getting documents: ", e)
            }
    }

}