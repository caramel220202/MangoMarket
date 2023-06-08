package com.example.fastcampusdeepen.SecondHandDeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fastcampusdeepen.R
import com.example.fastcampusdeepen.SecondHandDeal.chatList.ChatListFragment
import com.example.fastcampusdeepen.SecondHandDeal.home.HomeFragment
import com.example.fastcampusdeepen.SecondHandDeal.myPage.MyPageFragment
import com.example.fastcampusdeepen.databinding.ActivitySecondHandDealBinding

class SecondHandDeal : AppCompatActivity() {
    private val secondHandDealBinding: ActivitySecondHandDealBinding by lazy {
        ActivitySecondHandDealBinding.inflate(layoutInflater)
    }
    private val fragmentHome = HomeFragment()
    private val fragmentChatList = ChatListFragment()
    private val fragmentMyPage = MyPageFragment()

    private var pressTime = 0L
    private val delayTime = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(secondHandDealBinding.root)

        replaceFragment(fragmentHome)

        secondHandDealBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(fragmentHome)
                R.id.chatList -> replaceFragment(fragmentChatList)
                R.id.myPage -> replaceFragment(fragmentMyPage)
            }
            true
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        val intervalTime = currentTime - pressTime
        if (delayTime >= intervalTime ){
            finish()
        }else{
            pressTime = currentTime
            Toast.makeText(this,"한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(secondHandDealBinding.fragmentContainer.id, fragment)
                commit()
            }
    }
}