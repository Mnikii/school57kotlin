package ru.tbank.education.school.lesson2

import kotlin.random.Random

object DemoMap {
    fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    fun createMap(): Map<String, Int> {
        val map = buildMap {
            repeat(5) {
                put(generateRandomString(Random.nextInt(5, 10)), Random.nextInt(1, 100))
            }
        }
        return map

    }

    fun maxValue(map: Map<String, Int>): Int {
        return map.maxByOrNull { it.value }!!.value
    }

    fun keyForMaxValue(map: Map<String, Int>): String {
        return map.maxByOrNull { it.value }!!.key
    }

    fun sortByValueDesc(map: Map<String, Int>): Map<String, Int> {
        return map.toList().sortedByDescending { (_, value) -> value }.toMap(LinkedHashMap())
    }

    fun filterOddValues(map: Map<String, Int>): Map<String, Int> {
        return map.filterValues { it % 2 == 0 }
    }

}
