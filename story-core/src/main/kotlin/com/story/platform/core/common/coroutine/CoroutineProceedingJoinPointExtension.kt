package com.story.platform.core.common.coroutine

import org.aspectj.lang.ProceedingJoinPoint
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

val ProceedingJoinPoint.coroutineArgs: Array<Any?>
    get() = this.args.sliceArray(0 until this.args.size - 1)

suspend fun ProceedingJoinPoint.proceedCoroutine(
    args: Array<Any?> = this.coroutineArgs,
): Any? =
    suspendCoroutineUninterceptedOrReturn { continuation ->
        this.proceed(args + continuation)
    }

@Suppress("unchecked_cast")
fun ProceedingJoinPoint.runCoroutine(
    runner: suspend () -> Any?,
): Any? =
    runner.startCoroutineUninterceptedOrReturn(this.args.last() as Continuation<Any?>)
