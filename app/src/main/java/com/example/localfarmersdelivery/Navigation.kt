package com.example.localfarmersdelivery

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Navigation {

    // Citește rolul utilizatorului din Firestore și deschide ecranul potrivit
    fun openHome(activity: Activity) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
            return
        }

        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val target = if (doc.getString("role") == "farmer") {
                    FarmerHomeActivity::class.java
                } else {
                    ClientHomeActivity::class.java
                }
                activity.startActivity(Intent(activity, target))
                activity.finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}