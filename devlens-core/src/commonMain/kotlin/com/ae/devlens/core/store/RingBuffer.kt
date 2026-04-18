package com.ae.devlens.core.store

/**
 * Thread-safe fixed-capacity circular buffer.
 *
 * When full, the oldest item is evicted to make room for the new one.
 * All operations are O(1) amortized.
 *
 * This is a pure data structure with no coroutine or Flow dependency —
 * use [PluginStore] when you need reactive observation.
 *
 * @param capacity Maximum number of items to hold. Must be > 0.
 */
public class RingBuffer<T>(public val capacity: Int) {

    init {
        require(capacity > 0) { "RingBuffer capacity must be > 0, was $capacity" }
    }

    @Suppress("UNCHECKED_CAST")
    private val buffer: Array<Any?> = arrayOfNulls(capacity)
    private var head = 0   // index of next write
    private var size = 0

    /**
     * Add an item to the buffer.
     * If the buffer is at capacity, the oldest item is silently evicted.
     */
    public fun add(item: T) {
        buffer[head] = item
        head = (head + 1) % capacity
        if (size < capacity) size++
    }

    /** Returns all items in insertion order (oldest first). */
    @Suppress("UNCHECKED_CAST")
    public fun toList(): List<T> {
        if (size == 0) return emptyList()
        val result = ArrayList<T>(size)
        val start = if (size < capacity) 0 else head
        for (i in 0 until size) {
            result.add(buffer[(start + i) % capacity] as T)
        }
        return result
    }

    /** Remove all items from the buffer. */
    public fun clear() {
        for (i in buffer.indices) buffer[i] = null
        head = 0
        size = 0
    }

    /** Current number of items in the buffer. */
    public val count: Int get() = size

    /** True when the buffer holds no items. */
    public val isEmpty: Boolean get() = size == 0
}
