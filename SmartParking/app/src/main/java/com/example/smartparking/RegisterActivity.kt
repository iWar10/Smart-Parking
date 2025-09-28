package com.example.smartparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparking.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {

            val email      = binding.etEmail.text.toString().trim()
            val pass       = binding.etPass.text.toString().trim()
            val fullName   = binding.etName.text.toString().trim()
            val plate      = binding.etPlate.text.toString().trim()
            val number     = binding.etNumber.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this,
                    "Nu ai completat Email-ul",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6 ) {
                Toast.makeText(this,
                    "Nu ai completat Parola (Parola ≥6 caractere)",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fullName.isEmpty()) {
                Toast.makeText(this,
                    "Nu ai completat Numele",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (plate.isEmpty()) {
                Toast.makeText(this,
                    "Nu ai completat Numărul de înmatriculare",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (number.isEmpty()) {
                Toast.makeText(this,
                    "Nu ai completat Numărul de telefon",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid

                    val profile = mapOf(
                        "fullName"     to fullName,
                        "licensePlate" to plate,
                        "phonenumber"  to number,
                        "balance"      to 0.0,
                        "createdAt"    to FieldValue.serverTimestamp()
                    )

                    db.collection("users").document(uid).set(profile)
                        .addOnSuccessListener {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,
                                "Eroare Firestore: ${e.localizedMessage}",
                                Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        "Eroare înregistrare: ${e.localizedMessage}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }
}
