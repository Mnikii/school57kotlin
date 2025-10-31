package ru.tbank.education.school.lesson2

fun main() {
    val list = DemoList.createList()
    println("Original list: $list")

    val sum = DemoList.sumList()
    println("Sum of all elements: $sum")

    val sumEven = DemoList.sumEvenList()
    println("Sum of even elements: $sumEven")

    val multiplied = DemoList.multiplyList()
    println("List multiplied 2: $multiplied")

    val max = DemoList.maxElement()
    println("Max element: $max")

    val sortedDesc = DemoList.sortDesc()
    println("List sorted descending: $sortedDesc")


    val map = DemoMap.createMap()
    println("Original map: $map")
    TODO()
}
