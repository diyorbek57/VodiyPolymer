package com.ayizor.vodiypolymer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.model.Product
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter


class HomeBannerAdapter(val context: Context, val products: List<Product?>?) :
    BannerAdapter<Product?, HomeBannerAdapter.BannerViewHolder?>(products) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = ImageView(parent.context)
        imageView.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
        return BannerViewHolder(imageView)
    }

    inner class BannerViewHolder(view: ImageView) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView

        init {
            imageView = view
        }
    }

    @SuppressLint("CheckResult")
    override fun onBindView(holder: BannerViewHolder?, data: Product?, position: Int, size: Int) {
        Glide.with(context).load(data?.product_image!![0].image_url)
    }
}
