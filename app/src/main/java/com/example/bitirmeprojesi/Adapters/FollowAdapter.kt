package com.example.bitirmeprojesi.Adapters

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.Activities.UserInfoActivity
import com.example.bitirmeprojesi.FollowRequestModel
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.UserModel
import com.example.bitirmeprojesi.databinding.ItemFollowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FollowAdapter(val followList: List<FollowRequestModel>,val context: Context, var TYPE: Int):
    RecyclerView.Adapter<FollowAdapter.FollowViewHolder>() {
    class FollowViewHolder(val view: ItemFollowBinding): RecyclerView.ViewHolder(view.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemFollowBinding>(inflater,
            R.layout.item_follow,parent,false)
        return FollowAdapter.FollowViewHolder(view)
    }

    override fun getItemCount(): Int {
        return followList.size
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        val follows = followList[position]

        // 0 ise followers listelecek, 1 ise following
        if (TYPE == 0){
            holder.view.tvName.text = follows.senderName
            //holder.view.btnFollow.text = "Takipçiyi çıkar"
            Glide.with(context).load(follows.senderImage).into(holder.view.imgContactItem)

            holder.view.btnPopup.setOnClickListener {

                val popupMenu = PopupMenu(context,holder.view.btnPopup, Gravity.TOP)

                popupMenu.menuInflater.inflate(R.menu.menu_follow_adapter,popupMenu.menu)
                val menuItem = popupMenu.menu.findItem(R.id.item)
                var referenceUsers =
                    FirebaseDatabase.getInstance().getReference("Users")
                menuItem.title = "Takipçiyi çıkar"
                popupMenu.setOnMenuItemClickListener {menuItem->
                    val id = menuItem.itemId

                    if (id == R.id.item){
                        var reference =
                            FirebaseDatabase.getInstance().getReference("Followers")
                        reference.child(follows.receiverId).child(follows.senderId).removeValue()

                        val databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("Users").child(follows.receiverId)
                        databaseReferenceReceiver.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val userModel = snapshot.getValue(UserModel::class.java)
                                    if (userModel != null) {
                                        val newCount = (userModel.followers.toInt() - 1).toString()
                                        val map2 = mapOf(
                                            "followers" to newCount
                                        )
                                        referenceUsers.child(follows.receiverId).updateChildren(map2)
                                    }
                                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                        val databaseReferenceSender = FirebaseDatabase.getInstance().getReference("Users").child(follows.senderId)
                        databaseReferenceSender.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val userModel = snapshot.getValue(UserModel::class.java)
                                    if (userModel != null) {
                                        val newCount = (userModel.following.toInt() - 1).toString()
                                        val map2 = mapOf(
                                            "following" to newCount
                                        )
                                        referenceUsers.child(follows.senderId).updateChildren(map2)
                                    }
                                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }

                    false
                }
                popupMenu.show()
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, UserInfoActivity::class.java)
                intent.putExtra("hisId",follows.senderId)
                intent.putExtra("hisImage",follows.senderImage)
                intent.putExtra("hisName",follows.senderName)
                it.context.startActivity(intent)
            }
        }else{
            holder.view.tvName.text = follows.receiverName
            Glide.with(context).load(follows.receiverImage).into(holder.view.imgContactItem)
            //holder.view.btnFollow.text = "Takip ediliyor"

            holder.view.btnPopup.setOnClickListener {

                val popupMenu = PopupMenu(context, holder.view.btnPopup, Gravity.TOP)
                popupMenu.menuInflater.inflate(R.menu.menu_follow_adapter, popupMenu.menu)
                var referenceUsers =
                    FirebaseDatabase.getInstance().getReference("Users")
                val menuItem = popupMenu.menu.findItem(R.id.item)

                menuItem.title = "Takibi bırak"
                popupMenu.setOnMenuItemClickListener {menuItem->
                    val id = menuItem.itemId

                    if (id == R.id.item){
                        var reference =
                            FirebaseDatabase.getInstance().getReference("Followers")
                        reference.child(follows.receiverId).child(follows.senderId).removeValue()

                        val databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("Users").child(follows.receiverId)
                        databaseReferenceReceiver.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val userModel = snapshot.getValue(UserModel::class.java)
                                    if (userModel != null) {
                                        val newCount = (userModel.followers.toInt() - 1).toString()
                                        val map2 = mapOf(
                                            "followers" to newCount
                                        )
                                        referenceUsers.child(follows.receiverId).updateChildren(map2)
                                    }
                                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                        val databaseReferenceSender = FirebaseDatabase.getInstance().getReference("Users").child(follows.senderId)
                        databaseReferenceSender.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val userModel = snapshot.getValue(UserModel::class.java)
                                    if (userModel != null) {
                                        val newCount = (userModel.following.toInt() - 1).toString()
                                        val map2 = mapOf(
                                            "following" to newCount
                                        )
                                        referenceUsers.child(follows.senderId).updateChildren(map2)
                                    }
                                    //Picasso.get().load(userModel!!.image).into(activityMessageBinding.imgProfile)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }

                    false
                }
                popupMenu.show()
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, UserInfoActivity::class.java)
                intent.putExtra("hisId",follows.receiverId)
                intent.putExtra("hisImage",follows.receiverImage)
                intent.putExtra("hisName",follows.receiverName)
                it.context.startActivity(intent)
            }
        }


    }
}