package com.esa.frontendtodolistapi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esa.frontendtodolistapi.api.TheTodoListApiService
import com.esa.frontendtodolistapi.model.Category
import com.esa.frontendtodolistapi.model.ListItem
import com.esa.frontendtodolistapi.model.Todo
import com.esa.frontendtodolistapi.model.User
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Suppress("LABEL_NAME_CLASH")
class MainActivity : AppCompatActivity(), CategoryTodoAdapter.OnItemClickListener {

    private lateinit var adapter: CategoryTodoAdapter

 private fun getAuthToken(): String {
        val sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        return "bearer " +  sharedPreferences.getString("JWT_TOKEN", null)
    }

    private var retrofit :Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val apiService: TheTodoListApiService by lazy { retrofit.create(TheTodoListApiService::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fun isTokenExpired(token: String): Boolean {
            try {
                val jwtParts = token.split(".")
                if (jwtParts.size != 3) {
                    // Invalid JWT format
                    return true
                }
                val decodedToken = JSONObject(String(Base64.decode(jwtParts[1], Base64.DEFAULT)))
                val expirationTime = decodedToken.getLong("exp")
                val currentTime = (System.currentTimeMillis() / 1000).toInt()
                return expirationTime < currentTime
            } catch (e: Exception) {
                // Failed to decode the token
                return true
            }
        }
        fun restartApplication(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            if (context is Activity) {
                context.finishAffinity()
            }
        }



        fun checkAuthTokenExpiredOrNotSet() {
                val jwtToken = getAuthToken()
                if (jwtToken.length == 7 && isTokenExpired(jwtToken)) {
                    setContentView(R.layout.signup)
                    val btnSignup = findViewById<Button>(R.id.btnSignup)
                    btnSignup?.setOnClickListener {
                        val username = findViewById<EditText>(R.id.etUsernameSignup).text.toString()
                        val password = findViewById<EditText>(R.id.etPasswordSignup).text.toString()
                        adapter.signup(User(username, password))
                    }

                    val btnHaveAnAccountLogin = findViewById<Button>(R.id.btnHaveAnAccountLogin)
                    btnHaveAnAccountLogin?.setOnClickListener {
                        setContentView(R.layout.login)
                        val btnLogin = findViewById<Button>(R.id.btnLogin)
                        btnLogin?.setOnClickListener {
                            val username = findViewById<EditText>(R.id.etUsernameLogin).text.toString()
                            val password = findViewById<EditText>(R.id.etPasswordLogin).text.toString()
                            adapter.login(User(username, password), this@MainActivity)
                            restartApplication(this@MainActivity)


                        }
                        val btnHaveAnAccountSignup = findViewById<Button>(R.id.btnHaveAnAccountSignup)
                        btnHaveAnAccountSignup?.setOnClickListener {
                        checkAuthTokenExpiredOrNotSet()
                        }
                    }
            }
        }





        // Initialize the adapter with an instance of the API service
        adapter = CategoryTodoAdapter(this, apiService, this)

        val swipeToDeleteCallback = CategoryTodoAdapter.SwipeToDeleteCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)

        // Set up the RecyclerView with the adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        checkAuthTokenExpiredOrNotSet()

        // Set up the button click listeners
        findViewById<Button>(R.id.btnAddCategory)?.setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.etCategoryName).text.toString()
            val category = Category(0U, categoryName)
            adapter.addCategory(category)
        }

        findViewById<Button>(R.id.btnAddTodo)?.setOnClickListener {
            val todoName = findViewById<EditText>(R.id.etTodoName).text.toString()
            val todoDescription = findViewById<EditText>(R.id.etTodoDescription).text.toString()
            val categoryId = findViewById<EditText>(R.id.etTodoCategoryId).text.toString().toInt()
            val todo = Todo(
                0U,
                name = todoName,
                description = todoDescription,
                category_id = categoryId.toUInt()
            )
            adapter.addTodoInCategory(categoryId, todo)
        }

        findViewById<Button>(R.id.btnUpdateCategory)?.setOnClickListener {
            val categoryId = findViewById<EditText>(R.id.etCategoryUpdateId).text.toString().toInt()
            val categoryName = findViewById<EditText>(R.id.etCategoryUpdateName).text.toString()
            val category = Category(id = categoryId.toUInt(), name = categoryName)
            adapter.updateCategory(category)
        }

       findViewById<Button>(R.id.btnUpdateTodo)?.setOnClickListener {
            val todoId = findViewById<EditText>(R.id.etTodoUpdateId).text.toString().toInt()
            val todoName = findViewById<EditText>(R.id.etTodoUpdateName).text.toString()
            val todoDescription =
                findViewById<EditText>(R.id.etTodoUpdateDescription).text.toString()
            val categoryId = findViewById<EditText>(R.id.etTodoUpdateCategoryId).text.toString().toInt()
            val todo = Todo(id = todoId.toUInt(), category_id = categoryId.toUInt(), name = todoName, description = todoDescription)
            adapter.updateTodoInCategory(categoryId, todoId, todo)
        }

        findViewById<Button>(R.id.btnDeleteAccount)?.setOnClickListener {
            adapter.deleteAccount()
            checkAuthTokenExpiredOrNotSet()
        }
        findViewById<Button>(R.id.btnUpdateCredentials)?.setOnClickListener {
            val username = findViewById<EditText>(R.id.etNameUpdateCredentials).text.toString()
            val password = findViewById<EditText>(R.id.etPasswordUpdateCredentials).text.toString()
            adapter.updateCredentials(User(username, password))
        }

        // Load the initial data
        loadCategories()
    }


    private fun loadCategories() {
            apiService.getCategories(getAuthToken()).enqueue(object : Callback<List<Category>> {
                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {
                    if (response.isSuccessful) {
                        val categories = response.body() ?: return
                        val items = mutableListOf<ListItem>()
                        var categoriesLoaded = 0

                        for (category in categories) {
                            items.add(category)
                                apiService.getTodosForCategory(getAuthToken(), category.id.toInt())
                                    .enqueue(object : Callback<List<Todo>> {
                                        override fun onResponse(
                                            call: Call<List<Todo>>,
                                            response: Response<List<Todo>>
                                        ) {
                                            if (response.isSuccessful) {
                                                val todos = response.body() ?: return
                                                for (todo in todos) {
                                                    items.add(todo)
                                                }
                                            }
                                            categoriesLoaded++
                                            if (categoriesLoaded == categories.size) {
                                                adapter.setItems(items)
                                            }
                                        }

                                        override fun onFailure(call: Call<List<Todo>>, t: Throwable) {
                                            Log.e(CategoryTodoAdapter.TAG, "Error: " + t.message.toString())
                                        }
                                    })
                        }
                    } else {
                        // handle Failure
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Log.e(CategoryTodoAdapter.TAG, "Error: " + t.message.toString())
                }
            })
    }
    override fun onCategoryClicked(category: Category) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(category.name)
        builder.setMessage("ID: ${category.id}")
        builder.setPositiveButton("OK", null)
        builder.create().show()
    }

    override fun onTodoClicked(todo: Todo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(todo.name)
        builder.setMessage("Description: ${todo.description}\nCategory ID: ${todo.category_id}\nID: ${todo.id}")
        builder.setPositiveButton("OK", null)
        builder.create().show()
    }
}
