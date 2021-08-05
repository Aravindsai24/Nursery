package com.example.nursery.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.nursery.GoogleSignInActivity
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
        val root: View = binding.root
        btnLogout = binding.logoutbtn
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, GoogleSignInActivity::class.java))
        }
        val fullname: TextView = binding.tvName
        val email: TextView = binding.tvEmailProfile
        val userId:TextView=binding.tvuserId
        val acct = FirebaseAuth.getInstance().currentUser
        fullname.text = acct!!.getDisplayName().toString().toUpperCase()
        email.text = acct.getEmail().toString()
        userId.text=acct.uid.toString()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}