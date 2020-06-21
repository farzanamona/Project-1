package com.example.project_1.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class User :
    Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id = 0

    @ColumnInfo(name = "user_name")
    @Nullable
    var name: String? = null

    @ColumnInfo(name = "user_email")
    @Nullable
    var email: String? = null

    @ColumnInfo(name = "user_phone")
    @Nullable
    var phone: String? = null

    @ColumnInfo(name = "user_address")
    @Nullable
    var address: String? = null

    @ColumnInfo(name = "user_image")
    @Nullable
    var image: String? = null

    @ColumnInfo(name = "user_pass")
    @Nullable
    var password: String? = null
}
