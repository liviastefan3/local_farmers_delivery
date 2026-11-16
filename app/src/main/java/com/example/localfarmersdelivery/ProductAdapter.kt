package com.example.localfarmersdelivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localfarmersdelivery.databinding.ItemProductBinding

class ProductAdapter(
    private val showActions: Boolean = true,
    private val onEdit: (Product) -> Unit = {},
    private val onDelete: (Product) -> Unit = {},
    private val onAddToCart: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    private var items: List<Product> = emptyList()

    fun submit(list: List<Product>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.b.tvName.text = p.name
        holder.b.tvDetails.text = "${p.quantityKg} kg • ${p.pricePerKg} lei/kg"

        holder.b.btnEdit.setOnClickListener { onEdit(p) }
        holder.b.btnDelete.setOnClickListener { onDelete(p) }
        holder.b.btnCart.setOnClickListener { onAddToCart?.invoke(p) }

        val actionsVis = if (showActions) View.VISIBLE else View.GONE
        holder.b.btnEdit.visibility = actionsVis
        holder.b.btnDelete.visibility = actionsVis
        holder.b.btnCart.visibility = if (onAddToCart != null) View.VISIBLE else View.GONE
    }
}