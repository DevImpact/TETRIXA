package com.game.tetrixa.devimpact.store

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.game.tetrixa.devimpact.R
import com.google.android.material.button.MaterialButton

class StoreActivity : Activity() {
    private lateinit var purchaseManager: PurchaseManager
    private lateinit var coinCounter: TextView
    private lateinit var sectionsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        purchaseManager = PurchaseManager(this)
        findViewById<MaterialButton>(R.id.storeBackButton).setOnClickListener { finish() }
        coinCounter = findViewById(R.id.coinCounter)
        sectionsContainer = findViewById(R.id.storeSections)

        updateCoinCounter(animateFrom = purchaseManager.coins, animateTo = purchaseManager.coins)
        renderStore()
    }

    override fun onResume() {
        super.onResume()
        if (::purchaseManager.isInitialized && ::sectionsContainer.isInitialized) {
            renderStore()
        }
    }

    private fun renderStore() {
        sectionsContainer.removeAllViews()

        val allItems = StoreRepository.getPacks()
        val adapter = StoreAdapter(
            allItems,
            purchaseManager::purchaseCooldownRemainingMillis,
            ::onBuyClicked
        )
        StoreSection.values().forEach { section ->
            val sectionItems = allItems.filter { it.section == section }
            if (sectionItems.isEmpty()) return@forEach

            sectionsContainer.addView(createSectionHeader(getString(section.titleRes)))
            sectionItems.forEach { item ->
                sectionsContainer.addView(adapter.createView(sectionsContainer, item))
            }
        }
    }

    private fun createSectionHeader(title: String): TextView = TextView(this).apply {
        text = title
        setTextAppearance(R.style.TextAppearance_Game_Store_Section)
        setPadding(6.dp(), 24.dp(), 6.dp(), 4.dp())
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun onBuyClicked(item: StoreItem, button: MaterialButton) {
        animateBuyButton(button)
        performHapticFeedback()

        val previousBalance = purchaseManager.coins
        when (val result = purchaseManager.purchase(item)) {
            is PurchaseResult.Success -> {
                updateCoinCounter(previousBalance, result.newBalance)
                renderStore()
                showSuccessDialog(item, result.newBalance)
            }
            is PurchaseResult.Failed -> {
                updateCoinCounter(result.currentBalance, result.currentBalance)
                Toast.makeText(this, R.string.not_enough_coins, Toast.LENGTH_SHORT).show()
            }
            is PurchaseResult.Cooldown -> {
                button.isEnabled = false
                val cooldownText = StoreCooldownFormatter.formatRemaining(result.remainingMillis)
                button.text = getString(R.string.buy_available_in_format, cooldownText)
                renderStore()
                Toast.makeText(
                    this,
                    getString(R.string.purchase_cooldown_message, cooldownText),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun animateBuyButton(button: MaterialButton) {
        button.animate()
            .scaleX(0.92f)
            .scaleY(0.92f)
            .setDuration(80L)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(220L)
                    .start()
            }
            .start()
    }

    private fun updateCoinCounter(animateFrom: Int, animateTo: Int) {
        ValueAnimator.ofInt(animateFrom, animateTo).apply {
            duration = 550L
            interpolator = OvershootInterpolator()
            addUpdateListener { animator ->
                val coins = animator.animatedValue as Int
                coinCounter.text = getString(R.string.coin_balance_format, coins)
            }
            start()
        }
    }

    private fun showSuccessDialog(item: StoreItem, balance: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_purchase_success, null)
        dialogView.findViewById<TextView>(R.id.successMessage).text =
            getString(R.string.purchase_success_message, getString(item.titleRes))
        dialogView.findViewById<TextView>(R.id.successBalance).text =
            getString(R.string.coins_after_purchase_format, balance)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialogView.findViewById<MaterialButton>(R.id.successButton).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun performHapticFeedback() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(24L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(24L)
        }
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()
}
