package com.example.chatapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST:Int = 2020

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etUserName.setText(user?.userName)

                if(user!!.userImage == ""){
                    binding.userImage.setImageResource(R.drawable.profile_image)
                }else{
                    Glide.with(this@ProfileActivity).load(user.userImage).into(binding.userImage)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message ,Toast.LENGTH_SHORT).show()
            }

        })

        binding.imgBack.setOnClickListener{
            val intent = Intent(this@ProfileActivity, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun chooseImage(){
        val intent: Intent = Intent()
        intent.type =  "image/"
        intent.action  = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode != null){
            filePath = data!!.data
            try{
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.userImage.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            val progressDialog: ProgressDialog = ProgressDialog(null)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    OnSuccessListener<UploadTask.TaskSnapshot>{
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext,"Uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    OnFailureListener{
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext,"Failed" + it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                .addOnProgressListener {
                    OnProgressListener<UploadTask.TaskSnapshot>{
                        var progress: Double = (100.0*it.bytesTransferred/it.totalByteCount)
                        progressDialog.setMessage("Uploaded" + progress.toInt()+"%")
                    }
                }
            }
    }
}