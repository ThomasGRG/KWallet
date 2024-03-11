package jp.ikigai.kwallet.ui.screens.upsert

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import compose.icons.TablerIcons
import compose.icons.tablericons.Alarm
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.CurrencyDollar
import compose.icons.tablericons.DeviceFloppy
import compose.icons.tablericons.FileText
import compose.icons.tablericons.Typography
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.enums.UpsertTransactionFormStatus
import jp.ikigai.kwallet.extensions.getIconByType
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.BottomLoadingIndicator
import jp.ikigai.kwallet.ui.components.ConfirmDeleteDialog
import jp.ikigai.kwallet.ui.components.DatePickerDialog
import jp.ikigai.kwallet.ui.components.TimePickerBottomSheet
import jp.ikigai.kwallet.ui.components.UpsertTransactionSelectionBottomSheet
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionScreenState
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UpsertTransactionScreen(
    navigateBack: () -> Unit,
    getListForSheet: (Type) -> List<Triple<Long, String, String>>,
    updateId: (Type, Long) -> Unit,
    setDate: (ZonedDateTime) -> Unit,
    setTitle: (String) -> Unit,
    setDescription: (String) -> Unit,
    setAmount: (String) -> Unit,
    setTime: (Int, Int) -> Unit,
    clearSnackBarText: () -> Unit,
    setStrings: (List<String>) -> Unit,
    upsertTransaction: () -> Unit,
    deleteTransaction: () -> Unit,
    screenState: UpsertTransactionScreenState
) {
    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val gridState = rememberLazyGridState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val focusRequester = remember {
        FocusRequester()
    }

    val loading by remember(key1 = screenState.loading) {
        mutableStateOf(screenState.loading)
    }

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
                context.getString(R.string.insufficient_balance),
                context.getString(R.string.add_transaction_amount_error),
                context.getString(R.string.save_success),
                context.getString(R.string.delete_success)
            )
        )
    }

    LaunchedEffect(Unit) {
        setStrings(statusText)
    }

    LaunchedEffect(loading) {
        if (!loading) {
            focusRequester.requestFocus()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            message = stringResource(id = R.string.delete_transaction_dialog_label),
            dismiss = {
                showDeleteDialog = false
            },
            delete = {
                deleteTransaction()
                showDeleteDialog = false
            }
        )
    }

    var sheetExpanded by remember {
        mutableStateOf(Type.NONE)
    }

    val categoryList by remember(key1 = screenState.categories) {
        mutableStateOf(getListForSheet(Type.CATEGORY))
    }

    val counterPartyList by remember(key1 = screenState.counterParties) {
        mutableStateOf(getListForSheet(Type.COUNTERPARTY))
    }

    val transactionMethodList by remember(key1 = screenState.transactionMethods) {
        mutableStateOf(getListForSheet(Type.METHOD))
    }

    val transactionNatureList by remember(key1 = screenState.transactionNatures) {
        mutableStateOf(getListForSheet(Type.NATURE))
    }

    val transactionSourceList by remember(key1 = screenState.transactionSources) {
        mutableStateOf(getListForSheet(Type.SOURCE))
    }

    val transactionTypeList by remember(key1 = screenState.transactionTypes) {
        mutableStateOf(getListForSheet(Type.TYPE))
    }

    val title by remember(key1 = screenState.transaction.title) {
        mutableStateOf(screenState.transaction.title)
    }

    val description by remember(key1 = screenState.transaction.description) {
        mutableStateOf(screenState.transaction.description)
    }

    val amount by remember(key1 = screenState.displayAmount) {
        mutableStateOf(screenState.displayAmount)
    }

    val formStatus by remember(key1 = screenState.formStatus) {
        mutableStateOf(screenState.formStatus)
    }

    val status by remember(key1 = screenState.status) {
        mutableStateOf(screenState.status)
    }

    val snackBarText by remember(key1 = screenState.snackBarText) {
        mutableStateOf(screenState.snackBarText)
    }

    LaunchedEffect(snackBarText) {
        if (snackBarText != "") {
            scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = snackBarText,
                    withDismissAction = true
                )
                if (result == SnackbarResult.Dismissed) {
                    if (status == UpsertStatus.SUCCESS) {
                        navigateBack()
                    } else {
                        clearSnackBarText()
                    }
                }
            }
        }
    }

    val dateTimeDetails by remember(key1 = screenState.dateTime) {
        mutableStateOf(screenState.dateTime)
    }

    val selectedCategory by remember(key1 = screenState.selectedCategory) {
        mutableStateOf(screenState.selectedCategory)
    }

    val selectedCounterParty by remember(key1 = screenState.selectedCounterParty) {
        mutableStateOf(screenState.selectedCounterParty)
    }

    val selectedTransactionMethod by remember(key1 = screenState.selectedTransactionMethod) {
        mutableStateOf(screenState.selectedTransactionMethod)
    }

    val selectedTransactionNature by remember(key1 = screenState.selectedTransactionNature) {
        mutableStateOf(screenState.selectedTransactionNature)
    }

    val selectedTransactionSource by remember(key1 = screenState.selectedTransactionSource) {
        mutableStateOf(screenState.selectedTransactionSource)
    }

    val selectedTransactionType by remember(key1 = screenState.selectedTransactionType) {
        mutableStateOf(screenState.selectedTransactionType)
    }

    val selectedCategoryIcon by remember(key1 = screenState.selectedCategory) {
        mutableStateOf(screenState.selectedCategory.iconName.getIconByType(Type.CATEGORY))
    }

    val selectedCounterPartyIcon by remember(key1 = screenState.selectedCounterParty) {
        mutableStateOf(screenState.selectedCounterParty.iconName.getIconByType(Type.COUNTERPARTY))
    }

    val selectedTransactionMethodIcon by remember(key1 = screenState.selectedTransactionMethod) {
        mutableStateOf(screenState.selectedTransactionMethod.iconName.getIconByType(Type.METHOD))
    }

    val selectedTransactionNatureIcon by remember(key1 = screenState.selectedTransactionNature) {
        mutableStateOf(screenState.selectedTransactionNature.iconName.getIconByType(Type.NATURE))
    }

    val selectedTransactionSourceIcon by remember(key1 = screenState.selectedTransactionSource) {
        mutableStateOf(screenState.selectedTransactionSource.iconName.getIconByType(Type.SOURCE))
    }

    val selectedTransactionTypeIcon by remember(key1 = screenState.selectedTransactionType) {
        mutableStateOf(screenState.selectedTransactionType.iconName.getIconByType(Type.TYPE))
    }

    when (sheetExpanded) {
        Type.NONE -> {}
        Type.CATEGORY -> {
            UpsertTransactionSelectionBottomSheet(
                data = categoryList,
                selectedId = selectedCategory.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.CATEGORY, id)
                },
                sheetState = sheetState
            )
        }

        Type.COUNTERPARTY -> {
            UpsertTransactionSelectionBottomSheet(
                data = counterPartyList,
                selectedId = selectedCounterParty.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.COUNTERPARTY, id)
                },
                sheetState = sheetState
            )
        }

        Type.METHOD -> {
            UpsertTransactionSelectionBottomSheet(
                data = transactionMethodList,
                selectedId = selectedTransactionMethod.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.METHOD, id)
                },
                sheetState = sheetState
            )
        }

        Type.NATURE -> {
            UpsertTransactionSelectionBottomSheet(
                data = transactionNatureList,
                selectedId = selectedTransactionNature.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.NATURE, id)
                },
                sheetState = sheetState
            )
        }

        Type.SOURCE -> {
            UpsertTransactionSelectionBottomSheet(
                data = transactionSourceList,
                selectedId = selectedTransactionSource.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.SOURCE, id)
                },
                sheetState = sheetState
            )
        }

        Type.TYPE -> {
            UpsertTransactionSelectionBottomSheet(
                data = transactionTypeList,
                selectedId = selectedTransactionType.id,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                select = { id ->
                    updateId(Type.TYPE, id)
                },
                sheetState = sheetState
            )
        }

        Type.DATE -> {
            DatePickerDialog(
                date = dateTimeDetails.first,
                updateDate = setDate,
                dismiss = {
                    sheetExpanded = Type.NONE
                },
            )
        }

        Type.TIME -> {
            TimePickerBottomSheet(
                date = dateTimeDetails.first,
                updateTime = setTime,
                dismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetExpanded = Type.NONE
                    }
                },
                sheetState = sheetState
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    if (screenState.transaction.id != 0L) {
                        Text(
                            text = stringResource(id = R.string.update_transaction_screen_label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.add_transaction_screen_label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                                if (!loading) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    upsertTransaction()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = TablerIcons.DeviceFloppy,
                                contentDescription = "save category"
                            )
                        }
                    }
                    if (screenState.transaction.id != 0L) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "delete transaction"
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
            ) {
                item(
                    key = "title",
                    contentType = "input"
                ) {
                    TextField(
                        value = title,
                        onValueChange = setTitle,
                        maxLines = 1,
                        enabled = !loading,
                        leadingIcon = {
                            Icon(
                                imageVector = TablerIcons.Typography,
                                contentDescription = "title icon"
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.title)
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                item(
                    key = "description",
                    contentType = "input"
                ) {
                    TextField(
                        value = description,
                        onValueChange = setDescription,
                        enabled = !loading,
                        leadingIcon = {
                            Icon(
                                imageVector = TablerIcons.FileText,
                                contentDescription = "description icon"
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.description)
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                item(
                    key = "amount",
                    contentType = "input"
                ) {
                    TextField(
                        value = amount,
                        onValueChange = {
                            setAmount(it)
                        },
                        maxLines = 1,
                        enabled = !loading,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        isError = formStatus.amountStatus == UpsertTransactionFormStatus.INVALID_AMOUNT_VALUE_ENTERED,
                        leadingIcon = {
                            Icon(
                                imageVector = TablerIcons.CurrencyDollar,
                                contentDescription = "amount icon"
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.amount)
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester = focusRequester)
                    )
                }
                item(
                    key = "date",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.DATE },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = dateTimeDetails.second,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = TablerIcons.CalendarEvent,
                                    contentDescription = "date"
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "time",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.TIME },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = dateTimeDetails.third,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(imageVector = TablerIcons.Alarm, contentDescription = "time")
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "category",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.CATEGORY },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedCategory.name,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.categoryStatus == UpsertTransactionFormStatus.NO_CATEGORY_SELECTED,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedCategoryIcon,
                                    contentDescription = "category icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_category))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "counterParty",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.COUNTERPARTY },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedCounterParty.name,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.counterPartyStatus == UpsertTransactionFormStatus.NO_COUNTERPARTY_SELECTED,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedCounterPartyIcon,
                                    contentDescription = "counter party icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_counter_party))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "method",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.METHOD },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedTransactionMethod.method,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.transactionMethodStatus == UpsertTransactionFormStatus.NO_TRANSACTION_METHOD_SELECTED,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedTransactionMethodIcon,
                                    contentDescription = "transaction method icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_transaction_method))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "nature",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.NATURE },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedTransactionNature.nature,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.transactionNatureStatus == UpsertTransactionFormStatus.NO_TRANSACTION_NATURE_SELECTED,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedTransactionNatureIcon,
                                    contentDescription = "transaction nature icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_transaction_nature))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "source",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.SOURCE },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedTransactionSource.name,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.transactionSourceStatus == UpsertTransactionFormStatus.NO_TRANSACTION_SOURCE_SELECTED ||
                                    formStatus.transactionSourceStatus == UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedTransactionSourceIcon,
                                    contentDescription = "transaction source icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_transaction_source))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
                item(
                    key = "type",
                    contentType = "dropdown"
                ) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { sheetExpanded = Type.TYPE },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedTransactionType.type,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            isError = formStatus.transactionTypeStatus == UpsertTransactionFormStatus.NO_TRANSACTION_TYPE_SELECTED,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = selectedTransactionTypeIcon,
                                    contentDescription = "transaction type icon"
                                )
                            },
                            label = {
                                Text(text = stringResource(id = R.string.single_transaction_type))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                    }
                }
            }
            if (loading && status != UpsertStatus.SUCCESS) {
                BottomLoadingIndicator()
            }
        }
    }
}

