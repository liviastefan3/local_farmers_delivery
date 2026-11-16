package com.example.localfarmersdelivery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            Navigation.openHome(this)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}