package jp.ikigai.kwallet.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.ui.Routes
import jp.ikigai.kwallet.ui.components.IconDetailsDialog
import jp.ikigai.kwallet.ui.viewmodels.ChooseIconScreenState
import jp.ikigai.kwallet.ui.viewmodels.ChooseIconViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ChooseIconScreen(
    defaultIcon: String,
    navigateBackWithResult: (String) -> Unit,
    setSearchText: (String) -> Unit,
    screenState: ChooseIconScreenState
) {
    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val icons by remember(key1 = screenState.icons) {
        mutableStateOf(screenState.icons)
    }

    val searchText by remember(key1 = screenState.searchText) {
        mutableStateOf(screenState.searchText)
    }

    val loading by remember(key1 = screenState.loading) {
        mutableStateOf(screenState.loading)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var iconToPreview by remember {
        mutableStateOf("")
    }

    if (showDialog) {
        IconDetailsDialog(
            dismiss = { showDialog = false },
            iconName = iconToPreview
        )
    }

    BackHandler(
        enabled = !showDialog
    ) {
        navigateBackWithResult(defaultIcon)
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.choose_icon_screen_label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = setSearchText,
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.search))
                        },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
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
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                navigateBackWithResult(defaultIcon)
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
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(58.dp)
            ) {
                itemsIndexed(
                    items = icons,
                    key = { _, icon -> "icon-${icon.name}" }
                ) { _, icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = icon.name,
                        modifier = Modifier
                            .size(52.dp)
                            .padding(6.dp)
                            .combinedClickable(
                                onClick = {
                                    navigateBackWithResult(icon.name)
                                },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    iconToPreview = icon.name
                                    showDialog = true
                                },
                                onLongClickLabel = icon.name,
                                role = Role.Button,
                            )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChooseIconScreenPreview() {
    ChooseIconScreen(
        defaultIcon = "",
        navigateBackWithResult = { _ -> },
        setSearchText = {},
        screenState = ChooseIconScreenState()
    )
}

fun NavGraphBuilder.addChooseIconScreen(navController: NavController) {
    composable(
        route = Routes.ChooseIcon.route,
        arguments = listOf(
            navArgument("defaultIcon") {
                type = NavType.StringType
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
        val viewModel: ChooseIconViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        ChooseIconScreen(
            defaultIcon = viewModel.defaultIcon,
            navigateBackWithResult = { value ->
                navController.previousBackStackEntry?.savedStateHandle?.set("icon", value)
                navController.popBackStack()
            },
            setSearchText = viewModel::setSearchText,
            screenState = state
        )
    }
}