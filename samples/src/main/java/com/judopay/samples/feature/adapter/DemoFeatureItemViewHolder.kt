package com.judopay.samples.feature.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.samples.common.BindableRecyclerViewHolder
import com.judopay.samples.model.DemoFeature
import kotlinx.android.synthetic.main.item_demo_feature.view.*

class DemoFeatureItemViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<DemoFeature> {
    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        titleTextView.setText(model.title)
        surtitleTextView.setText(model.surTitle)
        setOnClickListener { listener?.invoke(model) }
    }
}
