package jp.ikigai.kwallet.ui.screens.details

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowDownCircle
import compose.icons.tablericons.ArrowUpCircle
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.Constants.DEBIT
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.CurrencyFilterBottomSheet
import jp.ikigai.kwallet.ui.components.DetailsScreenRoundedBottomBar
import jp.ikigai.kwallet.ui.components.DetailsScreenTopAppBar
import jp.ikigai.kwallet.ui.components.EmptyScreenPlaceholder
import jp.ikigai.kwallet.ui.components.TotalTransactionInfoCard
import jp.ikigai.kwallet.ui.components.TransactionCard
import jp.ikigai.kwallet.ui.components.TransactionFilterChipRow
import jp.ikigai.kwallet.ui.components.TransactionHeader
import jp.ikigai.kwallet.ui.components.YearMonthFilterBottomSheet
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionTypeDetailsScreenState
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionTypeDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TransactionTypeDetailsScreen(
    navigateBack: () -> Unit,
    editTransactionType: () -> Unit,
    deleteTransactionType: () -> Unit,
    setCurrency: (String) -> Unit,
    setYearMonth: (YearMonth) -> Unit,
    screenState: TransactionTypeDetailsScreenState
) {
    val configuration = LocalConfiguration.current

    var screenWidth by remember {
        mutableIntStateOf(configuration.screenWidthDp)
    }

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.screenWidthDp }
            .collectLatest { screenWidth = it }
    }

    val scope = rememberCoroutineScope()

    val currencySheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val yearMonthSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val haptics = LocalHapticFeedback.current

    val loading by remember(key1 = screenState.loading) { mutableStateOf(screenState.loading) }

    val transactionType by remember(key1 = screenState.transactionType.baseType) {
        mutableStateOf(screenState.transactionType.baseType)
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
        topBar = {
            DetailsScreenTopAppBar(
                title = screenState.transactionType.type,
                iconName = screenState.transactionType.iconName,
                type = Type.TYPE
            )
        },
        bottomBar = {
            Column {
                TransactionFilterChipRow(
                    currency = currency,
                    setCurrencyExpanded = { setCurrencyExpanded(!loading) },
                    yearMonth = yearMonth,
                    setYearMonthExpanded = { setYearMonthExpanded(!loading) }
                )
                if (loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                DetailsScreenRoundedBottomBar(
                    editClick = editTransactionType,
                    editIconDescription = "edit transaction type",
                    deleteClick = deleteTransactionType,
                    deleteIconDescription = "delete transaction type",
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
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TotalTransactionInfoCard(
                                currency = currency,
                                total = screenState.cashFlow,
                                count = screenState.transactionCount,
                                color = if (transactionType == DEBIT) {
                                    Color(0xFFF44336)
                                } else {
                                    Color(0xFF4CAF50)
                                },
                                maxWidth = screenWidth.toFloat(),
                                icon = if (transactionType == DEBIT) {
                                    TablerIcons.ArrowUpCircle
                                } else {
                                    TablerIcons.ArrowDownCircle
                                }
                            )
                        }
                    }
                    screenState.transactions.forEach {
                        stickyHeader(
                            key = "date-${it.key}"
                        ) {
                            TransactionHeader(
                                date = it.key,
                                amount = if (transactionType == DEBIT) -1 * it.value.cashFlow else it.value.cashFlow,
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
fun TransactionTypeDetailsScreenPreview() {
    TransactionTypeDetailsScreen(
        navigateBack = {},
        editTransactionType = {},
        deleteTransactionType = {},
        setCurrency = {},
        setYearMonth = {},
        screenState = TransactionTypeDetailsScreenState()
    )
}

fun NavGraphBuilder.addTransactionTypeDetailsScreen(navController: NavController) {
    composable(
        route = Routes.TransactionTypeDetails.route,
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
        val viewModel: TransactionTypeDetailsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        TransactionTypeDetailsScreen(
            navigateBack = {
                navController.popBackStack()
            },
            editTransactionType = {
                navController.navigate(Routes.UpsertTransactionType.getRoute(viewModel.transactionTypeId)) {
                    launchSingleTop = true
                }
            },
            deleteTransactionType = {},
            setCurrency = viewModel::setCurrency,
            setYearMonth = viewModel::setYearMonth,
            screenState = state
        )
    }
}