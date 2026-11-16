package com.example.localfarmersdelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localfarmersdelivery.databinding.ActivityFarmerHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FarmerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ProductAdapter(
            onEdit = { p ->
                startActivity(
                    Intent(this, ProductFormActivity::class.java).putExtra("productId", p.id)
                )
            },
            onDelete = { p -> confirmDelete(p) }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, ProductFormActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        listener = db.collection("products")
            .whereEqualTo("farmerId", uid)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    d.toObject(Product::class.java)?.copy(id = d.id)
                } ?: emptyList()
                adapter.submit(list)
            }
    }

    private fun confirmDelete(p: Product) {
        AlertDialog.Builder(this)
            .setTitle("Ștergi produsul?")
            .setMessage(p.name)
            .setPositiveButton("Șterge") { _, _ ->
                db.collection("products").document(p.id).delete()
            }
            .setNegativeButton("Anulează", null)
            .show()
    }

    override fun onDestroy() {
        listener?.remove()
        super.onDestroy()
    }
}