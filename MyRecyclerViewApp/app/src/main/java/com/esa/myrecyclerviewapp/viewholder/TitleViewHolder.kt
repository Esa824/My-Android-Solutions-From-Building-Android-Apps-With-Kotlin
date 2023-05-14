package com.esa.myrecyclerviewapp.viewholder

import android.view.View
import android.widget.TextView
import com.esa.myrecyclerviewapp.R
import com.esa.myrecyclerviewapp.model.ListItemUiModel

class TitleViewHolder(
    containerView: View
) : ListItemViewHolder(containerView) {
    private val titleView: TextView
            by lazy { containerView.findViewById(R.id.item_title_title) }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Title) {
            "Expected ListItemUiModel.Title"
        }

        titleView.text = listItem.title
    }
}