package com.wenubey.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

suspend fun <T> safeRun(
    logger: Logger,
    block: suspend () -> T,
): T? = withContext(Dispatchers.IO) {
    try {
        block()
    } catch (e: Exception) {
        logger.error("Error occurred: ", e)
        null
    }
}
