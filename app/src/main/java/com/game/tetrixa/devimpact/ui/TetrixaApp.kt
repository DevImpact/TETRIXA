package com.game.tetrixa.devimpact.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.tetrixa.devimpact.R
import com.game.tetrixa.devimpact.domain.model.ShopItemType
import com.game.tetrixa.devimpact.presentation.TetrixaUiState
import com.game.tetrixa.devimpact.presentation.TetrixaViewModel

private val PalaceMidnight = Color(0xFF101738)
private val PalaceDeepNavy = Color(0xFF071027)
private val PalaceRoyalBlue = Color(0xFF233C88)
private val PalaceViolet = Color(0xFF6C4BCE)
private val PalaceGold = Color(0xFFFFD86B)
private val PalaceGoldDeep = Color(0xFFC58A22)
private val PalaceEmerald = Color(0xFF2FE6B8)
private val PalaceRuby = Color(0xFFFF4F7B)
private val PalaceMarble = Color(0xFFFFF8E6)
private val PalaceInk = Color(0xFF20133B)

@Composable
fun TetrixaApp(
    viewModel: TetrixaViewModel,
    activity: Activity,
    showInterstitialBeforeNewGame: (() -> Unit) -> Unit,
    showInterstitialBeforeNextLevel: (Int, () -> Unit) -> Unit,
    onGameOver: (score: Int, level: Int, lines: Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.game.isGameOver) {
        if (state.game.isGameOver) {
            onGameOver(state.game.score, state.game.level, state.game.clearedLines)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PalaceBackdropBrush())
    ) {
        PalaceAmbientEffects()
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                PalaceNavigationBar(state, viewModel, activity)
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (state.currentTab == 0) {
                    GameScreen(state, viewModel, showInterstitialBeforeNewGame)
                } else {
                    ShopScreen(state, viewModel, activity)
                }

                if (state.isLevelSuccess) {
                    LevelSuccessOverlay(
                        level = state.successLevel,
                        onContinue = {
                            showInterstitialBeforeNextLevel(
                                state.successLevel,
                                viewModel::continueToNextLevel
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PalaceBackdropBrush(): Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF17275E),
        PalaceMidnight,
        PalaceDeepNavy
    )
)

@Composable
private fun PalaceAmbientEffects() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = PalaceGold.copy(alpha = 0.16f),
            radius = size.minDimension * 0.42f,
            center = Offset(size.width * 0.15f, size.height * 0.08f)
        )
        drawCircle(
            color = PalaceViolet.copy(alpha = 0.22f),
            radius = size.minDimension * 0.48f,
            center = Offset(size.width * 0.92f, size.height * 0.25f)
        )
        drawCircle(
            color = PalaceEmerald.copy(alpha = 0.13f),
            radius = size.minDimension * 0.36f,
            center = Offset(size.width * 0.38f, size.height * 0.88f)
        )
        repeat(28) { index ->
            val x = ((index * 37) % 100) / 100f * size.width
            val y = ((index * 61) % 100) / 100f * size.height
            val radius = 1.6f + (index % 4) * 0.8f
            drawCircle(
                color = Color.White.copy(alpha = 0.18f + (index % 3) * 0.05f),
                radius = radius,
                center = Offset(x, y)
            )
            if (index % 5 == 0) {
                drawCircle(
                    color = PalaceGold.copy(alpha = 0.24f),
                    radius = radius * 3f,
                    center = Offset(x, y),
                    style = Stroke(width = 1f)
                )
            }
        }
    }
}

