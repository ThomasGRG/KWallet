package jp.ikigai.kwallet.koin

import jp.ikigai.kwallet.data.repository.CategoryRepository
import jp.ikigai.kwallet.data.repository.CounterPartyRepository
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
import jp.ikigai.kwallet.data.repository.TransactionNatureRepository
import jp.ikigai.kwallet.data.repository.TransactionRepository
import jp.ikigai.kwallet.data.repository.TransactionSourceRepository
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import jp.ikigai.kwallet.ui.viewmodels.CategoryViewModel
import jp.ikigai.kwallet.ui.viewmodels.ChooseIconViewModel
import jp.ikigai.kwallet.ui.viewmodels.CounterPartyViewModel
import jp.ikigai.kwallet.ui.viewmodels.CurrencyViewModel
import jp.ikigai.kwallet.ui.viewmodels.TransactionMethodsViewModel
import jp.ikigai.kwallet.ui.viewmodels.TransactionNaturesViewModel
import jp.ikigai.kwallet.ui.viewmodels.TransactionScreenViewModel
import jp.ikigai.kwallet.ui.viewmodels.TransactionSourcesViewModel
import jp.ikigai.kwallet.ui.viewmodels.TransactionTypesViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.CategoryDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.CounterPartyDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionMethodDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionNatureDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionSourceDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.details.TransactionTypeDetailsViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertCategoryViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertCounterPartyViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionMethodViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionNatureViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionSourceViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionTypeViewModel
import jp.ikigai.kwallet.ui.viewmodels.upsert.UpsertTransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { getDatabase(androidContext()) }

    single { getBankDao(get()) }
    single { getCategoryDao(get()) }
    single { getCounterPartyDao(get()) }
    single { getTransactionDao(get()) }
    single { getTransactionMethodDao(get()) }
    single { getTransactionNatureDao(get()) }
    single { getTransactionTypeDao(get()) }

    factory { TransactionSourceRepository(get()) }
    factory { CategoryRepository(get()) }
    factory { CounterPartyRepository(get()) }
    factory { TransactionRepository(get()) }
    factory { TransactionMethodRepository(get()) }
    factory { TransactionNatureRepository(get()) }
    factory { TransactionTypeRepository(get()) }

    viewModel() { CurrencyViewModel() }
    viewModel() { ChooseIconViewModel(get()) }

    viewModel() { TransactionScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel() { TransactionMethodsViewModel(get()) }
    viewModel() { TransactionNaturesViewModel(get()) }
    viewModel() { TransactionSourcesViewModel(get()) }
    viewModel() { TransactionTypesViewModel(get()) }
    viewModel() { CategoryViewModel(get()) }
    viewModel() { CounterPartyViewModel(get()) }

    viewModel() { CategoryDetailsViewModel(get(), get()) }
    viewModel() { CounterPartyDetailsViewModel(get(), get()) }
    viewModel() { TransactionMethodDetailsViewModel(get(), get()) }
    viewModel() { TransactionNatureDetailsViewModel(get(), get()) }
    viewModel() { TransactionSourceDetailsViewModel(get(), get()) }
    viewModel() { TransactionTypeDetailsViewModel(get(), get()) }

    viewModel() {
        UpsertTransactionViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel() { UpsertCategoryViewModel(get(), get()) }
    viewModel() { UpsertCounterPartyViewModel(get(), get()) }
    viewModel() { UpsertTransactionMethodViewModel(get(), get()) }
    viewModel() { UpsertTransactionNatureViewModel(get(), get()) }
    viewModel() { UpsertTransactionSourceViewModel(get(), get()) }
    viewModel() { UpsertTransactionTypeViewModel(get(), get()) }
}