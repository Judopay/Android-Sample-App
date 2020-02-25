package com.judopay.samples.feature.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.samples.R
import com.judopay.samples.common.inflate
import com.judopay.samples.model.DemoFeature

class DemoFeaturesAdapter(features: List<DemoFeature> = emptyList(), private val listener: (DemoFeature) -> Unit) : RecyclerView.Adapter<DemoFeatureItemViewHolder>() {

    var features: List<DemoFeature> = features
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoFeatureItemViewHolder {
        return DemoFeatureItemViewHolder(parent.inflate(R.layout.item_demo_feature))
    }

    override fun onBindViewHolder(holder: DemoFeatureItemViewHolder, position: Int) {
        holder.bind(features[position], listener)
    }

    override fun getItemCount(): Int = features.size
}
