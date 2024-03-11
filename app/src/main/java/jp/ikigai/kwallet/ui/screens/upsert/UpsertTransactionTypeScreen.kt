package jp.ikigai.kwallet.ui.screens.upsert

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import compose.icons.TablerIcons
import compose.icons.tablericons.DeviceFloppy
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.Constants.CREDIT
import jp.ikigai.kwallet.data.Constants.DEBIT
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.extensions.getIconByType
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.BottomLoadingIndicator
import jp.ikigai.kwallet.ui.components.ResetIconDialog
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionTypeScreenState
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionTypeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun UpsertTransactionTypeScreen(
    navigateBack: () -> Unit,
    chooseIcon: (String) -> Unit,
    selectedIcon: String?,
    screenState: UpsertTransactionTypeScreenState,
    setType: (String) -> Unit,
    setBaseType: (String) -> Unit,
    upsertTransactionType: (String?) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    var icon by remember {
        mutableStateOf(selectedIcon)
    }

    var orientation by remember {
        mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val debitString by remember {
        mutableStateOf(
            context.getString(R.string.add_transaction_type_dialog_radio_debit)
        )
    }

    val creditString by remember {
        mutableStateOf(
            context.getString(R.string.add_transaction_type_dialog_radio_credit)
        )
    }

    val saveSuccessString by remember {
        mutableStateOf(
            context.getString(R.string.save_success)
        )
    }

    val typeNameInvalidString by remember {
        mutableStateOf(
            context.getString(R.string.add_dialog_empty_error_label)
        )
    }

    val loading by remember(key1 = screenState.loading) {
        mutableStateOf(screenState.loading)
    }

    val type by remember(key1 = screenState.transactionType.type) {
        mutableStateOf(
            screenState.transactionType.type
        )
    }

    val baseTypeString by remember(key1 = screenState.transactionType.baseType) {
        mutableStateOf(
            if (screenState.transactionType.baseType == DEBIT) debitString else creditString
        )
    }

    val typeValid by remember(key1 = screenState.typeValid) {
        mutableStateOf(
            screenState.typeValid
        )
    }

    val upsertStatus by remember(key1 = screenState.upsertStatus) {
        mutableStateOf(
            screenState.upsertStatus
        )
    }

    LaunchedEffect(key1 = typeValid, key2 = upsertStatus) {
        if (!typeValid) {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = typeNameInvalidString,
                    withDismissAction = true
                )
            }
        } else if (upsertStatus == UpsertStatus.SUCCESS) {
            scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = saveSuccessString,
                    withDismissAction = true
                )
                if (result == SnackbarResult.Dismissed) {
                    navigateBack()
                }
            }
        }
    }

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        ResetIconDialog(
            dismiss = {
                showResetDialog = false
            },
            reset = {
                icon = Constants.DEFAULT_TRANSACTION_TYPE_ICON.name
                showResetDialog = false
            }
        )
    }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(loading) {
        if (!loading) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collectLatest { orientation = it }
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
                    if (screenState.transactionType.id != 0L) {
                        Text(
                            text = stringResource(id = R.string.update_transaction_type_screen_label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.add_transaction_type_screen_label),
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
                                    upsertTransactionType(icon)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = TablerIcons.DeviceFloppy,
                                contentDescription = "save transaction type"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
    ) { contentPadding ->
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (icon != null) {
                            icon!!.getIconByType(Type.TYPE)
                        } else {
                            screenState.transactionType.iconName.getIconByType(Type.TYPE)
                        },
                        contentDescription = "default icon",
                        modifier = Modifier
                            .size(120.dp)
                            .combinedClickable(
                                enabled = !loading,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    chooseIcon(icon ?: screenState.transactionType.iconName)
                                },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showResetDialog = true
                                }
                            ),
                    )
                    TextField(
                        value = type,
                        onValueChange = setType,
                        enabled = !loading,
                        modifier = Modifier
                            .focusRequester(focusRequester = focusRequester)
                            .fillMaxWidth(0.9f),
                        label = {
                            Text(text = stringResource(id = R.string.add_type_dialog_label))
                        },
                        isError = !typeValid,
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                    ) {
                        TextField(
                            value = baseTypeString,
                            onValueChange = {},
                            enabled = !loading,
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            placeholder = {
                                Text(text = stringResource(id = R.string.add_source_dialog_transaction_type_label))
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = debitString)
                                },
                                onClick = {
                                    setBaseType(DEBIT)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = creditString)
                                },
                                onClick = {
                                    setBaseType(CREDIT)
                                    expanded = false
                                }
                            )
                        }
                    }
                    if (loading && upsertStatus != UpsertStatus.SUCCESS) {
                        BottomLoadingIndicator()
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (icon != null) {
                                icon!!.getIconByType(Type.TYPE)
                            } else {
                                screenState.transactionType.iconName.getIconByType(Type.TYPE)
                            },
                            contentDescription = "default icon",
                            modifier = Modifier
                                .size(120.dp)
                                .weight(1f)
                                .combinedClickable(
                                    enabled = !loading,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        chooseIcon(icon ?: screenState.transactionType.iconName)
                                    },
                                    onLongClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showResetDialog = true
                                    }
                                ),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextField(
                                value = type,
                                onValueChange = setType,
                                enabled = !loading,
                                modifier = Modifier
                                    .focusRequester(focusRequester = focusRequester)
                                    .fillMaxWidth(0.9f),
                                label = {
                                    Text(text = stringResource(id = R.string.add_type_dialog_label))
                                },
                                isError = !typeValid,
                                maxLines = 1,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                    }
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f),
                            ) {
                                TextField(
                                    value = baseTypeString,
                                    onValueChange = {},
                                    enabled = !loading,
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    placeholder = {
                                        Text(text = stringResource(id = R.string.add_source_dialog_transaction_type_label))
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = debitString)
                                        },
                                        onClick = {
                                            setBaseType(DEBIT)
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = creditString)
                                        },
                                        onClick = {
                                            setBaseType(CREDIT)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (loading && upsertStatus != UpsertStatus.SUCCESS) {
                        BottomLoadingIndicator()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UpsertTransactionTypeScreenPreview() {
    UpsertTransactionTypeScreen(
        navigateBack = {},
        chooseIcon = {},
        selectedIcon = null,
        screenState = UpsertTransactionTypeScreenState(),
        setType = {},
        setBaseType = {},
        upsertTransactionType = {}
    )
}

fun NavGraphBuilder.addUpsertTransactionTypeScreen(navController: NavController) {
    composable(
        route = Routes.UpsertTransactionType.route,
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
    ) { backStackEntry ->
        val viewModel: UpsertTransactionTypeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        UpsertTransactionTypeScreen(
            navigateBack = {
                navController.popBackStack()
            },
            selectedIcon = backStackEntry.savedStateHandle.get<String>("icon"),
            chooseIcon = { defaultIcon ->
                navController.navigate(Routes.ChooseIcon.getRoute(defaultIcon)) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            screenState = state,
            setType = viewModel::setType,
            setBaseType = viewModel::setBaseType,
            upsertTransactionType = viewModel::upsertTransactionType
        )
    }
}