package com.shinkevich.pexelsapp.View.Adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shinkevich.pexelsapp.Model.Image
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.ViewModel.HomeViewModel

class ImagesRecyclerViewAdapter(
    private var imagesList: ArrayList<Image>,
    private val viewModel: HomeViewModel
) :
    RecyclerView.Adapter<ImagesRecyclerViewAdapter.ImagesViewHolder>() {

    private var notLoadedImages: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false)
        return ImagesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imagesList[position].url)
            .timeout(5000)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.image_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    decreaseNotLoadedImages()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    decreaseNotLoadedImages()
                    return false
                }

            })
            .into(holder.imageView)

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                viewModel.setImageForDetails(imagesList[holder.adapterPosition].id)
            }
        })
    }

    private fun decreaseNotLoadedImages() {
        notLoadedImages--
        if (notLoadedImages == 0) {
            viewModel.updateDataIsLoading(false)
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    fun setImageList(imagesList: ArrayList<Image>) {
        this.imagesList.clear()
        this.imagesList.addAll(imagesList)
        notLoadedImages = imagesList.size
        this.notifyDataSetChanged()
    }

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewHome)
    }
}