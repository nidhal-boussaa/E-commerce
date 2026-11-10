
package tn.rnu.isetr.e_com.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import tn.rnu.isetr.e_com.model.OrderResponse
import tn.rnu.isetr.e_com.model.AddressResponse
import tn.rnu.isetr.e_com.model.AddressRequest
import tn.rnu.isetr.e_com.model.AddressSaveResponse
import tn.rnu.isetr.e_com.model.Category
import tn.rnu.isetr.e_com.model.LoginRequest
import tn.rnu.isetr.e_com.model.LoginResponse
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.model.ProfileResponse
import tn.rnu.isetr.e_com.model.RegisterRequest
import tn.rnu.isetr.e_com.model.RegisterResponse
import tn.rnu.isetr.e_com.model.UpdateProfileRequest
import tn.rnu.isetr.e_com.model.UpdateProfileResponse

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProduct(
        @Path("id") id: Int
    ): Response<Product>

    @GET("api/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @GET("api/profile/addresses")
    suspend fun getAddresses(
        @Header("Authorization") token: String
    ): Response<AddressResponse>

    // Ajouter une adresse principale
    @POST("api/profile/address")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body request: AddressRequest
    ): Response<AddressSaveResponse>

    // Modifier une adresse principale
    @PUT("api/profile/address")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Body request: AddressRequest
    ): Response<AddressSaveResponse>

    @GET("api/orders/my-orders")
    suspend fun getMyOrders(
        @Header("Authorization") token: String
    ): Response<OrderResponse>
}
