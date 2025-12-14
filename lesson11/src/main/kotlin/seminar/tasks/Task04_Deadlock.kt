package seminar.tasks

/**
 * Задание 4. Deadlock
 *
 * Создайте пример deadlock с двумя ресурсами и двумя потоками.
 * Затем исправьте его.
 */
object Deadlock {

    /**
     * Демонстрация deadlock.
     * Внимание: этот метод зависнет! Используйте только для демонстрации.
     */
    fun runDeadlock() {
        val resourceA = Any()
        val resourceB = Any()

        val thread1 = Thread(Runnable {
            synchronized(resourceA) {
                println("thread1: lock resource A")
                Thread.sleep(500)
                synchronized(resourceB) {
                    Thread.sleep(500)
                    println("thread1: lock resource B")
                }
            }
        })

        val thread2 = Thread(Runnable {
            synchronized(resourceA) {
                println("thread1: lock resource A")
                Thread.sleep(500)
                synchronized(resourceB) {
                    Thread.sleep(500)
                    println("thread1: lock resource B")
                }
            }
        })

        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()

    }

    /**
     * Исправленная версия без deadlock.
     * Захватывайте ресурсы в одинаковом порядке.
     *
     * @return true если оба потока успешно завершились
     */
    fun runFixed(): Boolean {
        val resourceA = Any()
        val resourceB = Any()

        val thread1 = Thread(Runnable {
            synchronized(resourceA) {
                println("thread1: lock resource A")
                Thread.sleep(500)
                synchronized(resourceB) {
                    Thread.sleep(500)
                    println("thread1: lock resource B")
                }
            }
        })

        val thread2 = Thread(Runnable {
            synchronized(resourceA) {
                println("thread1: lock resource A")
                Thread.sleep(500)
                synchronized(resourceB) {
                    Thread.sleep(500)
                    println("thread1: lock resource B")
                }
            }
        })

        thread1.start()
        thread2.start()
        return true
    }
}
