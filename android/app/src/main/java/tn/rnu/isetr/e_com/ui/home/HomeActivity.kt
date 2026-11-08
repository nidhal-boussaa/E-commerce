package tn.rnu.isetr.e_com.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tn.rnu.isetr.e_com.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}