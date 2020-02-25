package com.judopay.samples.common

interface BindableRecyclerViewHolder<V> {
    fun bind(model: V, listener: ((V) -> Unit)? = null)
}
