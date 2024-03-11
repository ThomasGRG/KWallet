package jp.ikigai.kwallet.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
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
import jp.ikigai.kwallet.ui.components.CurrencyFilterBottomSheet
import jp.ikigai.kwallet.ui.components.EmptyScreenPlaceholder
import jp.ikigai.kwallet.ui.components.TotalTransactionInfoRow
import jp.ikigai.kwallet.ui.components.TransactionCard
import jp.ikigai.kwallet.ui.components.TransactionCardActionBottomSheet
import jp.ikigai.kwallet.ui.components.TransactionHeader
import jp.ikigai.kwallet.ui.components.YearMonthFilterBottomSheet
import jp.ikigai.kwallet.ui.viewmodels.TransactionScreenState
import jp.ikigai.kwallet.ui.viewmodels.TransactionScreenViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    addTransaction: () -> Unit,
    editTransaction: (Long) -> Unit,
    cloneTransaction: (Long) -> Unit,
    navigateToMoreScreen: () -> Unit,
    setStrings: (List<String>) -> Unit,
    setCurrency: (String) -> Unit,
    setYearMonth: (YearMonth) -> Unit,
    screenState: TransactionScreenState
) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    val statusText by remember {
        mutableStateOf(
            listOf(
                context.getString(R.string.are_required),
                context.getString(R.string.is_required),
                context.getString(R.string.single_category),
                context.getString(R.string.single_counter_party),
                context.getString(R.string.single_transaction_method),
                context.getString(R.string.single_transaction_nature),
                context.getString(R.string.single_transaction_source),
                context.getString(R.string.single_transaction_type),
            )
        )
    }

    LaunchedEffect(Unit) {
        setStrings(statusText)
    }

    val scope = rememberCoroutineScope()

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val currencySheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val actionSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val yearMonthSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val loading by remember(key1 = screenState.loading) { mutableStateOf(screenState.loading) }

    val snackBarText by remember(key1 = screenState.snackBarText) { mutableStateOf(screenState.snackBarText) }

    val addButtonEnabled by remember(key1 = screenState.addButtonEnabled) { mutableStateOf(screenState.addButtonEnabled) }

    var selectedTransactionId by remember {
        mutableLongStateOf(
            0L
        )
    }

    if (selectedTransactionId != 0L) {
        TransactionCardActionBottomSheet(
            sheetState = actionSheetState,
            dismiss = {
                selectedTransactionId = 0L
            },
            clone = {
                cloneTransaction(selectedTransactionId)
                scope.launch {
                    actionSheetState.hide()
                    selectedTransactionId = 0L
                }
            }
        )
    }

    val (currencyExpanded, setCurrencyExpanded) = remember { mutableStateOf(false) }
    val (yearMonthExpanded, setYearMonthExpanded) = remember { mutableStateOf(false) }

    val currency by remember(key1 = screenState.selectedCurrency) {
        mutableStateOf(
            screenState.selectedCurrency
        )
    }

    val yearMonth by remember(key1 = screenState.selectedYearMonth) {
        mutableStateOf(
            screenState.selectedYearMonth
        )
    }

    if (currencyExpanded) {
        CurrencyFilterBottomSheet(
            sheetState = currencySheetState,
            dismiss = {
                scope.launch {
                    currencySheetState.hide()
                    setCurrencyExpanded(false)
                }
            },
            filter = setCurrency,
            selectedCurrency = currency,
            currencies = screenState.currencies
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
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.main_screen_label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
                        onClick = { setCurrencyExpanded(!loading) },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string.currency_filter_label,
                                    currency
                                )
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(6.dp))
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
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "settings"
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
                                    if (addButtonEnabled) {
                                        addTransaction()
                                    } else {
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = snackBarText,
                                                withDismissAction = true
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "add_new_transaction"
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    navigateToMoreScreen()
                                }
                            ) {
                                Icon(imageVector = Icons.Filled.Menu, contentDescription = "more")
                            }
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        if (screenState.transactions.isEmpty()) {
            EmptyScreenPlaceholder(
                content = stringResource(id = R.string.empty_transactions_label),
                contentPadding = contentPadding
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item(
                        key = "totalBalance"
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        ) {
                            Text(
                                text = screenState.balance + " " + screenState.selectedCurrency,
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                    item(
                        key = "baseFlowRow"
                    ) {
                        TotalTransactionInfoRow(
                            currency = currency,
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
                                currency = currency
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
                                    editTransaction(transactionDetailsWithIcon.id)
                                },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedTransactionId = transactionDetailsWithIcon.id
                                }
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
fun TransactionScreenPreview() {
    TransactionScreen(
        addTransaction = {},
        editTransaction = {},
        cloneTransaction = {},
        navigateToMoreScreen = {},
        setStrings = {},
        setYearMonth = {},
        setCurrency = {},
        screenState = TransactionScreenState()
    )
}

fun NavGraphBuilder.addTransactionScreen(navController: NavController) {
    composable(
        Routes.Transactions.route,
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
        val viewModel: TransactionScreenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        TransactionScreen(
            addTransaction = {
                navController.navigate(Routes.UpsertTransaction.getRoute()) {
                    launchSingleTop = true
                }
            },
            editTransaction = { id ->
                navController.navigate(Routes.UpsertTransaction.getRoute(id)) {
                    launchSingleTop = true
                }
            },
            cloneTransaction = viewModel::cloneTransaction,
            navigateToMoreScreen = {
                navController.navigate(Routes.More.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            setStrings = viewModel::setStrings,
            setCurrency = viewModel::setCurrency,
            setYearMonth = viewModel::setYearMonth,
            screenState = state
        )
    }
}