package com.esa.sweetandsavory

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esa.sweetandsavory.model.ListItem
import com.esa.sweetandsavory.model.RecipeUiModel
import com.esa.sweetandsavory.model.TitleUiModel

abstract class BaseViewHolder(
    containerView: View
) : RecyclerView.ViewHolder(containerView) {
    abstract fun bindData(listItem: ListItem)
}

class TitleViewHolder(containerView: View) : BaseViewHolder(containerView) {
    private val titleView: TextView
            by lazy { containerView.findViewById(R.id.title_label) }

    override fun bindData(listItem: ListItem) {
        titleView.text = (listItem as TitleUiModel).title
    }
}

class RecipeViewHolder(
    containerView: View,
    private val onClickListener: OnClickListener
) : BaseViewHolder(containerView) {
    private val titleView: TextView
            by lazy { containerView.findViewById(R.id.recipe_title) }

    override fun bindData(listItem: ListItem) {
        titleView.text = (listItem as RecipeUiModel).title
        titleView.setOnClickListener {
            onClickListener.onClick(listItem)
        }
    }

    interface OnClickListener {
        fun onClick(recipe: RecipeUiModel)
    }
}