package com.game.tetrixa.devimpact.gameover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.game.tetrixa.devimpact.MainActivity
import com.game.tetrixa.devimpact.databinding.ActivityGameOverBinding
import kotlinx.coroutines.launch

class GameOverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameOverBinding
    private lateinit var viewModel: GameOverViewModel
    private val summary: GameOverSessionSummary by lazy { readSummaryFromIntent() }
    private var isTransitionRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            GameOverViewModelFactory(this)
        )[GameOverViewModel::class.java]
        setContentView(binding.root)

        bindSummary(GameOverUiState.fromSummary(summary))
        bindActions()
    }

    private fun readSummaryFromIntent(): GameOverSessionSummary {
        return GameOverSessionSummary.fromIntentValues(
            score = intent.optionalIntExtra(EXTRA_FINAL_SCORE),
            level = intent.optionalIntExtra(EXTRA_LEVEL),
            lines = intent.optionalIntExtra(EXTRA_LINES)
        )
    }

    private fun bindSummary(uiState: GameOverUiState) {
        binding.tvFinalScore.text = uiState.finalScoreText
        binding.tvFinalLevel.text = uiState.finalLevelText
        binding.tvFinalLines.text = uiState.finalLinesText
    }

    private fun bindActions() {
        binding.btnRetry.setOnClickListener { persistSessionResultThen() }
    }

    private fun persistSessionResultThen() {
        if (isTransitionRunning) return
        isTransitionRunning = true
        setActionsEnabled(false)

        lifecycleScope.launch {
            viewModel.persistSessionResultIfNeeded(summary)
            navigateAfterPersist()
        }
    }

    private fun navigateAfterPersist() {
        startActivity(MainActivity.newGameIntent(this))
        finish()
    }

    private fun setActionsEnabled(enabled: Boolean) {
        binding.btnRetry.isEnabled = enabled
    }

    private fun Intent.optionalIntExtra(name: String): Int? {
        return if (hasExtra(name)) getIntExtra(name, 0) else null
    }


    companion object {
        const val EXTRA_FINAL_SCORE = "FINAL_SCORE"
        const val EXTRA_LEVEL = "LEVEL"
        const val EXTRA_LINES = "LINES"

        fun newIntent(
            context: Context,
            score: Int,
            level: Int,
            lines: Int
        ): Intent {
            return Intent(context, GameOverActivity::class.java).apply {
                putExtra(EXTRA_FINAL_SCORE, score)
                putExtra(EXTRA_LEVEL, level)
                putExtra(EXTRA_LINES, lines)
            }
        }
    }
}
