package com.example.localfarmersdelivery

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localfarmersdelivery.databinding.ActivityProductFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private val db = FirebaseFirestore.getInstance()
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId")

        // Mod editare: încărcăm produsul existent în câmpuri
        productId?.let { id ->
            binding.btnSave.text = "Salvează modificările"
            db.collection("products").document(id).get()
                .addOnSuccessListener { d ->
                    val p = d.toObject(Product::class.java) ?: return@addOnSuccessListener
                    binding.etName.setText(p.name)
                    binding.etQuantity.setText(p.quantityKg.toString())
                    binding.etPrice.setText(p.pricePerKg.toString())
                }
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        val qty = binding.etQuantity.text.toString().toDoubleOrNull()
        val price = binding.etPrice.text.toString().toDoubleOrNull()

        if (name.isEmpty() || qty == null || price == null) {
            Toast.makeText(this, "Completează corect toate câmpurile", Toast.LENGTH_LONG).show()
            return
        }

        val data = hashMapOf<String, Any>(
            "name" to name,
            "quantityKg" to qty,
            "pricePerKg" to price
        )

        val id = productId
        if (id == null) {
            // CREATE
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            data["farmerId"] = uid
            db.collection("products").add(data)
                .addOnSuccessListener { finish() }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            // UPDATE
            db.collection("products").document(id).update(data)
                .addOnSuccessListener { finish() }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}