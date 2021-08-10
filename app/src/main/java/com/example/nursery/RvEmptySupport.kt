package com.example.nursery

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RvEmptySupport(val emptyView: TextView, val recyclerView: RecyclerView): RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
        super.onChanged()
        handleView()
    }

    private fun handleView() {
        if (checkIfEmpty() ) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else
        {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        handleView()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        handleView()
    }

    private fun checkIfEmpty(): Boolean {
        return recyclerView.adapter?.itemCount!! <= 0
    }
}