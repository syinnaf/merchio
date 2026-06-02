package com.example.merchio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.merchio.fragments.CartFragment
import com.example.merchio.fragments.HomeFragment
import com.example.merchio.fragments.ProductSearchFragment
import com.example.merchio.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        if (savedInstanceState == null) replace(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replace(HomeFragment())
                R.id.nav_search -> replace(ProductSearchFragment())
                R.id.nav_cart -> replace(CartFragment())
                R.id.nav_profile -> replace(ProfileFragment())
                else -> false
            }
            true
        }
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
