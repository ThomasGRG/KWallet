package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.data.Constants

@Entity("transaction_sources")
data class TransactionSource(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    val name: String = "",
    val iconName: String = Constants.DEFAULT_TRANSACTION_SOURCE_ICON.name,
    val balance: Float = 0f,
    val currency: String = Constants.currencyList[0].code,
    val frequency: Int = 0,
    val lastUsed: Long = 0L,
)
