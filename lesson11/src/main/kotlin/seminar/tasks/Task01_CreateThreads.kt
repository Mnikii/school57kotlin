package seminar.tasks

/**
 * Задание 1. Создание потоков
 *
 * Создайте 3 потока с именами "Thread-A", "Thread-B", "Thread-C".
 * Каждый поток должен вывести своё имя 5 раз с задержкой 500мс.
 */
object CreateThreads {

    /**
     * @return список созданных потоков (после их завершения)
     */
    fun run(): List<Thread> {
        val threads = mutableListOf<Thread>()
        val names = listOf<String>("Thread-A", "Thread-B", "Thread-C")
        names.forEach {
            threads.add(Thread({
                Thread.sleep(500)
                repeat(5) {
                    println(Thread.currentThread().name)
                }
            }, it))
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        return threads


    }

}
