package com.mahin.oneuilauncher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahin.oneuilauncher.R
import com.mahin.oneuilauncher.model.AppInfo

class AppDrawerAdapter(
    private var apps: List<AppInfo>,
    private val onAppClick: (View, AppInfo) -> Unit,
    private val onAppLongClick: (View, AppInfo) -> Unit
) : RecyclerView.Adapter<AppDrawerAdapter.DrawerViewHolder>() {

    inner class DrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconImage)
        val label: TextView = itemView.findViewById(R.id.iconLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return DrawerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.label.text = app.label
        holder.itemView.setOnClickListener { onAppClick(holder.icon, app) }
        holder.itemView.setOnLongClickListener { onAppLongClick(holder.itemView, app); true }
    }

    override fun getItemCount() = apps.size

    fun filter(query: String, fullList: List<AppInfo>) {
        apps = if (query.isBlank()) fullList else fullList.filter { it.label.contains(query, ignoreCase = true) }
        notifyDataSetChanged()
    }
}
