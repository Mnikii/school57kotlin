package seminar.tasks

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

/**
 * Задание 2. Race condition
 *
 * Создайте переменную counter = 0.
 * Запустите 10 потоков, каждый из которых увеличивает counter на 1000.
 * Выведите финальное значение и объясните результат.
 */
object RaceCondition {

    /**
     * @return финальное значение counter (может быть меньше 10000 из-за race condition)
     */
    fun run(): Int {
        var counter = AtomicInteger(0)
        val threads = mutableListOf<Thread>()
        val lock = Any()
        repeat(10) {
            val thread = Thread {
                repeat(1000) {
                    counter.incrementAndGet()
                }
            }
            threads.add(thread)
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        return counter.get()

    }
}
