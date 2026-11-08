/*package com.example.eshop.models



/**
 * Entité Utilisateur en Kotlin (compatible Room Database et SQLite)
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val profileImageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)*/