package com.game.tetrixa.devimpact.store

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.game.tetrixa.devimpact.R
import com.google.android.material.button.MaterialButton

class StoreAdapter(
    private val items: List<StoreItem>,
    private val purchaseCooldownRemainingMillis: (StoreItem) -> Long,
    private val onBuyClick: (StoreItem, MaterialButton) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): StoreItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store_pack, parent, false)
        bind(view, getItem(position))
        return view
    }

    fun createView(parent: ViewGroup, item: StoreItem): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store_pack, parent, false)
        bind(view, item)
        return view
    }

    private fun bind(view: View, item: StoreItem) {
        val context = view.context
        val iconText = view.findViewById<TextView>(R.id.iconText)
        val titleText = view.findViewById<TextView>(R.id.titleText)
        val descriptionText = view.findViewById<TextView>(R.id.descriptionText)
        val rewardText = view.findViewById<TextView>(R.id.rewardText)
        val bonusBadge = view.findViewById<TextView>(R.id.bonusBadge)
        val oldPriceText = view.findViewById<TextView>(R.id.oldPriceText)
        val priceText = view.findViewById<TextView>(R.id.priceText)
        val buyButton = view.findViewById<MaterialButton>(R.id.buyButton)

        iconText.setText(item.iconRes)
        titleText.setText(item.titleRes)
        descriptionText.setText(item.descriptionRes)
        rewardText.setText(item.rewardHighlightRes)
        priceText.text = context.getString(R.string.price_format, item.priceCoins)

        if (item.oldPriceCoins != null) {
            oldPriceText.visibility = View.VISIBLE
            oldPriceText.text = context.getString(R.string.old_price_format, item.oldPriceCoins)
            oldPriceText.paintFlags = oldPriceText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            oldPriceText.visibility = View.GONE
        }

        when {
            item.bonusPercent != null && item.badgeRes != null -> {
                bonusBadge.visibility = View.VISIBLE
                bonusBadge.text = context.getString(
                    R.string.badge_with_bonus_format,
                    context.getString(item.badgeRes),
                    context.getString(R.string.bonus_badge_format, item.bonusPercent)
                )
            }
            item.bonusPercent != null -> {
                bonusBadge.visibility = View.VISIBLE
                bonusBadge.text = context.getString(R.string.bonus_badge_format, item.bonusPercent)
            }
            item.badgeRes != null -> {
                bonusBadge.visibility = View.VISIBLE
                bonusBadge.setText(item.badgeRes)
            }
            else -> bonusBadge.visibility = View.GONE
        }

        val cooldownRemaining = purchaseCooldownRemainingMillis(item)
        if (cooldownRemaining > 0L) {
            buyButton.isEnabled = false
            buyButton.text = context.getString(
                R.string.buy_available_in_format,
                StoreCooldownFormatter.formatRemaining(cooldownRemaining)
            )
        } else {
            buyButton.isEnabled = true
            buyButton.text = context.getString(R.string.buy_now)
        }
        buyButton.setOnClickListener { onBuyClick(item, buyButton) }
    }

}
