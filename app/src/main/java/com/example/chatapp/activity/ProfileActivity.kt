package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference  = FirebaseDatabase.getInstance().getReference().child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etUserName.text = user!!.userName

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message ,Toast.LENGTH_SHORT).show()
            }

        })

        binding.imgBack.setOnClickListener{
            onBackPressed()
        }

        binding.imgProfile.setOnClickListener {
            val intent = Intent(this@ProfileActivity, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}