@Composable
private fun PalaceNavigationBar(
    state: TetrixaUiState,
    viewModel: TetrixaViewModel,
    activity: Activity
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
            .shadow(18.dp, RoundedCornerShape(28.dp)),
        color = PalaceDeepNavy.copy(alpha = 0.96f),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PalaceGold.copy(alpha = 0.7f))
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
            NavigationBarItem(
                selected = state.currentTab == 0,
                onClick = { viewModel.changeTab(0) },
                label = { Text(stringResource(R.string.tab_game), fontWeight = FontWeight.Bold) },
                icon = { Text(stringResource(R.string.icon_tab_game), fontSize = 22.sp) }
            )
            NavigationBarItem(
                selected = state.currentTab == 1,
                onClick = { viewModel.changeTab(1) },
                label = { Text(stringResource(R.string.tab_shop), fontWeight = FontWeight.Bold) },
                icon = { Text(stringResource(R.string.icon_tab_shop), fontSize = 22.sp) }
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    viewModel.openExternalGameScreen()
                    activity.startActivity(Intent(activity, com.game.tetrixa.devimpact.settings.SettingsActivity::class.java))
                },
                label = { Text(stringResource(R.string.tab_settings), fontWeight = FontWeight.Bold) },
                icon = { Text(stringResource(R.string.icon_tab_settings), fontSize = 22.sp) }
            )
        }
    }
}

