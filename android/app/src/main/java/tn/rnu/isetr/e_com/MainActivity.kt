
package tn.rnu.isetr.e_com

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import tn.rnu.isetr.e_com.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Navigation Bottom Menu
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            if (item.itemId == R.id.nav_cart) {
                Toast.makeText(
                    this,
                    "Panier bientôt disponible",
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

        // Synchronisation du menu avec la page actuelle
        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.bottomNavigation.visibility =
                if (destination.id == R.id.nav_product_detail) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            val selectedItem = binding.bottomNavigation.menu
                .findItem(destination.id)

            if (selectedItem != null && !selectedItem.isChecked) {
                selectedItem.isChecked = true
            }
        }
    }
}
