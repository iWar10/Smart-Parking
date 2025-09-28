package com.example.smartparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparking.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserData()

        binding.btnOpenMap.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnAddFunds.setOnClickListener {
            startActivity(Intent(this, AddFundsActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, StartActivity::class.java))
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserBalance()
    }

    private fun loadUserData() {
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snap ->
                binding.tvUserName.text  = snap.getString("fullName")     ?: "Utilizator"
                binding.tvUserPlate.text = snap.getString("licensePlate") ?: ""
                val bal = snap.getDouble("balance") ?: 0.0
                binding.tvBalance.text   = "Sold: %.2f lei".format(bal)
            }
            .addOnFailureListener {
                binding.tvUserName.text  = "Utilizator"
                binding.tvUserPlate.text = ""
                binding.tvBalance.text   = "Sold: 0.00 lei"
            }
    }

    private fun loadUserBalance() {
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snap ->
                val bal = snap.getDouble("balance") ?: 0.0
                binding.tvBalance.text = "Sold: %.2f lei".format(bal)
            }
    }
}
