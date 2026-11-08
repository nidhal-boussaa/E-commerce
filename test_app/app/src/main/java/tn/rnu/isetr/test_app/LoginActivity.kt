package com.example.eshop.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

import tn.rnu.isetr.test_app.R

/**
 * LoginActivity - Écran de connexion E-Shop
 * Gère la saisie utilisateur, la validation locale et la navigation.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnLogin: AppCompatButton
    private lateinit var tvGoToSignUp: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp)
    }

    private fun setupListeners() {
        // Afficher / masquer le mot de passe
        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Action de connexion
        btnLogin.setOnClickListener {
            attemptLogin()
        }

        // Redirection vers l'écran d'inscription
        tvGoToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
           /* btnTogglePassword.setImageResource(R.drawable.)*/
        } else {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            /*btnTogglePassword.setImageResource(R.drawable.ic_visibility_off)*/
        }
        etPassword.setSelection(etPassword.text.length)
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // 1. Validation champ vide
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.err_empty_fields)
            etEmail.requestFocus()
            return
        }

        // 2. Validation format email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.err_invalid_email)
            etEmail.requestFocus()
            return
        }

        // 3. Validation mot de passe
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.err_empty_fields)
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = getString(R.string.err_short_password)
            etPassword.requestFocus()
            return
        }

        // Validation locale réussie
        Toast.makeText(this, getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show()

        // Redirection vers l'écran d'accueil principal (MainActivity)
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)
        // finish()
    }
}