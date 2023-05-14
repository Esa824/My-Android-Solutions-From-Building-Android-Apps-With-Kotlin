package com.esa.sweetandsavory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.esa.sweetandsavory.model.Flavor
import com.esa.sweetandsavory.model.ListItem
import com.esa.sweetandsavory.model.RecipeUiModel
import com.esa.sweetandsavory.model.TitleUiModel

private const val VIEW_TYPE_TITLE = 0
private const val VIEW_TYPE_RECIPE = 1

class RecipesAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<BaseViewHolder>() {
    val swipeToDeleteCallback = SwipeToDeleteCallback()


    private val savoryTitle = TitleUiModel("SAVORY")
    private val sweetTitle = TitleUiModel("SWEET")
    private val listItems = mutableListOf<ListItem>(savoryTitle, sweetTitle)

    fun addRecipe(recipe: RecipeUiModel) {
        if (recipe.title.isBlank() || recipe.description.isBlank()) return
        // Find the index to insert the new recipe, based on its flavor
        val insertionIndex = listItems.indexOf(when (recipe.flavor) {
            Flavor.SAVORY -> savoryTitle
            Flavor.SWEET -> sweetTitle
        }) + 1
        // Add the recipe to the list and notify the adapter of the insertion
        listItems.add(insertionIndex, recipe)
        notifyItemInserted(insertionIndex)
    }

    fun removeRecipe(position: Int) {
        listItems.removeAt(position)
        notifyItemRemoved(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // Create a new view holder based on the view type
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                val view = layoutInflater.inflate(R.layout.item_title, parent, false)
                TitleViewHolder(view)
            }
            VIEW_TYPE_RECIPE -> {
                val view = layoutInflater.inflate(R.layout.item_recipe, parent, false)
                RecipeViewHolder(view, object : RecipeViewHolder.OnClickListener {
                    override fun onClick(recipe: RecipeUiModel) {
                        onClickListener.onItemClick(recipe)
                    }
                })
            }
            else -> throw IllegalArgumentException("Unknown view type requested: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        // Bind the data to the view holder based on its position in the list
        holder.bindData(listItems[position])
    }

    override fun getItemViewType(position: Int): Int =
        // Return the view type based on the item at the specified position
        when (listItems[position]) {
            is TitleUiModel -> VIEW_TYPE_TITLE
            is RecipeUiModel -> VIEW_TYPE_RECIPE
            else -> throw IllegalArgumentException("Unknown item type at position: $position")
        }

    override fun getItemCount(): Int = listItems.size

    interface OnClickListener {
        fun onItemClick(recipe: RecipeUiModel)
    }
    inner class SwipeToDeleteCallback :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = if (viewHolder is RecipeViewHolder) {
            makeMovementFlags(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) or makeMovementFlags(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        } else {
            0
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            removeRecipe(position)
        }
    }
}
