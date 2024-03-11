package jp.ikigai.kwallet.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import jp.ikigai.kwallet.ui.screens.addCategoryScreen
import jp.ikigai.kwallet.ui.screens.addChooseIconScreen
import jp.ikigai.kwallet.ui.screens.addCounterPartyScreen
import jp.ikigai.kwallet.ui.screens.addCurrencyScreen
import jp.ikigai.kwallet.ui.screens.addMoreScreen
import jp.ikigai.kwallet.ui.screens.addTransactionMethodsScreen
import jp.ikigai.kwallet.ui.screens.addTransactionNaturesScreen
import jp.ikigai.kwallet.ui.screens.addTransactionScreen
import jp.ikigai.kwallet.ui.screens.addTransactionSourceScreen
import jp.ikigai.kwallet.ui.screens.addTransactionTypesScreen
import jp.ikigai.kwallet.ui.screens.details.addCategoryDetailsScreen
import jp.ikigai.kwallet.ui.screens.details.addCounterPartyDetailsScreen
import jp.ikigai.kwallet.ui.screens.details.addTransactionMethodDetailsScreen
import jp.ikigai.kwallet.ui.screens.details.addTransactionNatureDetailsScreen
import jp.ikigai.kwallet.ui.screens.details.addTransactionSourceDetailsScreen
import jp.ikigai.kwallet.ui.screens.details.addTransactionTypeDetailsScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertCategoryScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertCounterPartyScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertTransactionMethodScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertTransactionNatureScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertTransactionScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertTransactionSourceScreen
import jp.ikigai.kwallet.ui.screens.upsert.addUpsertTransactionTypeScreen

@Composable
fun BaseScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Transactions.route,
        modifier = Modifier.fillMaxSize()
    ) {
        addCategoryScreen(navController = navController)
        addCategoryDetailsScreen(navController = navController)
        addUpsertCategoryScreen(navController = navController)

        addChooseIconScreen(navController = navController)

        addCounterPartyScreen(navController = navController)
        addCounterPartyDetailsScreen(navController = navController)
        addUpsertCounterPartyScreen(navController = navController)

        addCurrencyScreen(navController = navController)

        addMoreScreen(navController = navController)

        addTransactionScreen(navController = navController)
        addUpsertTransactionScreen(navController = navController)

        addTransactionMethodsScreen(navController = navController)
        addTransactionMethodDetailsScreen(navController = navController)
        addUpsertTransactionMethodScreen(navController = navController)

        addTransactionNaturesScreen(navController = navController)
        addTransactionNatureDetailsScreen(navController = navController)
        addUpsertTransactionNatureScreen(navController = navController)

        addTransactionSourceScreen(navController = navController)
        addTransactionSourceDetailsScreen(navController = navController)
        addUpsertTransactionSourceScreen(navController = navController)

        addTransactionTypesScreen(navController = navController)
        addTransactionTypeDetailsScreen(navController = navController)
        addUpsertTransactionTypeScreen(navController = navController)
    }
}

@Preview
@Composable
fun BaseScreenPreview() {
    BaseScreen()
}
