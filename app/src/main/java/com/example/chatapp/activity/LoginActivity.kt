package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password= binding.etPassword.text.toString()

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()

            }else{
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                        if(it.isSuccessful){
                            binding.etEmail.setText("")
                            binding.etPassword.setText("")
                            val intent = Intent(this@LoginActivity, UsersActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(applicationContext, "Email and Password invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}