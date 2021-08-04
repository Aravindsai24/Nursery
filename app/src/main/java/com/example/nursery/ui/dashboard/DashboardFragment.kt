package com.example.nursery.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nursery.MyAdapter
import com.example.nursery.Plant
import com.example.nursery.R
import com.example.nursery.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.*

class DashboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantArrayList: ArrayList<Plant>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore

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

        myAdapter = MyAdapter(plantArrayList, activity?.applicationContext)

        recyclerView.adapter = myAdapter

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
                            plantArrayList.add(dc.document.toObject(Plant::class.java))
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
}