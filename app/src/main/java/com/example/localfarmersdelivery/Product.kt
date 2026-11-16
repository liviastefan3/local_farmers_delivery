package com.example.localfarmersdelivery

data class Product(
    val id: String = "",
    val farmerId: String = "",
    val name: String = "",
    val quantityKg: Double = 0.0,
    val pricePerKg: Double = 0.0
)