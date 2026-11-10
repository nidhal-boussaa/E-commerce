
package tn.rnu.isetr.e_com.ui.profil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.ActivityAddressesBinding
import tn.rnu.isetr.e_com.databinding.ActivityAddAddressBinding
import tn.rnu.isetr.e_com.databinding.ActivityPersonalInfoBinding
import tn.rnu.isetr.e_com.databinding.ActivityOrdersBinding
import tn.rnu.isetr.e_com.databinding.ActivityOrderDetailBinding
import tn.rnu.isetr.e_com.databinding.FragmentProfileBinding
import tn.rnu.isetr.e_com.model.AddressRequest
import tn.rnu.isetr.e_com.model.Order
import tn.rnu.isetr.e_com.model.ProfileUser
import tn.rnu.isetr.e_com.ui.auth.LoginActivity
import tn.rnu.isetr.e_com.utils.SessionManager
import tn.rnu.isetr.e_com.viewmodel.AddressViewModel
import tn.rnu.isetr.e_com.viewmodel.ProfileViewModel
import tn.rnu.isetr.e_com.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val session by lazy {
        SessionManager(requireContext())
    }

    private val viewModel: ProfileViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    private var currentProfile: ProfileUser? = null

    private var personalInfoDialog: Dialog? = null
    private var addressesDialog: Dialog? = null
    private var addAddressDialog: Dialog? = null
    private var ordersDialog: Dialog? = null
    private var orderDetailDialog: Dialog? = null

    private var addressBinding: ActivityAddressesBinding? = null
    private var addressAdapter: AddressAdapter? = null

    private var ordersBinding: ActivityOrdersBinding? = null
    private var orderAdapter: OrderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeProfile()
        loadProfile()

        withToken {
            orderViewModel.loadOrders(it)
        }

        addressViewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            addressAdapter?.updateAddresses(addresses)
        }

        addressViewModel.error.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) toast(it, true)
        }

        // Résultat de l'ajout ou de la modification d'adresse
        addressViewModel.saveSuccess.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                addressViewModel.clearSaveSuccess()

                toast(message)
                addAddressDialog?.dismiss()
                addAddressDialog = null

                val token = session.getToken()

                if (!token.isNullOrEmpty()) {
                    addressViewModel.loadAddresses(token)
                    loadProfile()
                }
            }
        }

        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->

            binding.tvOrderBadge.text = orders.size.toString()

            binding.tvOrderBadge.visibility =
                if (orders.isEmpty()) View.GONE else View.VISIBLE

            orderAdapter?.updateOrders(orders)

            ordersBinding?.let { b ->
                val empty = orders.isEmpty()

                b.rvOrders.visibility =
                    if (empty) View.GONE else View.VISIBLE

                b.layoutEmptyOrders.visibility =
                    if (empty) View.VISIBLE else View.GONE
            }
        }

        orderViewModel.error.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) toast(it, true)
        }
    }

    private fun toast(msg: String, long: Boolean = false) {
        Toast.makeText(
            requireContext(),
            msg,
            if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }

    private fun withToken(action: (String) -> Unit) {
        val token = session.getToken()

        if (token.isNullOrEmpty()) {
            toast("Session expirée")
        } else {
            action(token)
        }
    }

    private fun showDialog(root: View, btnBack: View): Dialog {
        val dialog = Dialog(requireContext())

        dialog.setContentView(root)

        btnBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        return dialog
    }

    // Format : 8 novembre 2026
    private fun formatDate(date: String?): String {
        if (date.isNullOrBlank()) return "Non disponible"

        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
                .format(input.parse(date.substringBefore("T"))!!)
        } catch (e: Exception) {
            "Non disponible"
        }
    }

    private fun setupListeners() = with(binding) {

        rowPersonalInfo.setOnClickListener {
            showPersonalInfoDialog()
        }

        rowAddresses.setOnClickListener {
            showAddressesDialog()
        }

        rowOrders.setOnClickListener {
            showOrdersDialog()
        }

        rowLogout.setOnClickListener {
            session.clearSession()

            val intent = Intent(
                requireContext(),
                LoginActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
        }
    }

    private fun loadProfile() {
        withToken {
            viewModel.loadProfile(it)
        }
    }

    private fun observeProfile() {

        viewModel.profile.observe(viewLifecycleOwner) { profile ->

            profile ?: return@observe

            currentProfile = profile

            binding.tvUserName.text = profile.full_name
            binding.tvUserEmail.text = profile.email

            binding.tvAvatarInitials.text = profile.full_name
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") {
                    it.first().uppercase()
                }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->

            if (message.isNullOrEmpty()) return@observe

            toast(message)
            personalInfoDialog?.dismiss()
            loadProfile()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) toast(it, true)
        }
    }

    // Informations personnelles
    private fun showPersonalInfoDialog() {

        val profile = currentProfile
            ?: return toast("Profil non disponible")

        val b = ActivityPersonalInfoBinding.inflate(layoutInflater)

        fun setEditable(editable: Boolean) {

            b.etFullName.isEnabled = editable
            b.etEmail.isEnabled = editable
            b.etPhone.isEnabled = editable

            b.btnEdit.visibility =
                if (editable) View.GONE else View.VISIBLE

            b.btnSave.visibility =
                if (editable) View.VISIBLE else View.GONE
        }

        b.etFullName.setText(profile.full_name)
        b.etEmail.setText(profile.email)
        b.etPhone.setText(profile.phone ?: "")
        b.tvCreatedAt.text = formatDate(profile.created_at)

        setEditable(false)

        b.btnEdit.setOnClickListener {
            setEditable(true)
        }

        b.btnSave.setOnClickListener {

            val fullName = b.etFullName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val phone = b.etPhone.text.toString().trim()

            when {
                fullName.isEmpty() || email.isEmpty() ->
                    toast("Le nom et l'email sont obligatoires")

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    b.etEmail.error = "Email invalide"

                else -> withToken {
                    viewModel.updateProfile(it, fullName, email, phone)
                }
            }
        }

        personalInfoDialog = showDialog(b.root, b.btnBack)
    }

    // Liste des adresses
    private fun showAddressesDialog() {

        val token = session.getToken()

        if (token.isNullOrEmpty()) {
            toast("Session expirée")
            return
        }

        val b = ActivityAddressesBinding.inflate(layoutInflater)

        addressBinding = b
        addressAdapter = AddressAdapter()

        b.rvAddresses.layoutManager =
            LinearLayoutManager(requireContext())

        b.rvAddresses.adapter = addressAdapter

        addressesDialog = showDialog(b.root, b.btnClose)
        val hasAddress = !currentProfile?.address.isNullOrBlank()

        if (hasAddress) {
            b.btnAddAddress.text = "Modifier"
        } else {
            b.btnAddAddress.text = "Ajouter"
        }

        // Ajouter ou modifier l'adresse principale
        b.btnAddAddress.setOnClickListener {
            showAddAddressDialog()
        }

        b.btnBottomClose.setOnClickListener {
            addressesDialog?.dismiss()
        }

        addressesDialog?.setOnDismissListener {
            addressBinding = null
            addressAdapter = null
            addressesDialog = null
        }

        addressViewModel.loadAddresses(token)
    }

    // Formulaire d'ajout ou de modification
    private fun showAddAddressDialog() {

        val token = session.getToken()

        if (token.isNullOrEmpty()) {
            toast("Session expirée")
            return
        }

        val b = ActivityAddAddressBinding.inflate(layoutInflater)

        val currentAddress = currentProfile?.address
        val isEditing = !currentAddress.isNullOrBlank()

        if (isEditing) {

            b.tvAddressTitle.text = "Modifier mon adresse"
            b.btnSaveAddress.text = "Enregistrer les modifications"

            val parts = currentAddress.orEmpty().split(",")

            b.etAddress.setText(parts.getOrNull(0)?.trim() ?: "")
            b.etCity.setText(parts.getOrNull(1)?.trim() ?: "")
            b.etPostalCode.setText(parts.getOrNull(2)?.trim() ?: "")
            b.etCountry.setText(parts.getOrNull(3)?.trim() ?: "")

        } else {

            b.tvAddressTitle.text = "Ajouter une adresse"
            b.btnSaveAddress.text = "Ajouter"
        }

        addAddressDialog = showDialog(b.root, b.btnBack)

        b.btnSaveAddress.setOnClickListener {

            val address = b.etAddress.text.toString().trim()
            val city = b.etCity.text.toString().trim()
            val postalCode = b.etPostalCode.text.toString().trim()
            val country = b.etCountry.text.toString().trim()

            b.etAddress.error = null
            b.etCity.error = null
            b.etPostalCode.error = null
            b.etCountry.error = null

            when {
                address.isEmpty() -> {
                    b.etAddress.error = "Champ obligatoire"
                }

                city.isEmpty() -> {
                    b.etCity.error = "Champ obligatoire"
                }

                postalCode.isEmpty() -> {
                    b.etPostalCode.error = "Champ obligatoire"
                }

                country.isEmpty() -> {
                    b.etCountry.error = "Champ obligatoire"
                }

                else -> {

                    val request = AddressRequest(
                        address = address,
                        city = city,
                        postal_code = postalCode,
                        country = country
                    )

                    if (isEditing) {
                        addressViewModel.updateAddress(token, request)
                    } else {
                        addressViewModel.addAddress(token, request)
                    }
                }
            }
        }
    }

    // Historique des commandes
    private fun showOrdersDialog() {

        val token = session.getToken()

        if (token.isNullOrEmpty()) {
            toast("Session expirée")
            return
        }

        val b = ActivityOrdersBinding.inflate(layoutInflater)

        ordersBinding = b

        orderAdapter = OrderAdapter { order ->
            showOrderDetailDialog(order)
        }

        b.rvOrders.layoutManager =
            LinearLayoutManager(requireContext())

        b.rvOrders.adapter = orderAdapter

        ordersDialog = showDialog(b.root, b.btnBack)

        ordersDialog?.setOnDismissListener {
            ordersBinding = null
            orderAdapter = null
            ordersDialog = null
        }

        orderViewModel.loadOrders(token)
    }

    // Affichage du statut avec icône et couleur
    private fun showOrderStatus(
        b: ActivityOrderDetailBinding,
        status: String
    ) {

        val (label, icon, color) = when (status) {

            "PENDING" -> Triple(
                "En attente",
                "⏳",
                R.color.orange
            )

            "PREPARING" -> Triple(
                "En préparation",
                "📦",
                R.color.blue
            )

            "SHIPPED" -> Triple(
                "Expédiée",
                "🚚",
                R.color.purple
            )

            "DELIVERED" -> Triple(
                "Livrée",
                "✓",
                R.color.green
            )

            "CANCELLED" -> Triple(
                "Annulée",
                "✕",
                R.color.red_primary
            )

            else -> Triple(
                status,
                "•",
                R.color.text_secondary
            )
        }

        b.tvDetailStatus.text = "$icon $label"

        b.tvDetailStatus.setTextColor(
            ContextCompat.getColor(requireContext(), color)
        )
    }

    // Détail d'une commande
    private fun showOrderDetailDialog(order: Order) {

        val b = ActivityOrderDetailBinding.inflate(layoutInflater)

        b.tvTitleDetail.text = "Commande #${order.id}"
        b.tvDetailDate.text = formatDate(order.created_at)

        showOrderStatus(b, order.status)

        b.tvDetailPayment.text = order.payment_method
        b.tvDetailTotal.text = "${order.total_amount} DT"

        b.rvOrderProducts.layoutManager =
            LinearLayoutManager(requireContext())

        b.rvOrderProducts.adapter =
            OrderProductAdapter(order.items)

        orderDetailDialog = showDialog(b.root, b.btnBack)
    }

    override fun onDestroyView() {

        personalInfoDialog?.dismiss()
        addressesDialog?.dismiss()
        addAddressDialog?.dismiss()
        ordersDialog?.dismiss()
        orderDetailDialog?.dismiss()

        personalInfoDialog = null
        addressesDialog = null
        addAddressDialog = null
        ordersDialog = null
        orderDetailDialog = null

        addressBinding = null
        addressAdapter = null

        ordersBinding = null
        orderAdapter = null

        _binding = null

        super.onDestroyView()
    }
}
