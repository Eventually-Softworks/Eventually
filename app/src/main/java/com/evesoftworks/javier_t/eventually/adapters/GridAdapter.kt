package com.evesoftworks.javier_t.eventually.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.evesoftworks.javier_t.eventually.R

class GridAdapter(private val mContext: Context) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView

        if (convertView == null) {
            imageView = ImageView(mContext)
            imageView.layoutParams = ViewGroup.LayoutParams(420, 420)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(mPhotosId[position])

        return imageView
    }

    override fun getItem(position: Int): Any = mPhotosId[position]

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = mPhotosId.count()

    private val mPhotosId = arrayOf(
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema,
            R.drawable.cinema
    )


}