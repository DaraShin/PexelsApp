package com.shinkevich.pexelsapp.View.Adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shinkevich.pexelsapp.Model.Database.ImageEntity
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.ViewModel.BookmarksViewModel
import java.io.File

class BookmarksRecyclerViewAdapter(
    private var bookmarksList: MutableList<ImageEntity>,
    private val viewModel: BookmarksViewModel
) :
    RecyclerView.Adapter<BookmarksRecyclerViewAdapter.BookmarksViewHolder>() {

    private var notLoadedImages: Int = bookmarksList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_with_author_item, parent, false)
        return BookmarksViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val bookmark = bookmarksList[position]
        holder.authorTextView.text = bookmark.author
        Glide.with(holder.itemView.context)
            .load(File(bookmark.filePath))
            .placeholder(R.drawable.image_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    notLoadedImages--
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
                viewModel.setImageForDetails(bookmarksList[holder.adapterPosition].imageId)
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
        return bookmarksList.size
    }

    fun setBookmarksList(newBookmarksList: List<ImageEntity>) {
        bookmarksList.clear()
        bookmarksList.addAll(newBookmarksList)
        notLoadedImages = newBookmarksList.size
        notifyDataSetChanged()
    }

    class BookmarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewBookmarks)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextViewBookmarks)
    }
}