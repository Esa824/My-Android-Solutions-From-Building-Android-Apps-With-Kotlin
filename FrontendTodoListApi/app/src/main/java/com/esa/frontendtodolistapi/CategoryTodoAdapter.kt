package com.esa.frontendtodolistapi

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esa.frontendtodolistapi.api.TheTodoListApiService
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import com.esa.frontendtodolistapi.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryTodoAdapter(
    private val context: Context,
    private val apiService: TheTodoListApiService,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CATEGORY_VIEW_TYPE = 1
        const val TAG = "API"
        const val TODO_VIEW_TYPE = 2
    }

    private val items: MutableList<ListItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CATEGORY_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_item, parent, false)
                CategoryViewHolder(view)
            }
            TODO_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
                TodoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (@Suppress("NAME_SHADOWING") val item = items[position]) {
            is Category -> (holder as CategoryViewHolder).bind(item)
            is Todo -> (holder as TodoViewHolder).bind(item)
        }
        holder.itemView.setOnClickListener {
            when (item) {
                is Category -> onItemClickListener.onCategoryClicked(item)
                is Todo -> onItemClickListener.onTodoClicked(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Category -> CATEGORY_VIEW_TYPE
            is Todo -> TODO_VIEW_TYPE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addCategory(category: Category) {
        getAuthToken().let {
            apiService.createCategory(it, category).enqueue(object : Callback<Category> {
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    if (response.isSuccessful) {
                        val newCategory = response.body() ?: return
                        items.add(newCategory)
                        notifyItemInserted(items.size - 1)
                    }
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                }
            })
        }
    }
    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        return "bearer " + sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun login(user: User, context: Context) {
        val call = apiService.login(user)
        call.enqueue(object : Callback<JWT> {
            override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                if (response.isSuccessful) {
                    val jwt = response.body()
                    if (jwt != null) {
                        val sharedPreferences = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("JWT_TOKEN", jwt.JwtToken)
                        editor.apply()
                    } else {
                        Log.e(TAG, "JWT is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login failed: $errorBody")
                }
            }

            override fun onFailure(call: Call<JWT>, t: Throwable) {
                Log.e(TAG, "Login failed: ${t.message}")
            }
        })
    }

    fun signup(user: User) {
        val call = apiService.signup(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Signup successful")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Signup failed: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "Signup failed: ${t.message}")
            }
        })
    }

    fun updateCategory(category: Category) {
            apiService.updateCategory(getAuthToken(), category).enqueue(object : Callback<Category> {
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    if (response.isSuccessful) {
                        val updatedCategory = response.body() ?: return
                        if (updatedCategory.id != category.id) {
                            Log.e(TAG, "Updated category has a different id than the original category")
                            return
                        }
                        val position = items.indexOfFirst { it is Category && it.id == updatedCategory.id }
                        if (position >= 0) {
                            items[position] = updatedCategory
                            notifyItemChanged(position)
                        }
                    }
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                }
            })
    }

    fun deleteCategory(category: Category) {
            apiService.deleteCategoryById(getAuthToken(), category.id.toInt()).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        val position = items.indexOfFirst { it is Category && it.id == category.id }
                        if (position >= 0) {
                            items.removeAt(position)
                            notifyItemRemoved(position)
                            // Remove all todos in the category
                            val todosInCategory = items.filterIsInstance<Todo>().filter { it.category_id == category.id }
                            items.removeAll(todosInCategory)
                            notifyItemRangeRemoved(position + 1, todosInCategory.size)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                }
            })
    }


    fun updateTodoInCategory(id: Int, todoId: Int, todo: Todo) {
            apiService.updateTodoInCategoryById(getAuthToken(), id, todoId, todo).enqueue(object : Callback<Todo> {
                override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                    if (response.isSuccessful) {
                        val updatedTodo = response.body() ?: return
                        val todoPosition = items.indexOfFirst { it is Todo && it.id.toInt() == todoId }
                        if (todoPosition >= 0) {
                            items[todoPosition] = updatedTodo
                            notifyItemChanged(todoPosition)
                        }
                    }
                }

                override fun onFailure(call: Call<Todo>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                }
            })
    }

    fun deleteTodoInCategory(id: Int, todoId: Int) {
            apiService.deleteTodoInCategoryById(getAuthToken(), id, todoId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        val todoPosition = items.indexOfFirst { it is Todo && it.id.toInt() == todoId }
                        if (todoPosition >= 0) {
                            items.removeAt(todoPosition)
                            notifyItemRemoved(todoPosition)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                }
            })
    }

    fun addTodoInCategory(categoryId: Int, todo: Todo) {
            apiService.createTodoInCategory(getAuthToken(), categoryId, todo).enqueue(object : Callback<Todo> {
                override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                    if (response.isSuccessful) {
                        val newTodo = response.body() ?: return
                        val categoryPosition = items.indexOfFirst { it is Category && it.id.toInt() == categoryId }
                        if (categoryPosition >= 0) {
                            items.add(categoryPosition + 1, newTodo)
                            notifyItemInserted(categoryPosition + 1)
                        }
                    } else {
                        Log.e(TAG, "Error: " + response.code())
                        // Show error message to the user
                    }
                }

                override fun onFailure(call: Call<Todo>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message.toString())
                    // Show error message to the user
                }
            })
    }
    fun deleteAccount() {
        apiService.deleteAccount(getAuthToken()).enqueue(object : Callback<Void> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Clear the JWTToken from SharedPreferences
                    val sharedPreferences = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("JWT_TOKEN", "").apply()
                    items.clear()
                    notifyDataSetChanged()
                } else {
                    // Handle the error response
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle the failure
            }
        })
    }
    fun updateCredentials(user: User) {
        apiService.updateAccount(getAuthToken(), user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Updated Credentials")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, "Error " + t.message)
            }
        })
    }

    class SwipeToDeleteCallback(
        private val adapter: CategoryTodoAdapter
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Do nothing, we only support swiping to delete
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return
            }
            val item = adapter.items.getOrNull(position)
            if (item is Category) {
                adapter.deleteCategory(item)
            } else if (item is Todo && position > 0) {
                val categoryId = (adapter.items[position - 1] as? Category)?.id ?: return
                adapter.deleteTodoInCategory(categoryId.toInt(), item.id.toInt())
            }
        }


    }
    interface OnItemClickListener {
        fun onCategoryClicked(category: Category)
        fun onTodoClicked(todo: Todo)
    }


}


