package ru.tbank.education.school.lesson9

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class Archiver(
    private val extensions: Set<String> = setOf("txt", "log")
) {
    fun addDirectoryToZip(
        rootDir: File,
        currentFile: File,
        zipos: ZipOutputStream
    ) {
        // рекурсивно обходим папки
        if (currentFile.isDirectory) {
            currentFile.listFiles()?.forEach { file ->
                addDirectoryToZip(rootDir, file, zipos)
            }
            return
        }
        val ext = currentFile.extension.lowercase()
        if (ext !in extensions) return

        val entryName = rootDir.toPath().relativize(currentFile.toPath()).toString()

        println("$entryName - ${currentFile.length()}")

        FileInputStream(currentFile).use { fis ->
            val entry = ZipEntry(entryName)
            zipos.putNextEntry(entry)
            fis.copyTo(zipos, bufferSize = 8192) // 8192 как можно другое
            zipos.closeEntry()
        }
    }

    fun zipDirectory(sourceDir: File, zipFile: File) {
        FileOutputStream(zipFile).use { fileos ->
            ZipOutputStream(fileos).use { zipOs ->
                addDirectoryToZip(sourceDir, sourceDir, zipOs)
            }
        }
    }
}