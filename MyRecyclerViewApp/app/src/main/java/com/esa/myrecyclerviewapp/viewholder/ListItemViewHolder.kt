package com.esa.myrecyclerviewapp.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.esa.myrecyclerviewapp.model.ListItemUiModel

abstract class ListItemViewHolder(
    containerView: View
) : RecyclerView.ViewHolder(containerView) {
    abstract fun bindData(listItem: ListItemUiModel)
}