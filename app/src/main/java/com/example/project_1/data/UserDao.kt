package com.example.project_1.data

import androidx.room.*
import com.example.project_1.model.User


@Dao
interface UserDao {
    @Query("SELECT * FROM User where user_email= :email and user_pass= :password")
    fun getUser(email: String?, password: String?): User?

    @Insert
    fun insert(user: User?)

    @Update
    fun update(user: User?)

    @Delete
    fun delete(user: User?)

}
