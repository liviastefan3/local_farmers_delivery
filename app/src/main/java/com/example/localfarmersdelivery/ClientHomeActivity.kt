package com.example.localfarmersdelivery

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localfarmersdelivery.databinding.ActivityClientHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ClientHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ProductAdapter(
            showActions = false,
            onAddToCart = { p -> askQuantity(p) }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        listener = db.collection("products")
            .whereGreaterThan("quantityKg", 0)
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

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            Cart.clear()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartButton()
    }

    private fun updateCartButton() {
        binding.btnCart.text = if (Cart.count() == 0) {
            "Vezi coșul"
        } else {
            "Vezi coșul (${Cart.count()} produse • ${"%.2f".format(Cart.total())} lei)"
        }
    }

    private fun askQuantity(p: Product) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Cantitate (kg)"

        AlertDialog.Builder(this)
            .setTitle(p.name)
            .setMessage("Disponibil: ${p.quantityKg} kg • ${p.pricePerKg} lei/kg")
            .setView(input)
            .setPositiveButton("Adaugă în coș") { _, _ ->
                val qty = input.text.toString().replace(',', '.').toDoubleOrNull()
                val alreadyInCart = Cart.quantityOf(p.id)
                when {
                    qty == null || qty <= 0 ->
                        Toast.makeText(this, "Introdu o cantitate validă", Toast.LENGTH_LONG).show()
                    qty + alreadyInCart > p.quantityKg ->
                        Toast.makeText(this, "Nu poți depăși stocul disponibil", Toast.LENGTH_LONG).show()
                    else -> {
                        Cart.add(p, qty)
                        updateCartButton()
                        Toast.makeText(this, "Adăugat în coș", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Anulează", null)
            .show()
    }

    override fun onDestroy() {
        listener?.remove()
        super.onDestroy()
    }
}