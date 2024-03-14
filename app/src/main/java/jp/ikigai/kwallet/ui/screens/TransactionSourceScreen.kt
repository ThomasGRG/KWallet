package jp.ikigai.kwallet.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.EmptyScreenPlaceholder
import jp.ikigai.kwallet.ui.components.TransactionSourceCard
import jp.ikigai.kwallet.ui.viewmodels.TransactionSourcesScreenState
import jp.ikigai.kwallet.ui.viewmodels.TransactionSourcesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSourcesScreen(
    navigateBack: () -> Unit,
    addNewTransactionSource: () -> Unit,
    viewTransactionSourceDetails: (Long) -> Unit,
    editTransactionSource: (Long) -> Unit,
    screenState: TransactionSourcesScreenState
) {
    val haptics = LocalHapticFeedback.current

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sources_screen_label),
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                addNewTransactionSource()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "add new source"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
    ) { contentPadding ->
        if (screenState.transactionSources.isEmpty()) {
            EmptyScreenPlaceholder(
                content = stringResource(id = R.string.empty_sources_label),
                contentPadding = contentPadding
            )
        } else {
            Column(
                modifier = Modifier.padding(contentPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = screenState.transactionSources,
                        key = { _, transactionSource -> "transactionSource-${transactionSource.id}" }
                    ) { _, transactionSource ->
                        TransactionSourceCard(
                            title = transactionSource.name,
                            currency = transactionSource.currency,
                            balance = transactionSource.balance,
                            icon = transactionSource.iconName,
                            frequency = transactionSource.frequency,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewTransactionSourceDetails(transactionSource.id)
                            },
                            onEditClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                editTransactionSource(transactionSource.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TransactionSourcesScreenPreview() {
    TransactionSourcesScreen(
        navigateBack = {},
        addNewTransactionSource = {},
        viewTransactionSourceDetails = {},
        editTransactionSource = {},
        screenState = TransactionSourcesScreenState()
    )
}

fun NavGraphBuilder.addTransactionSourceScreen(navController: NavController) {
    composable(
        Routes.TransactionSources.route,
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
        val viewModel: TransactionSourcesViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        TransactionSourcesScreen(
            navigateBack = {
                navController.popBackStack()
            },
            addNewTransactionSource = {
                navController.navigate(Routes.UpsertTransactionSource.getRoute()) {
                    launchSingleTop = true
                }
            },
            viewTransactionSourceDetails = { id ->
                navController.navigate(Routes.TransactionSourceDetails.getRoute(id)) {
                    launchSingleTop = true
                }
            },
            editTransactionSource = { id ->
                navController.navigate(Routes.UpsertTransactionSource.getRoute(id)) {
                    launchSingleTop = true
                }
            },
            screenState = state
        )
    }
}