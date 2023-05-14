package com.esa.sweetandsavory

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esa.sweetandsavory.model.Flavor
import com.esa.sweetandsavory.model.RecipeUiModel

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }
    private val addSweetButton: View by lazy { findViewById(R.id.sweet_button) }
    private val addSavoryButton: View by lazy { findViewById(R.id.savory_button) }

    private val recipesAdapter by lazy {
        RecipesAdapter(layoutInflater, object : RecipesAdapter.OnClickListener {
            override fun onItemClick(recipe: RecipeUiModel) {
                showSelectionDialog(recipe)
            }
        })
    }

    private fun showSelectionDialog(recipe: RecipeUiModel) {
        AlertDialog.Builder(this)
            .setTitle(recipe.title)
            .setMessage(recipe.description)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = recipesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val itemTouchHelper = ItemTouchHelper(recipesAdapter.swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addSweetButton.setOnClickListener {
            recipesAdapter.addRecipe(
                RecipeUiModel(
                    findViewById<TextView>(R.id.recipe_title).text.toString(),
                    findViewById<TextView>(R.id.recipe_description).text.toString(),
                    Flavor.SWEET
                )
            )
        }

        addSavoryButton.setOnClickListener {
            recipesAdapter.addRecipe(
                RecipeUiModel(
                    findViewById<TextView>(R.id.recipe_title).text.toString(),
                    findViewById<TextView>(R.id.recipe_description).text.toString(),
                    Flavor.SAVORY
                )
            )
        }
    }
}
