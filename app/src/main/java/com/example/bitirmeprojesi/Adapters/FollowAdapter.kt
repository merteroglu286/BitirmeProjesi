package com.example.bitirmeprojesi.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.FollowRequestModel
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.ItemFollowBinding

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
            holder.view.btnFollow.text = "Kaldır"
            Glide.with(context).load(follows.senderImage).into(holder.view.imgContactItem)
        }else{
            holder.view.tvName.text = follows.receiverName
            Glide.with(context).load(follows.receiverImage).into(holder.view.imgContactItem)
            holder.view.btnFollow.text = "Takip ✓"
        }
    }
}