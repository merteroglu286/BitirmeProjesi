package com.example.bitirmeprojesi.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bitirmeprojesi.Adapters.DashboardViewPagerAdapter
import com.example.bitirmeprojesi.Dashboard.ChatFragment
import com.example.bitirmeprojesi.Dashboard.MapsFragment
import com.example.bitirmeprojesi.Dashboard.ProfileFragment
import com.example.bitirmeprojesi.Dashboard.SearchFragment
import com.example.bitirmeprojesi.ViewModels.ProfileViewModel
import com.example.bitirmeprojesi.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var profileViewModels: ProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(this, androidx.lifecycle.Observer { userModel->
            binding.userModel = userModel

            //Picasso.get().load(userModel.image).into(binding.imgProfile)
        })

        val viewPager = binding.viewPager


        val fragments: ArrayList<Fragment> = arrayListOf(
            ChatFragment(),
            SearchFragment(),
            ProfileFragment()
        )

        val adapter = DashboardViewPagerAdapter(fragments,this)
        viewPager.adapter = adapter
        //viewPager.currentItem = 1


        binding.bottomBarFabMap.setOnClickListener{
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(com.example.bitirmeprojesi.R.anim.enter_from_left,com.example.bitirmeprojesi.R.anim.exit_to_right,com.example.bitirmeprojesi.R.anim.enter_from_right,com.example.bitirmeprojesi.R.anim.exit_to_left)
                .replace(com.example.bitirmeprojesi.R.id.dashboardContainer, MapsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}