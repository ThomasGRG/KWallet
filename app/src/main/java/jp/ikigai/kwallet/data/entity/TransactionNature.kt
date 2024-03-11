package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.data.Constants

@Entity("transaction_nature")
data class TransactionNature(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nature: String = "",
    val iconName: String = Constants.DEFAULT_TRANSACTION_NATURE_ICON.name,
    val frequency: Int = 0,
    val lastUsed: Long = 0L,
)
