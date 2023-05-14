package com.esa.frontendtodolistapi.api

import com.esa.frontendtodolistapi.model.Category
import com.esa.frontendtodolistapi.model.JWT
import com.esa.frontendtodolistapi.model.Todo
import com.esa.frontendtodolistapi.model.User
import retrofit2.Call
import retrofit2.http.*

    interface TheTodoListApiService {

        @POST("categories")
        fun createCategory(@Header("Authorization") token: String, @Body category: Category): Call<Category>

        @PUT("categories")
        fun updateCategory(@Header("Authorization") token: String, @Body category: Category): Call<Category>

        @DELETE("categories/{id}")
        fun deleteCategoryById(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

        @POST("categories/{id}/todos")
        fun createTodoInCategory(@Header("Authorization") token: String, @Path("id") id: Int, @Body todo: Todo): Call<Todo>

        @PUT("categories/{id}/todos/{todo_id}")
        fun updateTodoInCategoryById(@Header("Authorization") token: String, @Path("id") id: Int, @Path("todo_id") todoId: Int, @Body todo: Todo): Call<Todo>

        @DELETE("categories/{id}/todos/{todo_id}")
        fun deleteTodoInCategoryById(@Header("Authorization") token: String, @Path("id") id: Int, @Path("todo_id") todoId: Int): Call<Void>

        @GET("categories")
        fun getCategories(@Header("Authorization") token: String): Call<List<Category>>

        @GET("categories/{id}/todos")
        fun getTodosForCategory(@Header("Authorization") token: String, @Path("id") id: Int): Call<List<Todo>>

        @POST("signup")
        fun signup(@Body user: User): Call<Void>

        @POST("login")
        fun login(@Body user: User): Call<JWT>

        @DELETE("credentials/delete")
        fun deleteAccount(@Header("Authorization") token: String) : Call<Void>

        @PUT("credentials/update")
        fun updateAccount(@Header("Authorization") token: String, @Body user: User) : Call<User>
    }
