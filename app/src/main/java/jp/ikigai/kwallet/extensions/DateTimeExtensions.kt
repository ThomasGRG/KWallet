package jp.ikigai.kwallet.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Long.toZonedDateTime() : ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

fun ZonedDateTime.toMilli() : Long {
    return this.toInstant().toEpochMilli()
}