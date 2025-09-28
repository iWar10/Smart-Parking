package com.example.smartparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparking.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completează email și parolă", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid

                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { snap ->
                            val fullName   = snap.getString("fullName") ?: "Utilizator"
                            val plate      = snap.getString("licensePlate") ?: "Utilizator"
                            val number     = snap.getString("phonenumber") ?: "Utilizator"

                            val intent = Intent(this, HomeActivity::class.java).apply {
                                putExtra("fullName", fullName)
                                putExtra("plate", plate)
                                putExtra("number", number)
                            }
                            startActivity(intent)
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,
                                "Conectat, dar nu pot citi profilul: ${e.localizedMessage}",
                                Toast.LENGTH_LONG).show()
                            // Totuși mergem mai departe
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        "Autentificare eșuată: ${e.localizedMessage}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }
}
