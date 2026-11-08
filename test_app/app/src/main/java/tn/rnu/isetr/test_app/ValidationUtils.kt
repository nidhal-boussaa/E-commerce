/*package com.example.eshop.utils

import android.util.Patterns

/**
 * Utilitaires de validation en Kotlin pur
 */
object ValidationUtils {

    /**
     * Vérifie la validité d'une adresse email
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    /**
     * Vérifie que le mot de passe respecte la longueur minimale (6 caractères)
     */
    fun isValidPassword(password: String): Boolean {
        return password.trim().length >= 6
    }

    /**
     * Vérifie le format d'un numéro de téléphone
     */
    fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace("\s+".toRegex(), "")
        return cleanPhone.length >= 8 && cleanPhone.all { it.isDigit() || it == '+' }
    }

    /**
     * Vérifie que deux mots de passe sont identiques
     */
    fun doPasswordsMatch(pass1: String, pass2: String): Boolean {
        return pass1 == pass2
    }
}*/