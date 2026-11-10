package tn.rnu.isetr.e_com.utils

import android.content.Context

class FavoriteManager(context: Context) {

    private val preferences = context.getSharedPreferences("ecommerce_favorites", Context.MODE_PRIVATE)

    fun getFavorites(): Set<Int> =
        preferences.getStringSet("favorite_ids", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

    fun isFavorite(productId: Int) = productId in getFavorites()

    private fun save(favorites: Set<Int>) =
        preferences.edit()
            .putStringSet("favorite_ids", favorites.map { it.toString() }.toSet())
            .apply()

    fun addFavorite(productId: Int) = save(getFavorites() + productId)

    fun removeFavorite(productId: Int) = save(getFavorites() - productId)

    fun toggleFavorite(productId: Int): Boolean {
        val add = !isFavorite(productId)
        if (add) addFavorite(productId) else removeFavorite(productId)
        return add
    }
}