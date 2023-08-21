package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.activity.ChatActivity
import com.example.chatapp.model.Chat
import com.example.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context, private val chatList:ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtUsername: TextView = view.findViewById(R.id.userName)
        val txtTemp: TextView = view.findViewById(R.id.temp)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
        val layoutUser: LinearLayout = view.findViewById(R.id.layoutUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUsername.text = user.userName
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.profile_image).into(holder.imgUser)

        holder.layoutUser.setOnClickListener {
            val intent  = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            context.startActivity(intent)
        }
    }
}