@Preview
@Composable
fun UpsertTransactionScreenPreview() {
    UpsertTransactionScreen(
        screenState = UpsertTransactionScreenState(),
        navigateBack = {},
        setDate = {},
        setTitle = {},
        setDescription = {},
        setAmount = {},
        setTime = { _, _ -> },
        setStrings = {},
        clearSnackBarText = {},
        upsertTransaction = {},
        deleteTransaction = {},
        getListForSheet = { _ -> emptyList() },
        updateId = { _, _ -> },
    )
}

fun NavGraphBuilder.addUpsertTransactionScreen(navController: NavController) {
    composable(
        route = Routes.UpsertTransaction.route,
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
        val viewModel: UpsertTransactionViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        UpsertTransactionScreen(
            screenState = state,
            navigateBack = {
                navController.popBackStack()
            },
            getListForSheet = viewModel::getListForSheetByType,
            setTitle = viewModel::setTitle,
            setDescription = viewModel::setDescription,
            setAmount = viewModel::setAmount,
            setDate = viewModel::setDate,
            setTime = viewModel::setTime,
            setStrings = viewModel::setStrings,
            clearSnackBarText = viewModel::clearSnackBarString,
            upsertTransaction = viewModel::upsertTransaction,
            deleteTransaction = viewModel::deleteTransaction,
            updateId = viewModel::updateDataByType,
        )
    }
}