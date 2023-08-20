package com.example.chatapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {
    var userList = ArrayList<User>()
    private lateinit var binding: ActivityUsersBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var userList = ArrayList<User>()

//        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))
//        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))
//        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))
//        var userAdapter = UserAdapter(this, userList)
//        binding.userRecyclerView.adapter = userAdapter

        getUserList()

    }

    fun getUserList(){
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance("Users").getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(dataSnapShot:DataSnapshot in snapshot.children){
                    val user = dataSnapShot.getValue(User::class.java)
                    if(!user!!.userId.equals(firebase.uid)){
                        userList.add(user)
                    }
                }
                val userAdapter = UserAdapter(this@UsersActivity, userList)
                binding.userRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}