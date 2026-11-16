package com.example.localfarmersdelivery

data class CartItem(
    val productId: String,
    val farmerId: String,
    val name: String,
    val pricePerKg: Double,
    val quantityKg: Double
) {
    val subtotal: Double get() = pricePerKg * quantityKg
}

object Cart {
    private val items = mutableMapOf<String, CartItem>()

    fun quantityOf(productId: String): Double = items[productId]?.quantityKg ?: 0.0

    fun add(p: Product, qty: Double) {
        val existing = items[p.id]
        val newQty = (existing?.quantityKg ?: 0.0) + qty
        items[p.id] = CartItem(p.id, p.farmerId, p.name, p.pricePerKg, newQty)
    }

    fun remove(productId: String) {
        items.remove(productId)
    }

    fun clear() = items.clear()

    fun list(): List<CartItem> = items.values.toList()

    fun total(): Double = items.values.sumOf { it.subtotal }

    fun count(): Int = items.size
}