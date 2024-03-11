package jp.ikigai.kwallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ikigai.kwallet.data.Constants

@Entity("counter_party")
data class CounterParty(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val iconName: String = Constants.DEFAULT_COUNTERPARTY_ICON.name,
    val frequency: Int = 0,
    val lastUsed: Long = 0L,
)
