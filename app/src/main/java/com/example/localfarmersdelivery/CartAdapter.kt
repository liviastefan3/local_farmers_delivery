package com.example.localfarmersdelivery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localfarmersdelivery.databinding.ItemCartBinding

class CartAdapter(
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    private var items: List<CartItem> = emptyList()

    fun submit(list: List<CartItem>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvName.text = item.name
        holder.b.tvDetails.text =
            "${item.quantityKg} kg × ${item.pricePerKg} lei = ${"%.2f".format(item.subtotal)} lei"
        holder.b.btnRemove.setOnClickListener { onRemove(item) }
    }
}