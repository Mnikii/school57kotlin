package ru.tbank.education.school.lesson10.homework

import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object DocumentationGenerator {
    fun generateDoc(obj: Any): String {
        val kClass = obj::class

        if (kClass.findAnnotation<InternalApi>() != null) {
            return "Документация скрыта (InternalApi)."
        }
        val docClass = kClass.findAnnotation<DocClass>() ?: return "Нет документации для класса."

        val ctorParamsByName = kClass.primaryConstructor?.parameters.orEmpty()
            .associateBy { it.name }

        val stringBuilder = StringBuilder()
        stringBuilder.append("=== Документация: ${kClass.simpleName} ===\n")
        stringBuilder.append("Описание: ${docClass.description}\n")
        stringBuilder.append("Автор: ${docClass.author}\n")
        stringBuilder.append("Версия: ${docClass.version}\n")

        val properties = kClass.memberProperties
            .filterNot {
                it.findAnnotation<InternalApi>() != null ||
                        ctorParamsByName[it.name]?.findAnnotation<InternalApi>() != null
            }
            .sortedBy { it.name }

        if (properties.isNotEmpty()) {
            stringBuilder.append("\n--- Свойства ---\n")
            for (prop in properties) {
                stringBuilder.append("- ${prop.name}\n")
                val docProp = prop.findAnnotation<DocProperty>()
                if (docProp != null) {
                    stringBuilder.append("  Описание: ${if (docProp.description.isNotBlank()) docProp.description else "Не указано"}\n")
                    if (docProp.example.isNotBlank()) {
                        stringBuilder.append("  Пример: ${docProp.example}\n")
                    }
                }
            }
        }

        val functions = kClass.declaredMemberFunctions
            .filterNot {
                it.findAnnotation<InternalApi>() != null
                        || it.name in setOf("toString", "equals", "hashCode", "copy")
                        || it.name.startsWith("component")
            }
            .sortedBy { it.name }

        if (functions.isNotEmpty()) {
            stringBuilder.appendLine("\n--- Методы ---")
            for (f in functions.sortedBy { it.name }) {
                val valueParams = f.parameters.filter { it.kind == KParameter.Kind.VALUE }
                val signatureParams = valueParams.joinToString(", ") { param ->
                    "${param.name}: ${param.type.toString().removePrefix("kotlin.")}"
                }
                stringBuilder.appendLine("- ${f.name}($signatureParams)")

                val dm = f.findAnnotation<DocMethod>()
                if (dm != null && dm.description.isNotBlank()) {
                    stringBuilder.appendLine("  Описание: ${dm.description}")
                }


                if (valueParams.isNotEmpty()) {
                    stringBuilder.appendLine("  Параметры:")
                    for (param in valueParams) {
                        val pdesc = param.findAnnotation<DocParam>()?.description ?: "Нет описания"
                        stringBuilder.appendLine("    - ${param.name}: $pdesc")
                    }
                }

                val returnsDesc = dm?.returns ?: "Нет описания"
                stringBuilder.appendLine("  Возвращает: $returnsDesc")
            }
        }

        return stringBuilder.toString().trimEnd()
    }
}