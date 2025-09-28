package com.example.smartparking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparking.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snap ->
                binding.etNameS.setText(snap.getString("fullName") ?: "")
                binding.etPlateS.setText(snap.getString("licensePlate") ?: "")
            }

        binding.btnSaveProfile.setOnClickListener {
            val newName  = binding.etNameS.text.toString().trim()
            val newPlate = binding.etPlateS.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "Numele nu poate fi gol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = mapOf(
                "fullName"     to newName,
                "licensePlate" to newPlate
            )

            db.collection("users").document(uid).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profil actualizat!", Toast.LENGTH_SHORT).show()
                    finish()                 // închide ecranul Setări
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        "Eroare: ${e.localizedMessage}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }
}
