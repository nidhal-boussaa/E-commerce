package tn.rnu.isetr.e_com.ui.profil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.model.AddressItem

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val addresses = mutableListOf<AddressItem>()

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvAddressTitle: TextView =
            itemView.findViewById(R.id.tvAddressTitle)

        val badgePrimary: TextView =
            itemView.findViewById(R.id.badgePrimary)

        val badgeSecondary: TextView =
            itemView.findViewById(R.id.badgeSecondary)

        val tvAddressLine: TextView =
            itemView.findViewById(R.id.tvAddressLine)

        val tvCity: TextView =
            itemView.findViewById(R.id.tvCity)

        val tvPostalCode: TextView =
            itemView.findViewById(R.id.tvPostalCode)

        val tvCountry: TextView =
            itemView.findViewById(R.id.tvCountry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)

        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AddressViewHolder,
        position: Int
    ) {
        val address = addresses[position]

        val parts = address.address.split(",").map { it.trim() }

        holder.tvAddressTitle.text = "Adresse #${position + 1}"

        holder.badgePrimary.visibility =
            if (address.is_default) View.VISIBLE else View.GONE

        holder.badgeSecondary.visibility =
            if (address.is_default) View.GONE else View.VISIBLE

        holder.tvAddressLine.text = parts.getOrElse(0) { "" }

        holder.tvCity.text = parts.getOrElse(1) { "" }
        holder.tvPostalCode.text = parts.getOrElse(2) { "" }
        holder.tvCountry.text = parts.getOrElse(3) { "" }

        holder.tvCity.visibility =
            if (holder.tvCity.text.isNotBlank()) View.VISIBLE else View.GONE

        holder.tvPostalCode.visibility =
            if (holder.tvPostalCode.text.isNotBlank()) View.VISIBLE else View.GONE

        holder.tvCountry.visibility =
            if (holder.tvCountry.text.isNotBlank()) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = addresses.size

    fun updateAddresses(newAddresses: List<AddressItem>) {
        addresses.clear()
        addresses.addAll(newAddresses)
        notifyDataSetChanged()
    }
}