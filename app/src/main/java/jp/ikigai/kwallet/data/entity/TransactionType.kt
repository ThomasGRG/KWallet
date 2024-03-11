package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.data.Constants

@Entity("transaction_type", indices = [Index(value = ["type"], unique = true)])
data class TransactionType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String = "",
    val baseType: String = Constants.DEBIT,
    val iconName: String = Constants.DEFAULT_TRANSACTION_TYPE_ICON.name,
    val frequency: Int = 0,
    val lastUsed: Long = 0L,
)
