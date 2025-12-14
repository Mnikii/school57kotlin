package seminar.tasks

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

/**
 * Задание 10. Structured concurrency
 *
 * Создайте корутину, которая запускает 5 дочерних корутин.
 * Если одна из них падает с исключением, все остальные должны отмениться.
 */
object StructuredConcurrency {

    /**
     * @param failingCoroutineIndex индекс корутины, которая должна упасть (0-4)
     * @return количество корутин, которые успели завершиться до отмены
     */
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        val atomicInteger = AtomicInteger(0)
        try {
            coroutineScope {
                repeat(5) {
                    launch {
                        if (atomicInteger.get() == failingCoroutineIndex) {
                            throw RuntimeException("Failing coroutine index $atomicInteger")
                        }
                        atomicInteger.incrementAndGet()
                    }
                }
            }
        }
        catch (ex: RuntimeException){
            println("Hook exeption with message: ${ex.message}")
        }
        atomicInteger.get()
    }
}
