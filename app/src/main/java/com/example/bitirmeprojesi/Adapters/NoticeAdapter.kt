package com.example.bitirmeprojesi.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bitirmeprojesi.NoticeModel
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.ItemNoticeBinding
import kotlinx.android.synthetic.main.item_notice.view.*

class NoticeAdapter(val noticeList: ArrayList<NoticeModel>,context: Context): RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    val context = context
    class NoticeViewHolder(var view: ItemNoticeBinding) : RecyclerView.ViewHolder(view.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemNoticeBinding>(inflater,R.layout.item_notice,parent,false)
        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.view.noticeModel = noticeList[position]

        if (noticeList[position].noticeDegree == "green"){
            holder.view.contextNoticeItem.background = context.resources.getDrawable(R.drawable.border_dialog_green)
        }
        if (noticeList[position].noticeDegree == "yellow"){
            holder.view.contextNoticeItem.background = context.resources.getDrawable(R.drawable.border_dialog_yellow)
        }
        if (noticeList[position].noticeDegree == "red"){
            holder.view.contextNoticeItem.background = context.resources.getDrawable(R.drawable.border_dialog_red)
        }
    }

    fun updateNoticesList(newNoticesList:List<NoticeModel>){
        noticeList.clear()
        noticeList.addAll(newNoticesList)
        notifyDataSetChanged()
    }
}