package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.data.Constants

@Entity("transaction_method")
data class TransactionMethod(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    val method: String = "",
    val iconName: String = Constants.DEFAULT_TRANSACTION_METHOD_ICON.name,
    val frequency: Int = 0,
    val lastUsed: Long = 0L,
)
