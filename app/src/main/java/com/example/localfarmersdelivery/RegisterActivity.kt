package com.example.localfarmersdelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localfarmersdelivery.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val role = if (binding.rbFarmer.isChecked) "farmer" else "client"

        if (name.isEmpty() || email.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Completează toate câmpurile (parola minim 6 caractere)", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid
                val user = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "role" to role
                )
                db.collection("users").document(uid).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cont creat!", Toast.LENGTH_LONG).show()
                        Navigation.openHome(this)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Eroare Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}