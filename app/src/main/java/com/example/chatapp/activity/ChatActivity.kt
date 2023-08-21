package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.RetrofitInstance
import com.example.chatapp.adapter.ChatAdapter
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.Chat
import com.example.chatapp.model.NotificationData
import com.example.chatapp.model.PushNotification
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null
    var reference : DatabaseReference? = null
    private lateinit var binding: ActivityChatBinding

    var chatList = ArrayList<Chat>()
    var topic = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val intent = getIntent()
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")
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
                binding.etMessage.setText("")

            }else{
                sendMessage(firebaseUser!!.uid,userId,message)
                binding.etMessage.setText("")
                topic = "/topics/$userId"
                PushNotification(NotificationData(userName!!, message),
                    topic).also{
                        sendNotification(it)
                }

            }
        }

        readMessage(firebaseUser!!.uid,userId)
    }

    private fun sendMessage(senderId:String, receiverId:String,message:String){
        var reference:DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        var hashMap:HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)


    }

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
    }

    private fun sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.pushNotification(notification)
            if(response.isSuccessful){
                Toast.makeText(this@ChatActivity,"Response ${Gson().toJson(response)}", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@ChatActivity,response.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        }catch(e:Exception){
            Toast.makeText(this@ChatActivity,e.message, Toast.LENGTH_SHORT).show()

        }
    }
}