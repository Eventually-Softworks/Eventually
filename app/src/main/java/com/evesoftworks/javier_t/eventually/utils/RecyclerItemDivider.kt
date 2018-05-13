package com.evesoftworks.javier_t.eventually.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.evesoftworks.javier_t.eventually.R

class RecyclerItemDivider(context: Context) : RecyclerView.ItemDecoration() {
    var mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider)!!

    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        parent?.let {
            val left = it.paddingStart + 32
            val right = it.width - it.paddingEnd - 32
            val childCount = it.childCount

            for (i in 0 until childCount) {
                val child = it.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}