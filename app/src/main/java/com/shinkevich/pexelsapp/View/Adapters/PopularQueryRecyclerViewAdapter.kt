package com.shinkevich.pexelsapp.View.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.ViewModel.HomeViewModel

class PopularQueryRecyclerViewAdapter(
    private val popularQueryList: MutableList<String>,
    private val viewModel: HomeViewModel,
    private val context: Context,
    private var selectedPosition: Int = -1
) :
    RecyclerView.Adapter<PopularQueryRecyclerViewAdapter.PopularQueryViewHolder>() {

    private var selectedItem: PopularQueryViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularQueryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_query_item, parent, false)
        return PopularQueryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopularQueryViewHolder, position: Int) {
        if (position == selectedPosition) {
            setItemSelection(holder)
        }
        holder.popularQueryTextView.text = popularQueryList[position]
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                clearItemSelection()
                selectedItem = holder
                selectedPosition = holder.adapterPosition
                viewModel.setPopularQuerySearch(holder.popularQueryTextView.text.toString())
                setItemSelection(holder)
            }
        })
    }

    override fun getItemCount(): Int {
        return popularQueryList.size
    }

    fun setPopularQueryList(newPopularQueryList: List<String>) {
        popularQueryList.clear()
        popularQueryList.addAll(newPopularQueryList)
        notifyDataSetChanged()
    }

    fun setSearchQuery(query: String): Int {
        clearItemSelection()
        var selectedQueryIdx : Int = -1
        val preparedQuery = query.trim()
        for (i in 0..(popularQueryList.size - 1)) {
            if (popularQueryList[i].equals(preparedQuery, true)) {
                selectedPosition = i
                selectedQueryIdx = i
                notifyDataSetChanged()
                break
            }
        }
        return selectedQueryIdx
    }

    fun setSelectedItemPosition(selectedPosition: Int){
        this.selectedPosition = selectedPosition
        notifyDataSetChanged()
    }

    private fun clearItemSelection() {
        selectedPosition = -1
        if (selectedItem != null) {
            selectedItem!!.itemView.background =
                context.getDrawable(R.drawable.text_block_gray_shape)
            selectedItem!!.popularQueryTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
        }
    }

    private fun setItemSelection(holder: PopularQueryViewHolder) {
        selectedItem = holder
        holder.itemView.background = context.getDrawable(R.drawable.text_block_selected_shape)
        holder.popularQueryTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    fun getSelectedPosition() : Int{
        return selectedPosition
    }

    class PopularQueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val popularQueryTextView: TextView = itemView.findViewById(R.id.popularQueryTextView)
    }
}