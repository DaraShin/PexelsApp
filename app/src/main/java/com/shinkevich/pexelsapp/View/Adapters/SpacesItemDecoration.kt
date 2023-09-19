package com.shinkevich.pexelsapp.View.Adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpacesItemDecoration(space: Int) : ItemDecoration() {
    private val halfSpace: Int

    init {
        halfSpace = space / 2
    }


    override fun getItemOffsets (
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.paddingLeft != halfSpace) {
            parent.setPadding(-halfSpace, -halfSpace, -halfSpace, -halfSpace)
            parent.setClipToPadding(false)
        }

        outRect.top = halfSpace
        outRect.bottom = halfSpace
        outRect.left = halfSpace
        outRect.right = halfSpace
    }
}