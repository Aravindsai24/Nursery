package com.example.nursery.ui.dashboard

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nursery.*
import com.example.nursery.R
import com.example.nursery.databinding.FragmentDashboardBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantArrayList: ArrayList<PlantItem>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    lateinit var navController: NavController
    private lateinit var plantSearch: SearchView
    private lateinit var currentLocation: TextView
    private lateinit var showLocation: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    var TAG = DashboardFragment::class.simpleName

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
        currentLocation = view.findViewById(R.id.currentLocation)
        showLocation = view.findViewById(R.id.showLocation)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        geocoder = Geocoder(activity, Locale.getDefault())
        plantSearch.maxWidth = Int.MAX_VALUE
        plantSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
//        recyclerView.layoutManager = object : GridLayoutManager(activity, 2){ override fun canScrollVertically(): Boolean { return false } }
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        plantArrayList = arrayListOf()
        navController = findNavController()
        myAdapter = MyAdapter(plantArrayList, activity?.applicationContext,navController)

        myAdapter = MyAdapter(
            plantArrayList,
            activity?.applicationContext
        ) { itemDto: Plant, position: Int ->
            Log.e("MyActivity", "Clicked on item  ${itemDto.pName} at position $position")
            Snackbar.make(
                view, "Clicked on item  ${itemDto.pName} at position $position",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null)
                .setAnchorView(R.id.nav_view)
                .show()
        }

        recyclerView.adapter = myAdapter
        navController = findNavController()
        recyclerView.addOnItemTouchListener(recyclerItemClickListener(navController))

//        recyclerView.onS

        EventChangeListener()

        getLastLocation()

        currentLocation.setOnClickListener {
            Log.d("Click","Location clicked")
            getLastLocation()
        }
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {

                    val location = taskLocation.result

//                    latitudeText.text = resources
//                        .getString(R.string.latitude_label, location?.latitude)
//                    longitudeText.text = resources
//                        .getString(R.string.longitude_label, location?.longitude)


                    val addresses: List<Address>


                    addresses = geocoder.getFromLocation(
                        location?.latitude!!,
                        location?.longitude,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    val city: String = addresses[0].getLocality()
                    val state: String = addresses[0].getAdminArea()
                    val country: String = addresses[0].getCountryName()
                    val postalCode: String = addresses[0].getPostalCode()
                    val knownName: String = addresses[0].getFeatureName()
                    showLocation.text = address
                } else {
                    Log.w("address", "getLastLocation:exception", taskLocation.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = getActivity()?.let {
            Snackbar.make(
                it.findViewById(android.R.id.content), getString(snackStrId),
                LENGTH_SHORT)
        }
        if (actionStrId != 0 && listener != null) {
            snackbar?.setAction(getString(actionStrId), listener)
        }
        snackbar?.setAnchorView(R.id.nav_view)
        snackbar?.show()

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