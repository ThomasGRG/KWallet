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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navigateBack: () -> Unit,
    navigateToMethodsScreen: () -> Unit,
    navigateToNaturesScreen: () -> Unit,
    navigateToSourcesScreen: () -> Unit,
    navigateToTypesScreen: () -> Unit,
    navigateToCategoriesScreen: () -> Unit,
    navigateToCounterPartyScreen: () -> Unit,
    navigateToCurrencyScreen: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

    val items = listOf(
        stringResource(id = R.string.transaction_methods_label) to navigateToMethodsScreen,
        stringResource(id = R.string.transaction_natures_label) to navigateToNaturesScreen,
        stringResource(id = R.string.transaction_sources_label) to navigateToSourcesScreen,
        stringResource(id = R.string.transaction_types_label) to navigateToTypesScreen,
        stringResource(id = R.string.categories_label) to navigateToCategoriesScreen,
        stringResource(id = R.string.counter_parties_label) to navigateToCounterPartyScreen,
        stringResource(id = R.string.currency_label) to navigateToCurrencyScreen,
    )

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.more_screen_label),
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
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items.forEach {
                    ElevatedCard(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            it.second()
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.first,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MoreScreenPreview() {
    MoreScreen(
        navigateBack = {},
        navigateToCategoriesScreen = {},
        navigateToCounterPartyScreen = {},
        navigateToCurrencyScreen = {},
        navigateToMethodsScreen = {},
        navigateToNaturesScreen = {},
        navigateToSourcesScreen = {},
        navigateToTypesScreen = {}
    )
}

fun NavGraphBuilder.addMoreScreen(navController: NavController) {
    composable(
        Routes.More.route,
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
        MoreScreen(
            navigateBack = {
                navController.popBackStack()
            },
            navigateToCategoriesScreen = {
                navController.navigate(Routes.Category.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToCounterPartyScreen = {
                navController.navigate(Routes.CounterParty.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToCurrencyScreen = {
                navController.navigate(Routes.Currency.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToMethodsScreen = {
                navController.navigate(Routes.TransactionMethods.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToNaturesScreen = {
                navController.navigate(Routes.TransactionNatures.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToSourcesScreen = {
                navController.navigate(Routes.TransactionSources.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToTypesScreen = {
                navController.navigate(Routes.TransactionTypes.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}