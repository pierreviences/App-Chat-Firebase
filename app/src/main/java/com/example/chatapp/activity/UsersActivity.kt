package com.example.chatapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var userList = ArrayList<User>()
        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))
        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))
        userList.add(User("yazid", "https://s.gravatar.com/avatar/af1318eb84ea7805042829d24456597f?s=200"))

        var userAdapter = UserAdapter(this, userList)
        binding.userRecyclerView.adapter = userAdapter

    }
}