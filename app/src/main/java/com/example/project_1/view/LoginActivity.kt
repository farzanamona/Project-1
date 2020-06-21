package com.example.project_1.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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


class LoginActivity : AppCompatActivity() {
    private var context: Context? =null
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var buttonLogin: AppCompatButton? = null
    var textViewRegister: TextView? = null
    var db: UserDao? = null
    var dataBase: UserDataBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context=this
        editTextEmail = findViewById(R.id.etEmail) as EditText
        editTextPassword = findViewById(R.id.etPass) as EditText
        buttonLogin = findViewById(R.id.btnSignin) as AppCompatButton
        textViewRegister = findViewById(R.id.tvRegister)
        dataBase =
            Room.databaseBuilder<UserDataBase>(this, UserDataBase::class.java, "userdb")
                .allowMainThreadQueries()
                .build()
        db = dataBase!!.userDao
        textViewRegister!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        })
        buttonLogin!!.setOnClickListener(View.OnClickListener {
            val email = editTextEmail!!.getText().toString().trim { it <= ' ' }
            val password = editTextPassword!!.getText().toString().trim { it <= ' ' }
            var user = db!!.getUser(email,password)
            if (user != null) {
                val i = Intent(this, HomeActivity::class.java)
                i.putExtra("User", user)
                startActivity(i)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Unregistered user, or incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
