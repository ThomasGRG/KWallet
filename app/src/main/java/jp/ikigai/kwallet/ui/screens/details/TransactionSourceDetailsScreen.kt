package jp.ikigai.kwallet.ui.screens.details

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.DetailsScreenRoundedBottomBar
import jp.ikigai.kwallet.ui.components.DetailsScreenTopAppBar
import jp.ikigai.kwallet.ui.components.EmptyScreenPlaceholder
import jp.ikigai.kwallet.ui.components.TotalTransactionInfoRow
import jp.ikigai.kwallet.ui.components.TransactionCard
import jp.ikigai.kwallet.ui.components.TransactionHeader
import jp.ikigai.kwallet.ui.components.YearMonthFilterBottomSheet
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionSourceDetailsScreenState
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionSourceDetailsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionSourceDetailsScreen(
    navigateBack: () -> Unit,
    editTransactionSource: () -> Unit,
    deleteTransactionSource: () -> Unit,
    setYearMonth: (YearMonth) -> Unit,
    screenState: TransactionSourceDetailsScreenState
) {
    val scope = rememberCoroutineScope()

    val yearMonthSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val haptics = LocalHapticFeedback.current

    val loading by remember(key1 = screenState.loading) { mutableStateOf(screenState.loading) }

    val (yearMonthExpanded, setYearMonthExpanded) = remember { mutableStateOf(false) }

    val yearMonth by remember(key1 = screenState.selectedYearMonth) {
        mutableStateOf(
            screenState.selectedYearMonth
        )
    }

    if (yearMonthExpanded) {
        YearMonthFilterBottomSheet(
            sheetState = yearMonthSheetState,
            dismiss = {
                scope.launch {
                    yearMonthSheetState.hide()
                    setYearMonthExpanded(false)
                }
            },
            filter = setYearMonth,
            selectedYearMonth = yearMonth
        )
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        topBar = {
            DetailsScreenTopAppBar(
                title = screenState.transactionSource.name,
                iconName = screenState.transactionSource.iconName,
                type = Type.SOURCE
            )
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { setYearMonthExpanded(!loading) },
                        label = {
                            Text(text = "${yearMonth.month.name}, ${yearMonth.year}")
                        }
                    )
                }
                if (loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                DetailsScreenRoundedBottomBar(
                    editClick = editTransactionSource,
                    editIconDescription = "edit transaction source",
                    deleteClick = deleteTransactionSource,
                    deleteIconDescription = "delete transaction source",
                    navigateBack = navigateBack
                )
            }
        },
    ) { contentPadding ->
        if (screenState.transactions.isEmpty()) {
            EmptyScreenPlaceholder(
                content = stringResource(id = R.string.empty_transactions_label),
                contentPadding = contentPadding
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item(
                        key = "baseFlowRow"
                    ) {
                        TotalTransactionInfoRow(
                            currency = screenState.transactionSource.currency,
                            expenses = screenState.expenses,
                            expensesCount = screenState.expensesTransactionCount,
                            income = screenState.income,
                            incomeCount = screenState.incomeTransactionCount
                        )
                    }
                    screenState.transactions.forEach {
                        stickyHeader(
                            key = "date-${it.key}"
                        ) {
                            TransactionHeader(
                                date = it.key,
                                amount = it.value.credit - it.value.debit,
                                currency = screenState.transactionSource.currency,
                            )
                        }
                        itemsIndexed(
                            items = it.value.transactionDetails,
                            key = { _, transactionDetailsWithIcon -> "transaction-${transactionDetailsWithIcon.id}" }
                        ) { _, transactionDetailsWithIcon ->
                            TransactionCard(
                                transactionDetailsWithIcon = transactionDetailsWithIcon,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onLongClick = {}
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
fun TransactionSourceDetailsScreenPreview() {
    TransactionSourceDetailsScreen(
        navigateBack = {},
        editTransactionSource = {},
        deleteTransactionSource = {},
        setYearMonth = {},
        screenState = TransactionSourceDetailsScreenState()
    )
}

fun NavGraphBuilder.addTransactionSourceDetailsScreen(navController: NavController) {
    composable(
        route = Routes.TransactionSourceDetails.route,
        arguments = listOf(
            navArgument("id") {
                defaultValue = -1L
                type = NavType.LongType
            }
        ),
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
        val viewModel: TransactionSourceDetailsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        TransactionSourceDetailsScreen(
            navigateBack = {
                navController.popBackStack()
            },
            editTransactionSource = {
                navController.navigate(Routes.UpsertTransactionSource.getRoute(viewModel.transactionSourceId)) {
                    launchSingleTop = true
                }
            },
            deleteTransactionSource = {},
            setYearMonth = viewModel::setYearMonth,
            screenState = state
        )
    }
}