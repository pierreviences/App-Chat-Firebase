package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null
    var reference : DatabaseReference? = null
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        val userId = intent.getStringExtra("userId")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)

        binding.imgBack.setOnClickListener{
            onBackPressed()
        }

        reference!!.addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    binding.tvUserName.text = user!!.userName
                    if (user!!.profileImage == ""){
                        binding.imgProfile.setImageResource(R.drawable.profile_image)
                    }else{
                        Glide.with(this@ChatActivity).load(user.profileImage).into(binding.imgProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            )

        binding.btnSendMessage.setOnClickListener {
            var message: String = binding.etMessage.text.toString()
            if(message.isEmpty()){
                Toast.makeText(applicationContext,"", Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(firebaseUser!!.uid,userId,message)
            }
        }

    }

    private fun sendMessage(senderId:String, receiverId:String,message:String){
        var reference:DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        var hashMap:HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)

    }
}