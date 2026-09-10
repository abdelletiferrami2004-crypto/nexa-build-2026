package com.example.util

import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Enterprise In-Memory & Redis-Style Caching Layer for NEXA
 * Provides sub-millisecond data access for high-frequency messages, stories, and translations.
 */
object NexaMemoryAndRedisCacheManager {

    private const val TAG = "NEXA_MemoryCache"
    private const val MAX_MEMORY_ENTRIES = 500

    private data class CacheEntry<T>(
        val value: T,
        val timestamp: Long = System.currentTimeMillis(),
        val ttlMillis: Long = 1000L * 60 * 30 // Default 30 minutes TTL
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttlMillis
    }

    private val mutex = Mutex()
    private val memoryLruCache = object : LruCache<String, CacheEntry<Any>>(MAX_MEMORY_ENTRIES) {
        override fun sizeOf(key: String, value: CacheEntry<Any>): Int = 1
    }

    // Cache metrics
    private var hitCount = 0L
    private var missCount = 0L

    suspend fun <T : Any> put(key: String, value: T, ttlMillis: Long = 1000L * 60 * 30) {
        mutex.withLock {
            memoryLruCache.put(key, CacheEntry(value, ttlMillis = ttlMillis))
            Log.d(TAG, "Cached entry for key: $key (TTL: ${ttlMillis / 1000}s)")
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(key: String): T? {
        return mutex.withLock {
            val entry = memoryLruCache.get(key)
            if (entry == null) {
                missCount++
                null
            } else if (entry.isExpired()) {
                memoryLruCache.remove(key)
                missCount++
                Log.d(TAG, "Cache expired for key: $key")
                null
            } else {
                hitCount++
                entry.value as? T
            }
        }
    }

    suspend fun remove(key: String) {
        mutex.withLock {
            memoryLruCache.remove(key)
        }
    }

    suspend fun clear() {
        mutex.withLock {
            memoryLruCache.evictAll()
            Log.d(TAG, "Cache cleared")
        }
    }

    fun getCacheStats(): Map<String, Any> {
        val total = hitCount + missCount
        val hitRate = if (total > 0) (hitCount.toDouble() / total * 100).toInt() else 100
        return mapOf(
            "size" to memoryLruCache.size(),
            "hitCount" to hitCount,
            "missCount" to missCount,
            "hitRatePercent" to hitRate
        )
    }
}
