package jp.ikigai.kwallet.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.CurrencyCard
import jp.ikigai.kwallet.ui.components.OneHandedModePullDownBox
import jp.ikigai.kwallet.ui.viewmodels.CurrencyScreenState
import jp.ikigai.kwallet.ui.viewmodels.CurrencyViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    navigateBack: () -> Unit,
    screenState: CurrencyScreenState
) {
    val haptics = LocalHapticFeedback.current

    val listState = rememberLazyListState()

    val atTop by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset == 0 }
    }

    var expand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = expand) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        if (expand) {
            delay(5000L)
            expand = false
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (expand && available.y < 0) {
                    expand = false
                    return available
                }
                return super.onPreScroll(available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (atTop && available.y >= 0) {
                    expand = true
                    return available
                }
                return super.onPreFling(available)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.currencies_screen_label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            navigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "go back"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(2f))
            }
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .padding(contentPadding)
        ) {
            OneHandedModePullDownBox(
                expand = expand,
                setExpand = { expand = it }
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = screenState.currencyList,
                    key = { _, currency -> "currency-${currency.countryCode}" }
                ) { _, currency ->
                    CurrencyCard(currency = currency)
                }
            }
        }
    }
}

@Preview
@Composable
fun CurrencyScreenPreview() {
    CurrencyScreen(
        navigateBack = {},
        screenState = CurrencyScreenState(),
    )
}

fun NavGraphBuilder.addCurrencyScreen(navController: NavController) {
    composable(
        Routes.Currency.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(Constants.tweenDuration)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(Constants.tweenDuration)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(Constants.tweenDuration)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(Constants.tweenDuration)
            )
        }
    ) {
        val viewModel: CurrencyViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        CurrencyScreen(
            navigateBack = {
                navController.popBackStack()
            },
            screenState = state,
        )
    }
}