package jp.ikigai.kwallet.ui

sealed class Routes(val route: String) {
    object Transactions: Routes("transactions")
    object UpsertTransaction: Routes("upsertTransaction?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertTransaction?id=$id"
        }
    }

    object More: Routes("more")

    object ChooseIcon: Routes("chooseIcon?defaultIcon={defaultIcon}") {
        fun getRoute(defaultIcon: String): String {
            return "chooseIcon?defaultIcon=$defaultIcon"
        }
    }

    object TransactionSources: Routes("transaction_sources")
    object UpsertTransactionSource: Routes("upsertTransactionSource?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertTransactionSource?id=$id"
        }
    }
    object TransactionSourceDetails: Routes("transactionSourceDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "transactionSourceDetails?id=${id}"
        }
    }

    object TransactionTypes: Routes("transaction_types")
    object UpsertTransactionType: Routes("upsertTransactionType?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertTransactionType?id=$id"
        }
    }
    object TransactionTypeDetails: Routes("transactionTypeDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "transactionTypeDetails?id=${id}"
        }
    }

    object TransactionMethods: Routes("transaction_methods")
    object UpsertTransactionMethod: Routes("upsertTransactionMethod?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertTransactionMethod?id=$id"
        }
    }
    object TransactionMethodDetails: Routes("transactionMethodDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "transactionMethodDetails?id=${id}"
        }
    }

    object TransactionNatures: Routes("transaction_natures")
    object UpsertTransactionNature: Routes("upsertTransactionNature?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertTransactionNature?id=$id"
        }
    }
    object TransactionNatureDetails: Routes("transactionNatureDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "transactionNatureDetails?id=${id}"
        }
    }

    object Currency: Routes("currency")

    object Category: Routes("category")
    object UpsertCategory: Routes("upsertCategory?categoryId={categoryId}") {
        fun getRoute(categoryId: Long = -1L): String {
            return "upsertCategory?categoryId=${categoryId}"
        }
    }
    object CategoryDetails: Routes("categoryDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "categoryDetails?id=${id}"
        }
    }

    object CounterParty: Routes("counter_party")
    object UpsertCounterParty: Routes("upsertCounterParty?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "upsertCounterParty?id=$id"
        }
    }
    object CounterPartyDetails: Routes("counterPartyDetails?id={id}") {
        fun getRoute(id: Long = -1L): String {
            return "counterPartyDetails?id=${id}"
        }
    }
}
