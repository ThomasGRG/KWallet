package jp.ikigai.kwallet.extensions

import java.time.YearMonth
import java.time.ZoneId

fun YearMonth.getFirstDayOfMonth() : Long {
    return this.atDay(1).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
}

fun YearMonth.getLastDayOfMonth() : Long {
    return this.atEndOfMonth().plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
}