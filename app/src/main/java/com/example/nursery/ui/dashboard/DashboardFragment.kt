package com.example.nursery.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nursery.MyAdapter
import com.example.nursery.Plant
import com.example.nursery.R
import com.example.nursery.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import java.util.logging.Filter

class DashboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantArrayList: ArrayList<Plant>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var plantSearch: SearchView

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
        plantSearch = view.findViewById(R.id.etSearchPlant)
        plantSearch.maxWidth = Int.MAX_VALUE
        plantSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(filterString: String?): Boolean {
                myAdapter.filter.filter(filterString)
                myAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(filterString: String?): Boolean {
                myAdapter.filter.filter(filterString)
                myAdapter.notifyDataSetChanged()
                return true
            }

        })
        recyclerView.layoutManager = GridLayoutManager(activity,2)

        plantArrayList = arrayListOf()

        myAdapter = MyAdapter(plantArrayList, activity?.applicationContext){ itemDto: Plant, position: Int ->
            Log.e("MyActivity", "Clicked on item  ${itemDto.pName} at position $position")
            Snackbar.make(view, "Clicked on item  ${itemDto.pName} at position $position",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.nav_view)
                .show()
        }

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