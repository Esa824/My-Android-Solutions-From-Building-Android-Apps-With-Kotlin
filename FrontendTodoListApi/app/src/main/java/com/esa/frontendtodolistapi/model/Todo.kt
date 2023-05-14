package com.esa.frontendtodolistapi.model

data class Todo(var id: UInt, var category_id: UInt, var name: String, var description: String) : ListItem()