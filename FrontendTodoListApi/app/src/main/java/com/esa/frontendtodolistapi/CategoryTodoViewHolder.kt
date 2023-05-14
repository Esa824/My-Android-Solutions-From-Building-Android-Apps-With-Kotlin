package com.esa.frontendtodolistapi

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esa.frontendtodolistapi.model.Category
import com.esa.frontendtodolistapi.model.Todo

class CategoryViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val categoryNameTextView: TextView = itemView.findViewById(R.id.tvCategoryName) ?: throw NullPointerException("etCategoryName view not found")

    fun bind(category: Category) {
        categoryNameTextView.text = category.name
    }
}

class TodoViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val todoNameTextView: TextView = itemView.findViewById(R.id.tvTodoName)
    private val todoDescriptionTextView: TextView = itemView.findViewById(R.id.tvTodoDescription)

    fun bind(todo: Todo) {
        todoNameTextView.text = todo.name
        todoDescriptionTextView.text = todo.description
    }
}
