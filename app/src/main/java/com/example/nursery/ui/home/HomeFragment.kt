package com.example.nursery.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.nursery.GoogleSignInActivity
import com.example.nursery.Plant
import com.example.nursery.R
import com.example.nursery.SignUp
import com.example.nursery.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var btnLogout: Button
    private lateinit var btnOrder: Button
    lateinit var navController: NavController
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid.toString()
        db = FirebaseFirestore.getInstance()
        val root: View = binding.root
        btnLogout = binding.logoutbtn
        btnOrder = binding.orderbtn
        btnLogout.setOnClickListener {
            auth.signOut()
            var intent=Intent(activity, GoogleSignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        btnOrder.setOnClickListener {
            navController.navigate(R.id.orderFragment,)
        }
        val fullname: TextView = binding.tvLName
        val email: TextView = binding.tvEmailProfile
        navController = findNavController()
        //val userId:TextView=binding.tvFName
        val address: EditText =binding.etAddress
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val Address = doc.data?.get("Address")
                    if(Address != null) {
                        address.text = Editable.Factory.getInstance().newEditable(Address.toString())
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("firebase address update", "Error updating documents: ", e)
            }
        address.isEnabled=false
        val btnEdit:Button=binding.editbtn
        btnEdit.setOnClickListener {
            if((btnEdit.text.toString())=="Save"){
                userRef.update("Address",address.text.toString())
                    .addOnSuccessListener {
                        Log.d("firebase address update", "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("firebase address update", "Error updating documents: ", e)
                    }
                address.isEnabled=false
                btnEdit.setText("Edit")
            }
            else{
                //code for edit
                address.isEnabled=true
                btnEdit.setText("Save")
            }
        }
        val acct = FirebaseAuth.getInstance().currentUser
        fullname.text = acct!!.getDisplayName().toString().toUpperCase()
        email.text = acct.getEmail().toString()
//        userId.text=acct.uid.toString()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}