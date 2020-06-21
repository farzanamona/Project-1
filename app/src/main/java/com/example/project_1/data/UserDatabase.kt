package com.example.project_1.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project_1.model.User


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDataBase : RoomDatabase() {

    abstract val userDao: UserDao?
}
