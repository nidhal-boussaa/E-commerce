package com.example.eshop.activities

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
 * SignUpActivity - Écran de création de compte E-Shop
 * Valide le nom complet, l'email, le téléphone et la correspondance des mots de passe.
 */
class SignUpActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var etFullName: EditText
    private lateinit var etEmailSignUp: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPasswordSignUp: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnTogglePasswordSignUp: ImageButton
    private lateinit var btnToggleConfirmPassword: ImageButton
    private lateinit var btnSignUp: AppCompatButton
    private lateinit var tvGoToLogin: TextView

    private var isPasswordVisible = false
    private var isConfirmVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        etFullName = findViewById(R.id.etFullName)
        etEmailSignUp = findViewById(R.id.etEmailSignUp)
        etPhone = findViewById(R.id.etPhone)
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnTogglePasswordSignUp = findViewById(R.id.btnTogglePasswordSignUp)
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnTogglePasswordSignUp.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePassword(etPasswordSignUp, btnTogglePasswordSignUp, isPasswordVisible)
        }

        btnToggleConfirmPassword.setOnClickListener {
            isConfirmVisible = !isConfirmVisible
            togglePassword(etConfirmPassword, btnToggleConfirmPassword, isConfirmVisible)
        }

        btnSignUp.setOnClickListener {
            attemptSignUp()
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun togglePassword(editText: EditText, button: ImageButton, visible: Boolean) {
        if (visible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
           /* button.setImageResource(R.drawable.ic_visibility)*/
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
           /* button.setImageResource(R.drawable.ic_visibility_off)*/
        }
        editText.setSelection(editText.text.length)
    }

    private fun attemptSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmailSignUp.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPasswordSignUp.text.toString().trim()
        val confirm = etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName.error = getString(R.string.err_empty_fields)
            etFullName.requestFocus()
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailSignUp.error = getString(R.string.err_invalid_email)
            etEmailSignUp.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = getString(R.string.err_empty_fields)
            etPhone.requestFocus()
            return
        }

        if (password.length < 6) {
            etPasswordSignUp.error = getString(R.string.err_short_password)
            etPasswordSignUp.requestFocus()
            return
        }

        if (password != confirm) {
            etConfirmPassword.error = getString(R.string.err_password_match)
            etConfirmPassword.requestFocus()
            return
        }

        // Inscription réussie
        Toast.makeText(this, getString(R.string.msg_signup_success), Toast.LENGTH_SHORT).show()
        finish()
    }
}