package com.example.localfarmersdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localfarmersdelivery.databinding.ActivityCartBinding
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CartAdapter { item ->
            Cart.remove(item.productId)
            refresh()
        }
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter

        binding.btnClear.setOnClickListener {
            Cart.clear()
            refresh()
        }
        binding.btnOrder.setOnClickListener { placeOrder() }
        refresh()

    }

    private fun placeOrder() {
        val items = Cart.list()
        if (items.isEmpty()) {
            Toast.makeText(this, "Coșul este gol", Toast.LENGTH_LONG).show()
            return
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.runTransaction { tx ->
            // 1. Citim stocul actual (toate citirile trebuie făcute înainte de scrieri)
            val snaps = items.map { it to tx.get(db.collection("products").document(it.productId)) }

            for ((item, snap) in snaps) {
                val stock = snap.getDouble("quantityKg") ?: 0.0
                if (!snap.exists() || stock < item.quantityKg) {
                    throw FirebaseFirestoreException(
                        "Stoc insuficient pentru ${item.name}",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }
            }

            // 2. Scădem stocul
            for ((item, snap) in snaps) {
                val stock = snap.getDouble("quantityKg") ?: 0.0
                tx.update(
                    db.collection("products").document(item.productId),
                    "quantityKg", stock - item.quantityKg
                )
            }

            // 3. Salvăm comanda
            val order = hashMapOf(
                "clientId" to uid,
                "status" to "plasată",
                "total" to Cart.total(),
                "date" to Timestamp.now(),
                "items" to items.map {
                    mapOf(
                        "productId" to it.productId,
                        "farmerId" to it.farmerId,
                        "name" to it.name,
                        "pricePerKg" to it.pricePerKg,
                        "quantityKg" to it.quantityKg
                    )
                }
            )
            tx.set(db.collection("orders").document(), order)
            null
        }.addOnSuccessListener {
            Toast.makeText(this, "Comandă plasată!", Toast.LENGTH_LONG).show()
            Cart.clear()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun refresh() {
        adapter.submit(Cart.list())
        binding.tvTotal.text = if (Cart.count() == 0) {
            "Coșul este gol"
        } else {
            "Total: ${"%.2f".format(Cart.total())} lei"
        }
    }
}