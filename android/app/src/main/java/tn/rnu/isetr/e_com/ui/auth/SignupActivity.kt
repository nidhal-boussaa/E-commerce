
package tn.rnu.isetr.e_com.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tn.rnu.isetr.e_com.databinding.ActivitySignupBinding
import tn.rnu.isetr.e_com.viewmodel.RegisterViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retour vers Login
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Inscription
        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etSignUpEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etSignUpPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            when {
                fullName.isEmpty() -> {
                    binding.etFullName.error = "Entrez votre nom complet"
                }

                email.isEmpty() -> {
                    binding.etSignUpEmail.error = "Entrez votre email"
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etSignUpEmail.error = "Email invalide"
                }

                phone.isEmpty() -> {
                    binding.etPhone.error = "Entrez votre numéro de téléphone"
                }

                password.isEmpty() -> {
                    binding.etSignUpPassword.error = "Entrez votre mot de passe"
                }

                password.length < 6 -> {
                    binding.etSignUpPassword.error =
                        "Mot de passe : 6 caractères minimum"
                }

                confirmPassword != password -> {
                    binding.etConfirmPassword.error =
                        "Les mots de passe ne correspondent pas"
                }

                else -> {
                    viewModel.register(fullName, email, password)
                }
            }
        }

        // Résultat inscription
        viewModel.registerResult.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Erreur inscription
        viewModel.registerError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}
