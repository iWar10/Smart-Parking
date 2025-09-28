package com.example.smartparking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparking.databinding.ActivityAddFundsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFundsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFundsBinding
    private val auth = FirebaseAuth.getInstance()
    private val db   = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFundsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirmAdd.setOnClickListener {
            val text = binding.etAmount.text.toString().trim()
            val amount = text.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Introdu o sumă validă", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addFunds(amount)
        }
    }

    private fun addFunds(amount: Double) {
        val uid = auth.currentUser!!.uid
        val userRef = db.collection("users").document(uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val current = snapshot.getDouble("balance") ?: 0.0
            transaction.update(userRef, "balance", current + amount)
        }.addOnSuccessListener {
            Toast.makeText(this, "Ai adăugat $amount lei", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Eroare: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
