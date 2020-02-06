package com.gimslab.ratelimiterexam

import com.google.common.util.concurrent.RateLimiter
import java.lang.Long.max
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime

val rateLimiter = RateLimiter.create(Double.MAX_VALUE)

class App

fun main(args: Array<String>) {
	val transaction = 50
	val rate = 30.0
	rateLimiter.setRate(rate)

	val timepoint1 = currentTimeMillis()
	acquireTest(transaction)
	val summary1 = log(rate, transaction, timepoint1)

	val timepoint2 = currentTimeMillis()
	val sleep = 100L
	tryAcquireTest(transaction, sleep)
	val log2 = log(rate, transaction, timepoint2)

	println("----")
	println("acquire()    " + summary1)
	println("tryAcquire() " + log2 + " sleepMs:$sleep")
}

fun log(rate: Double, transaction: Int, started: Long): String {
	val elapsed = currentTimeMillis() - started
	val tps = transaction.toDouble() / max(elapsed, 1L).toDouble() * 1000.0
	return "setRate:$rate transactionCount:$transaction elapsedSec:${elapsed.toDouble() / 1000.toDouble()} actualTps:$tps"
}

fun acquireTest(transaction: Int) {
	for (i in 1..transaction) {
		val delayed = rateLimiter.acquire()
		println("acquire() ${LocalDateTime.now()} idx:$i delayed:$delayed")
	}
}

fun tryAcquireTest(transaction: Int, sleep: Long) {
	for (i in 1..transaction) {
		val permit = rateLimiter.tryAcquire()
		if (!permit)
			Thread.sleep(sleep)
		println("tryAcquire() ${LocalDateTime.now()} idx:$i delayed:${!permit}")
	}
}

