package jp.ikigai.kwallet.data

import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import compose.icons.tablericons.BuildingBank
import compose.icons.tablericons.CashBanknote
import compose.icons.tablericons.CreditCard
import compose.icons.tablericons.TriangleSquareCircle
import compose.icons.tablericons.Users
import jp.ikigai.kwallet.data.entity.Currency

object Constants {

    const val decayConstant = 0.00005f

    const val tweenDuration = 350

    val currencyList = listOf(
        Currency("Indian Rupee", "INR", "IN"),
        Currency("United States Dollar", "USD", "US"),
        Currency("Euro", "EUR", "EU"),
        Currency("Japanese Yen", "JPY", "JP"),
        Currency("British Pound Sterling", "GBP", "GB"),
        Currency("Swiss Franc", "CHF", "CH"),
        Currency("Canadian Dollar", "CAD", "CA"),
        Currency("Australian Dollar", "AUD", "AU"),
        Currency("Chinese Yuan", "CNY", "CN"),
        Currency("Brazilian Real", "BRL", "BR"),
    )

    fun getCurrencyByCode(code: String): Currency? {
        return currencyList.find { it.code == code }
    }

    const val DEBIT = "Debit"
    const val CREDIT = "Credit"

    val DEFAULT_CATEGORY_ICON = TablerIcons.Archive
    val DEFAULT_COUNTERPARTY_ICON = TablerIcons.Users
    val DEFAULT_TRANSACTION_METHOD_ICON = TablerIcons.CreditCard
    val DEFAULT_TRANSACTION_NATURE_ICON = TablerIcons.TriangleSquareCircle
    val DEFAULT_TRANSACTION_SOURCE_ICON = TablerIcons.BuildingBank
    val DEFAULT_TRANSACTION_TYPE_ICON = TablerIcons.CashBanknote
}