@Composable
private fun GameSidePanel(state: TetrixaUiState) {
    PalacePanel(
        modifier = Modifier.width(118.dp),
        contentPadding = 8
    ) {
        Text(
            stringResource(R.string.game_next_piece),
            color = PalaceMarble,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        val rotation = state.game.nextPiece.rotations[0]
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(14.dp))
                .background(PalaceDeepNavy.copy(alpha = 0.74f))
                .border(1.dp, PalaceGold.copy(alpha = 0.38f), RoundedCornerShape(14.dp))
                .padding(8.dp)
        ) {
            for (y in 0 until 4) {
                Row {
                    for (x in 0 until 4) {
                        val isFilled = rotation.any { it.x == x && it.y == y }
                        PalaceMiniCell(if (isFilled) Color(state.game.nextPiece.color) else Color.Transparent)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CompactStatLine(stringResource(R.string.game_level_format, state.game.level))
        CompactStatLine(stringResource(R.string.game_score_format, state.game.score))
        CompactStatLine(stringResource(R.string.game_lines_format, state.game.clearedLines))
        CompactStatLine(stringResource(R.string.game_coins_format, state.wallet.coins), PalaceGold)
    }
}

@Composable
private fun CompactStatLine(text: String, accent: Color = PalaceMarble) {
    Text(
        text = text,
        color = accent,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PalaceDeepNavy.copy(alpha = 0.54f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    )
}

@Composable
private fun GameScreen(
    state: TetrixaUiState,
    viewModel: TetrixaViewModel,
    showInterstitialBeforeNewGame: (() -> Unit) -> Unit
) {
    val activeCells = state.game.activePiece?.cells()?.associateBy { it.x to it.y } ?: emptyMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        PalaceHeroHeader()
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            PalaceBoardFrame {
                state.game.grid.forEachIndexed { y, row ->
                    Row {
                        row.forEachIndexed { x, color ->
                            val activeColor = activeCells[x to y]?.let { state.game.activePiece?.type?.color }
                            PalaceBoardCell(activeColor ?: color)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            GameSidePanel(state)
        }

        if (state.game.isPaused) {
            PalaceStateBanner(
                title = stringResource(R.string.game_paused),
                subtitle = stringResource(R.string.game_paused_subtitle),
                accent = PalaceEmerald
            )
        }

        PalacePanel(modifier = Modifier.fillMaxWidth(), contentPadding = 10) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PalaceActionButton(stringResource(R.string.control_move_left), stringResource(R.string.acc_move_left), Modifier.weight(1f), viewModel::moveLeft)
                PalaceActionButton(stringResource(R.string.control_rotate), stringResource(R.string.acc_rotate), Modifier.weight(1f), viewModel::rotate)
                PalaceActionButton(stringResource(R.string.control_move_right), stringResource(R.string.acc_move_right), Modifier.weight(1f), viewModel::moveRight)
                PalaceActionButton(stringResource(R.string.control_soft_drop), stringResource(R.string.acc_soft_drop), Modifier.weight(1f), viewModel::softDrop)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PalaceIconControlButton(
                    iconRes = R.drawable.ic_slow_fall,
                    contentDescription = stringResource(R.string.power_up_use_slow),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.useItem(ShopItemType.SLOW_FALL) }
                )
                PalaceIconControlButton(
                    iconRes = R.drawable.ic_explosion,
                    contentDescription = stringResource(R.string.power_up_use_explosion),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.useItem(ShopItemType.EXPLOSION) }
                )
                PalaceIconControlButton(
                    iconRes = if (state.game.isPaused) R.drawable.ic_play else R.drawable.ic_pause,
                    contentDescription = if (state.game.isPaused) stringResource(R.string.game_resume) else stringResource(R.string.game_pause),
                    modifier = Modifier.weight(1f),
                    enabled = !state.game.isGameOver,
                    onClick = viewModel::togglePause
                )
                PalaceIconControlButton(
                    iconRes = R.drawable.ic_restart,
                    contentDescription = stringResource(R.string.game_restart),
                    modifier = Modifier.weight(1f),
                    onClick = { showInterstitialBeforeNewGame(viewModel::startNewGame) }
                )
                PalaceIconControlButton(
                    iconRes = R.drawable.ic_quit,
                    contentDescription = stringResource(R.string.game_quit),
                    modifier = Modifier.weight(1f),
                    enabled = !state.game.isGameOver,
                    onClick = viewModel::quitGame
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}


@Composable
private fun PalaceHeroHeader() {
    PalacePanel(modifier = Modifier.fillMaxWidth(), contentPadding = 10) {
        Text(
            stringResource(R.string.game_title),
            color = PalaceGold,
            fontSize = 26.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PalaceStatChip(icon: String, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(PalaceRoyalBlue.copy(alpha = 0.92f), PalaceViolet.copy(alpha = 0.86f))))
            .border(1.dp, PalaceGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(icon, fontSize = 16.sp)
        Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PalaceBoardFrame(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .shadow(26.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.verticalGradient(listOf(PalaceGold, PalaceGoldDeep, Color(0xFF70410F))))
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(21.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF17275A), Color(0xFF080E24))))
                .border(1.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(21.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(PalaceEmerald.copy(alpha = 0.13f), size.minDimension * 0.55f, Offset(size.width * 0.15f, size.height * 0.2f))
                drawCircle(PalaceGold.copy(alpha = 0.10f), size.minDimension * 0.5f, Offset(size.width * 0.85f, size.height * 0.75f))
            }
            Column(content = content)
        }
    }
}


@Composable
private fun PalaceBoardCell(color: Long?) {
    val gemColor = color?.let { Color(it) }
    val cellShape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .size(15.dp)
            .padding(0.7.dp)
            .shadow(if (gemColor != null) 3.dp else 0.dp, cellShape)
            .clip(cellShape)
            .background(
                if (gemColor != null) {
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.36f),
                            gemColor.copy(alpha = 0.98f),
                            PalaceDeepNavy.copy(alpha = 0.16f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF253B77).copy(alpha = 0.9f),
                            Color(0xFF101936).copy(alpha = 0.96f)
                        )
                    )
                }
            )
            .border(
                width = if (gemColor != null) 0.7.dp else 0.45.dp,
                color = if (gemColor != null) Color.White.copy(alpha = 0.42f) else PalaceGold.copy(alpha = 0.18f),
                shape = cellShape
            )
    )
}

@Composable
private fun PalaceMiniCell(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .padding(1.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (color == Color.Transparent) Color.Transparent else color)
            .border(0.5.dp, if (color == Color.Transparent) Color.Transparent else Color.White.copy(alpha = 0.42f), RoundedCornerShape(3.dp))
    )
}

@Composable
private fun LevelSuccessOverlay(
    level: Int,
    onContinue: () -> Unit
) {
    var continueRequested by remember(level) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PalaceDeepNavy.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        PalaceAmbientEffects()
        PalacePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentPadding = 24
        ) {
            Text(stringResource(R.string.icon_level_complete), fontSize = 58.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                stringResource(R.string.level_completed),
                color = PalaceGold,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                stringResource(R.string.level_success_format, level),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                stringResource(R.string.level_prepare_next),
                color = PalaceMarble.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                repeat(5) { Text(stringResource(R.string.icon_level_star), color = PalaceGold, fontSize = 22.sp, modifier = Modifier.padding(horizontal = 5.dp)) }
            }
            Spacer(modifier = Modifier.height(22.dp))
            PalaceButton(
                text = stringResource(R.string.level_continue),
                onClick = {
                    if (!continueRequested) {
                        continueRequested = true
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !continueRequested
            )
        }
    }
}

