package com.example.bitirmeprojesi.Activities

import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.*
import com.example.bitirmeprojesi.Adapters.ChatAdapter
import com.example.bitirmeprojesi.Adapters.NoticeAdapter
import com.example.bitirmeprojesi.Adapters.NoticeCommentsAdapter
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.ViewModels.NoticeViewModel
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.example.bitirmeprojesi.databinding.ActivityNoticeDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_notices.*
import java.text.SimpleDateFormat
import java.util.*

class NoticeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeDetailBinding
    private lateinit var noticesViewModel : NoticeViewModel
    private var hisId: String? = null
    private var noticeID: String? = null
    private var noticeImageUrl : String? = null
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userImage: String
    val commentLiveData = MutableLiveData<List<NoticeCommentModel>>()
    //var commentList = ArrayList<NoticeCommentModel>()
    val commentList = mutableListOf<NoticeCommentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        userID = firebaseAuth.uid.toString()

        noticesViewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
        noticesViewModel.getDataFromFirebase()

        hisId = intent.getStringExtra("hisId")
        noticeID = intent.getStringExtra("noticeID")
        //getUserNoticeFromFirebase(hisId.toString())

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer {
            userImage = it.image
            userName = it.username
        })

        Toast.makeText(this,noticeID,Toast.LENGTH_SHORT).show()

        binding.noticeImage.setOnClickListener {
            val intent = Intent(it.context, FullscreenPhotoActivity::class.java)
            intent.putExtra("img",noticeImageUrl)
            it.context.startActivity(intent)
        }

        binding.imgContactItem.setOnClickListener{
            val intent = Intent(it.context, UserInfoActivity::class.java)
            intent.putExtra("hisId",hisId)
            it.context.startActivity(intent)
        }
        var now = System.currentTimeMillis()

        binding.btnSend.setOnClickListener {
            var comment:String = binding.commentEdt.text.toString()
            if(comment.isEmpty()){

            }else{
                val databaseReference = FirebaseDatabase.getInstance().getReference("NoticeComments")
                val map = mapOf(
                    "noticeID" to noticeID,
                    "userName" to userName,
                    "userImage" to userImage,
                    "userID" to userID,
                    "comment" to comment,
                    "commentTime" to now.toString()
                )
                NoticeCommentModel()
                databaseReference!!.child(noticeID.toString()).push().setValue(map)
            }

            binding.commentEdt.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(binding.commentEdt.windowToken, 0)

        }

        binding.btnComment.setOnClickListener {
            binding.commentLinear.visibility = View.VISIBLE
            binding.commentEdt.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        getCommentsFromFirebase()
        getNoticeFromFirebase(noticeID.toString())


        binding.recycler.setOnTouchListener { _, _ ->
            true
        }

    }

    fun getNoticeFromFirebase(noticeID:String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Notices")
        val query = databaseReference.orderByChild("noticeID").equalTo(noticeID)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (data in snapshot.children) {
                        val noticeModel = data.getValue(NoticeModel::class.java)
                        noticeModel?.let {
                            // istenilen child'ın verileri burada
                            binding.noticeModel = noticeModel
                            if (noticeModel.noticeDegree == "green"){
                                binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_green)
                                binding.btnLike.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.main_green), PorterDuff.Mode.SRC_IN)
                                binding.btnComment.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.main_green), PorterDuff.Mode.SRC_IN)
                            }
                            if (noticeModel.noticeDegree == "yellow"){
                                binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_yellow)
                                binding.btnLike.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.mainYellow), PorterDuff.Mode.SRC_IN)
                                binding.btnComment.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.mainYellow), PorterDuff.Mode.SRC_IN)
                            }
                            if (noticeModel.noticeDegree == "red"){
                                binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_red)
                                binding.btnLike.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.main_red2), PorterDuff.Mode.SRC_IN)
                                binding.btnComment.setColorFilter(ContextCompat.getColor(this@NoticeDetailActivity, R.color.main_red2), PorterDuff.Mode.SRC_IN)
                            }

                            var currentTime = System.currentTimeMillis()

                            val simpleDateFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
                            val simpleDateFormat2 = SimpleDateFormat("kk:mm dd.MM.yyyy", Locale.getDefault())


                            val minute = SimpleDateFormat("mm", Locale.getDefault())
                            val hours = SimpleDateFormat("kk", Locale.getDefault())
                            val day = SimpleDateFormat("dd", Locale.getDefault())
                            val month = SimpleDateFormat("MM", Locale.getDefault())
                            val year = SimpleDateFormat("yyyy", Locale.getDefault())

                            val sharedNoticeMinute = minute.format(noticeModel.noticeTime?.toLong())
                            val sharedNoticeHours = hours.format(noticeModel.noticeTime?.toLong())
                            val sharedNoticeDay = day.format(noticeModel.noticeTime?.toLong())
                            val sharedNoticeMonth = month.format(noticeModel.noticeTime?.toLong())
                            val sharedNoticeYear = year.format(noticeModel.noticeTime?.toLong())

                            val currentMinute = minute.format(currentTime)
                            val currentHours = hours.format(currentTime)
                            val currentDay = day.format(currentTime)
                            val currentMonth = month.format(currentTime)
                            val currentYear = year.format(currentTime)

                            val date = simpleDateFormat.format(noticeModel.noticeTime?.toLong())
                            val date2 = simpleDateFormat2.format(noticeModel.noticeTime?.toLong())


                            val now = Calendar.getInstance()
                            val noticeTimeMillis = noticeModel.noticeTime?.toLong() ?: 0
                            val noticeTime = Calendar.getInstance().apply { timeInMillis = noticeTimeMillis }

                            val diffMillis = now.timeInMillis - noticeTime.timeInMillis
                            val diffMinutes = diffMillis / (1000 * 60)
                            var zaman = currentTime - noticeModel.noticeTime!!.toLong()

                            if (zaman < 60_000
                            /*
                        if (now.get(Calendar.YEAR) == noticeTime.get(Calendar.YEAR) &&
                            now.get(Calendar.DAY_OF_YEAR) == noticeTime.get(Calendar.DAY_OF_YEAR) &&
                            now.get(Calendar.HOUR_OF_DAY) == noticeTime.get(Calendar.HOUR_OF_DAY) &&
                            now.get(Calendar.MINUTE) == noticeTime.get(Calendar.MINUTE)

                             */
                            ) {
                                binding.tvTime.text = "Şimdi"
                            }
                            else {
                                if (diffMinutes < 60 && diffMinutes >= 1){
                                    binding.tvTime.text = "${diffMinutes} dakika önce"
                                }else{
                                    if(sharedNoticeDay == currentDay && sharedNoticeMonth == currentMonth && sharedNoticeYear == currentYear){
                                        binding.tvTime.text = "Bugün ${date.toString()}"
                                    }else{
                                        binding.tvTime.text = date2.toString()

                                    }
                                }
                            }

                            if (noticeModel.noticeImage != ""){
                                noticeImageUrl = noticeModel.noticeImage
                                Glide.with(applicationContext).load(noticeModel.noticeImage).into(binding.noticeImage)
                                binding.noticeImage.visibility = View.VISIBLE
                            }else{
                                binding.noticeImage.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // işlem iptal edildiğinde yapılacaklar burada
            }
        })

    }

    fun getUserNoticeFromFirebase(uid:String) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Notices").child(uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val noticeModel = snapshot.getValue(NoticeModel::class.java)
                    binding.noticeModel = noticeModel
                    //Picasso.get().load(userModel!!.image).into(activityUserInfoBinding.imgProfile)
                    Glide.with(applicationContext).load(noticeModel!!.userImage).into(binding.imgContactItem)

                    if (noticeModel.noticeDegree == "green"){
                        binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_green)

                    }
                    if (noticeModel.noticeDegree == "yellow"){
                        binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_yellow)
                    }
                    if (noticeModel.noticeDegree == "red"){
                        binding.contextNoticeItem.background = this@NoticeDetailActivity.resources.getDrawable(R.drawable.border_notice_detail_red)
                    }

                    var currentTime = System.currentTimeMillis()

                    val simpleDateFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
                    val simpleDateFormat2 = SimpleDateFormat("kk:mm dd.MM.yyyy", Locale.getDefault())


                    val minute = SimpleDateFormat("mm", Locale.getDefault())
                    val hours = SimpleDateFormat("kk", Locale.getDefault())
                    val day = SimpleDateFormat("dd", Locale.getDefault())
                    val month = SimpleDateFormat("MM", Locale.getDefault())
                    val year = SimpleDateFormat("yyyy", Locale.getDefault())

                    val sharedNoticeMinute = minute.format(noticeModel.noticeTime?.toLong())
                    val sharedNoticeHours = hours.format(noticeModel.noticeTime?.toLong())
                    val sharedNoticeDay = day.format(noticeModel.noticeTime?.toLong())
                    val sharedNoticeMonth = month.format(noticeModel.noticeTime?.toLong())
                    val sharedNoticeYear = year.format(noticeModel.noticeTime?.toLong())

                    val currentMinute = minute.format(currentTime)
                    val currentHours = hours.format(currentTime)
                    val currentDay = day.format(currentTime)
                    val currentMonth = month.format(currentTime)
                    val currentYear = year.format(currentTime)

                    val date = simpleDateFormat.format(noticeModel.noticeTime?.toLong())
                    val date2 = simpleDateFormat2.format(noticeModel.noticeTime?.toLong())


                    val now = Calendar.getInstance()
                    val noticeTimeMillis = noticeModel.noticeTime?.toLong() ?: 0
                    val noticeTime = Calendar.getInstance().apply { timeInMillis = noticeTimeMillis }

                    val diffMillis = now.timeInMillis - noticeTime.timeInMillis
                    val diffMinutes = diffMillis / (1000 * 60)
                    var zaman = currentTime - noticeModel.noticeTime!!.toLong()

                    if (zaman < 60_000
                        /*
                    if (now.get(Calendar.YEAR) == noticeTime.get(Calendar.YEAR) &&
                        now.get(Calendar.DAY_OF_YEAR) == noticeTime.get(Calendar.DAY_OF_YEAR) &&
                        now.get(Calendar.HOUR_OF_DAY) == noticeTime.get(Calendar.HOUR_OF_DAY) &&
                        now.get(Calendar.MINUTE) == noticeTime.get(Calendar.MINUTE)

                         */
                    ) {
                        binding.tvTime.text = "Şimdi"
                    }
                    else {
                        if (diffMinutes < 60 && diffMinutes >= 1){
                            binding.tvTime.text = "${diffMinutes} dakika önce"
                        }else{
                            if(sharedNoticeDay == currentDay && sharedNoticeMonth == currentMonth && sharedNoticeYear == currentYear){
                                binding.tvTime.text = "Bugün ${date.toString()}"
                            }else{
                                binding.tvTime.text = date2.toString()

                            }
                        }
                    }

                    if (noticeModel.noticeImage != ""){
                        noticeImageUrl = noticeModel.noticeImage
                        Glide.with(applicationContext).load(noticeModel.noticeImage).into(binding.noticeImage)
                        binding.noticeImage.visibility = View.VISIBLE
                    }else{
                        binding.noticeImage.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


    fun getCommentsFromFirebase() {

        val databaseReference = FirebaseDatabase.getInstance().getReference("NoticeComments")
        val noticeIDReference = databaseReference.child(noticeID.toString())

        val commentList = mutableListOf<NoticeCommentModel>()

        noticeIDReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (childSnapshot in snapshot.children) {
                    val noticeComment = childSnapshot.getValue(NoticeCommentModel::class.java)
                    if (noticeComment != null) {
                        commentList.add(noticeComment)
                    }
                }
                commentList.reverse()
                val adapter = NoticeCommentsAdapter(
                    commentList,this@NoticeDetailActivity
                )


                binding.recycler.adapter = adapter
                binding.recycler.layoutManager = LinearLayoutManager(this@NoticeDetailActivity)
                // Yeni verileri kullanarak arayüzü güncelleme kodu
                // Örneğin, RecyclerView veya ListView kullanarak bir liste görünümü güncellenir.
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.commentEdt.windowToken, 0)
    }
}