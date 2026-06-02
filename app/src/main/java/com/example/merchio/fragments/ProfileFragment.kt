package com.example.merchio.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.merchio.CustomerServiceActivity
import com.example.merchio.LoginActivity
import com.example.merchio.PaymentMethodActivity
import com.example.merchio.PersonalInformationActivity
import com.example.merchio.PurchaseHistoryActivity
import com.example.merchio.R
import com.example.merchio.SettingsActivity
import com.example.merchio.data.repository.AuthRepository
import com.example.merchio.data.repository.OrderRepository
import com.example.merchio.utils.loadMerchioImage
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val authRepository = AuthRepository()
    private val orderRepository = OrderRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.menu_purchase_history).setOnClickListener { startActivity(Intent(requireContext(), PurchaseHistoryActivity::class.java)) }
        view.findViewById<View>(R.id.menu_payment_method).setOnClickListener { startActivity(Intent(requireContext(), PaymentMethodActivity::class.java)) }
        view.findViewById<View>(R.id.menu_setting).setOnClickListener { startActivity(Intent(requireContext(), SettingsActivity::class.java)) }
        view.findViewById<View>(R.id.menu_customer_service).setOnClickListener { startActivity(Intent(requireContext(), CustomerServiceActivity::class.java)) }
        view.findViewById<ImageView>(R.id.img_avatar).setOnClickListener { startActivity(Intent(requireContext(), PersonalInformationActivity::class.java)) }
        loadProfile(view)
    }

    override fun onResume() { super.onResume(); view?.let { loadProfile(it) } }

    private fun loadProfile(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                val profile = authRepository.currentProfileOrNull(retry = true)
                val counts = orderRepository.summaryCounts()
                profile to counts
            }.onSuccess { (profile, counts) ->
                view.findViewById<TextView>(R.id.tv_name).text = profile?.displayName ?: "Merchio User"
                view.findViewById<TextView>(R.id.tv_username).text = profile?.email ?: ""
                view.findViewById<ImageView>(R.id.img_avatar).loadMerchioImage(profile?.avatarUrl)
                view.findViewById<TextView>(R.id.tv_packing_count).text = counts.first.toString()
                view.findViewById<TextView>(R.id.tv_shipping_count).text = counts.second.toString()
                view.findViewById<TextView>(R.id.tv_delivered_count).text = counts.third.toString()
            }.onFailure { requireContext().toast(it.message ?: "Gagal load profile") }
        }
    }
}
