package com.game.tetrixa.devimpact.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.game.tetrixa.devimpact.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsAdapter(
    private val parent: LinearLayout,
    private val sections: List<SettingsSection>
) {
    fun render() {
        parent.removeAllViews()
        sections.forEachIndexed { index, section ->
            val card = createCard(section)
            parent.addView(card)
            card.alpha = 0f
            card.translationY = 34f
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 90L)
                .setDuration(420L)
                .setInterpolator(OvershootInterpolator(0.8f))
                .start()
        }
    }

    private fun createCard(section: SettingsSection): View {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.item_settings_card, parent, false) as MaterialCardView
        card.findViewById<TextView>(R.id.cardTitle).setText(section.titleRes)
        val rows = card.findViewById<LinearLayout>(R.id.cardRows)
        section.items.forEach { item -> rows.addView(createRow(rows, item)) }
        return card
    }

    private fun createRow(parent: ViewGroup, item: SettingsItem): View {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.item_settings_row, parent, false)
        val icon = row.findViewById<TextView>(R.id.rowIcon)
        val title = row.findViewById<TextView>(R.id.rowTitle)
        val summary = row.findViewById<TextView>(R.id.rowSummary)
        val switch = row.findViewById<SwitchMaterial>(R.id.rowSwitch)
        val chevron = row.findViewById<TextView>(R.id.rowChevron)

        icon.text = item.icon
        when (item) {
            is SettingsItem.Toggle -> {
                title.setText(item.titleRes)
                summary.setText(item.summaryRes)
                switch.visibility = View.VISIBLE
                chevron.visibility = View.GONE
                switch.isChecked = item.isChecked
                row.setOnClickListener { switch.isChecked = !switch.isChecked }
                switch.setOnCheckedChangeListener { button, checked ->
                    button.animate()
                        .scaleX(0.86f)
                        .scaleY(0.86f)
                        .setDuration(70L)
                        .withEndAction {
                            button.animate().scaleX(1f).scaleY(1f).setDuration(240L).setInterpolator(OvershootInterpolator()).start()
                        }.start()
                    item.onChanged(checked)
                }
            }
            is SettingsItem.Action -> {
                title.setText(item.titleRes)
                summary.setText(item.summaryRes)
                switch.visibility = View.GONE
                chevron.visibility = View.VISIBLE
                row.setOnClickListener {
                    animatePress(row)
                    item.onClick()
                }
            }
            is SettingsItem.Info -> {
                title.text = item.title
                summary.text = item.summary
                switch.visibility = View.GONE
                chevron.visibility = View.GONE
                row.isClickable = false
                row.isFocusable = false
            }
        }
        row.contentDescription = parent.context.getString(R.string.acc_settings_row_format, title.text, summary.text)
        return row
    }

    private fun animatePress(view: View) {
        view.animate().scaleX(0.98f).scaleY(0.98f).setDuration(80L).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(160L).setInterpolator(OvershootInterpolator()).start()
        }.start()
    }
}
