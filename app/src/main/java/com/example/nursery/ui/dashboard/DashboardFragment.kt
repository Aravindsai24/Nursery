package com.example.nursery.ui.dashboard

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nursery.*
import com.example.nursery.R
import com.example.nursery.databinding.FragmentDashboardBinding
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.*

class DashboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantArrayList: ArrayList<PlantItem>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    lateinit var navController: NavController
    private var _binding: FragmentDashboardBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        plantArrayList = arrayListOf()
        navController = findNavController()
        myAdapter = MyAdapter(plantArrayList, activity?.applicationContext,navController)

        recyclerView.adapter = myAdapter
        navController = findNavController()
        recyclerView.addOnItemTouchListener(recyclerItemClickListener(navController))
        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("plantscollection")
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
                            plantArrayList.add(PlantItem(dc.document.id,dc.document.toObject(Plant::class.java)))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class recyclerItemClickListener(val navController: NavController): RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val view = rv.findChildViewUnder(e.x,e.y)
            if(view == null) {
                return false
            }
            val itemView = rv.findContainingViewHolder(view!!) as MyAdapter.MyViewHolder
            val bundle: Bundle = Bundle()
            bundle.putString("pId",itemView.pId)
            navController.navigate(R.id.plantViewFragment,bundle)
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }

    }
}