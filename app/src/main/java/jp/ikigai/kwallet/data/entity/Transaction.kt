package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.extensions.toMilli
import java.time.ZonedDateTime

@Entity("transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Float = 0f,
    val title: String = "",
    val description: String = "",
    val time: Long = ZonedDateTime.now().toMilli(),
    val currency: String = "",
    val categoryId: Long = 0L,
    val transactionSourceId: Long = 0L,
    val counterPartyId: Long = 0L,
    val transactionNatureId: Long = 0L,
    val transactionMethodId: Long = 0L,
    val transactionTypeId: Long = 0L,
)
