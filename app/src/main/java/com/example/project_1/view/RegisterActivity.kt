package com.example.project_1.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.room.Room
import com.example.project_1.R
import com.example.project_1.data.UserDao
import com.example.project_1.data.UserDataBase
import com.example.project_1.model.User

class RegisterActivity : AppCompatActivity() {
    var editTextUsername: EditText? = null
    var editTextEmail:EditText? = null
    var editTextPhone:EditText? = null
    var editTextPassword:EditText? = null
    var editTextCnfPassword:EditText? = null
    var buttonRegister: AppCompatButton? = null

    var textViewLogin: TextView? = null
    private var userDao: UserDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextUsername = findViewById(R.id.etFullName) as EditText
        editTextEmail = findViewById(R.id.etEmail) as EditText
        editTextPhone = findViewById(R.id.etPhone) as EditText
        editTextPassword = findViewById(R.id.etPass) as EditText
        editTextCnfPassword = findViewById(R.id.etConformPassword) as EditText
        buttonRegister = findViewById(R.id.btnRegister) as AppCompatButton
        textViewLogin = findViewById(R.id.tvLogin) as TextView
        textViewLogin!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        })
        userDao =
            Room.databaseBuilder<UserDataBase>(this, UserDataBase::class.java, "userdb")
                .allowMainThreadQueries()
                .build().userDao
        buttonRegister!!.setOnClickListener(View.OnClickListener {
            val userName = editTextUsername!!.getText().toString().trim { it <= ' ' }
            val email: String = editTextEmail!!.getText().toString().trim({ it <= ' ' })
            val phone: String = editTextPhone!!.getText().toString().trim({ it <= ' ' })
            val password: String = editTextPassword!!.getText().toString().trim({ it <= ' ' })
            val passwordConf: String = editTextCnfPassword!!.getText().toString().trim({ it <= ' ' })
            if (password == passwordConf) {
                val user = User()
                user.email = email
                user.name=userName
                user.phone = phone
                user.password= password
                userDao!!.insert(user)
                val moveToLogin =
                    Intent(this, LoginActivity::class.java)
                startActivity(moveToLogin)
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password is not matching",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
