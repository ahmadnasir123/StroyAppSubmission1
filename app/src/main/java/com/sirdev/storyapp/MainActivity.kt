package com.sirdev.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sirdev.storyapp.databinding.ActivityMainBinding
import com.sirdev.storyapp.ui.auth.login.LoginFragment
import com.sirdev.storyapp.ui.home.HomeFragment
import com.sirdev.storyapp.utils.Preferences

class MainActivity : AppCompatActivity() {

    private var activityMainBinding: ActivityMainBinding? = null
    lateinit var userLoginPref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding?.root)
        userLoginPref =Preferences(this)
        supportActionBar?.hide()
        checkSession()
    }


    fun moveToFragment(fragment: Fragment) {
        this.supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logoutMenu -> {
                doLogout()
                true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }

    private fun doLogout(){
        userLoginPref.logout()
        moveToFragment(LoginFragment())
    }

    private fun checkSession() {
        if (!userLoginPref.getLoginData().isLogin) {
            moveToFragment(LoginFragment())
        } else {
            moveToFragment(HomeFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityMainBinding = null
    }
}