@Composable
private fun ShopScreen(
    state: TetrixaUiState,
    viewModel: TetrixaViewModel,
    activity: Activity
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(14.dp)) }
        item {
            PalacePanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.shop_hero_title),
                    color = PalaceGold,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    stringResource(R.string.shop_title),
                    color = PalaceMarble.copy(alpha = 0.84f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                PalaceStatChip(stringResource(R.string.icon_stat_coins), stringResource(R.string.game_coins_format, state.wallet.coins), Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                PalaceButton(
                    text = stringResource(R.string.open_reward_shop),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.openExternalGameScreen()
                        activity.startActivity(Intent(activity, com.game.tetrixa.devimpact.store.StoreActivity::class.java))
                    }
                )
            }
        }
        items(viewModel.shopItems) { item ->
            PalaceShopItemCard(state, viewModel, item.type, item.price)
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
private fun PalaceShopItemCard(
    state: TetrixaUiState,
    viewModel: TetrixaViewModel,
    type: ShopItemType,
    price: Int
) {
    val title = when (type) {
        ShopItemType.SLOW_FALL -> stringResource(R.string.power_up_slow_fall_name)
        ShopItemType.EXPLOSION -> stringResource(R.string.power_up_explosion_name)
    }
    val desc = when (type) {
        ShopItemType.SLOW_FALL -> stringResource(R.string.power_up_slow_fall_desc)
        ShopItemType.EXPLOSION -> stringResource(R.string.power_up_explosion_desc)
    }
    val icon = when (type) {
        ShopItemType.SLOW_FALL -> stringResource(R.string.icon_power_slow_fall)
        ShopItemType.EXPLOSION -> stringResource(R.string.icon_power_explosion)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PalaceGold.copy(alpha = 0.64f))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(PalaceMarble, Color(0xFFFFEDC0))))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(PalaceGold, PalaceGoldDeep)))
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = PalaceInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text(desc, color = PalaceInk.copy(alpha = 0.68f), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.shop_owned_format, state.wallet.inventory[type] ?: 0),
                    color = PalaceRoyalBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            PalaceButton(
                text = stringResource(R.string.shop_buy_format, price),
                compact = true,
                onClick = { viewModel.buyItem(type) }
            )
        }
    }
}

@Composable
private fun PalaceStateBanner(title: String, subtitle: String, accent: Color) {
    PalacePanel(modifier = Modifier.fillMaxWidth(), contentPadding = 18) {
        Text(
            title,
            color = accent,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            subtitle,
            color = PalaceMarble.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PalacePanel(
    modifier: Modifier = Modifier,
    contentPadding: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(22.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(PalaceGold.copy(alpha = 0.95f), PalaceGoldDeep.copy(alpha = 0.9f))))
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.verticalGradient(listOf(Color(0xEE263D83), Color(0xF0121838))))
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(26.dp))
                .padding(contentPadding.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun PalaceButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.shadow(if (enabled) 10.dp else 0.dp, RoundedCornerShape(if (compact) 16.dp else 22.dp)),
        shape = RoundedCornerShape(if (compact) 16.dp else 22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PalaceGold,
            contentColor = PalaceInk,
            disabledContainerColor = Color(0xFF536079),
            disabledContentColor = Color.White.copy(alpha = 0.55f)
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, fontSize = if (compact) 12.sp else 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PalaceIconControlButton(
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .shadow(if (enabled) 8.dp else 0.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    if (enabled) {
                        listOf(PalaceGold, PalaceGoldDeep)
                    } else {
                        listOf(Color(0xFF536079), Color(0xFF30384D))
                    }
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .semantics { this.contentDescription = contentDescription }
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (enabled) PalaceInk else Color.White.copy(alpha = 0.55f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PalaceActionButton(text: String, contentDescription: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(PalaceEmerald, Color(0xFF128D86))))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    }
}
