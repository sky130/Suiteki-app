package ml.sky233.choseki.util

import android.os.Environment
import ml.sky233.suiteki.MainApplication.Companion.context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {

    fun deleteDirWithFile(name: String) {
        val dir = File(name)
        if (!dir.exists() || !dir.isDirectory) return
        for (file in dir.listFiles()!!) if (file.isFile) file.delete() // 删除所有文件
        else if (file.isDirectory) deleteDirWithFile(file.path) // 递规的方式删除文件夹
        dir.delete() // 删除目录本身
    }

    fun isFile(path: String): Boolean {
        return File(path).exists()
    }


    fun fileIsExist(fileName: String): Boolean {
        val file = File(fileName)
        return if (file.exists()) true else {
            file.mkdirs()
        }
    }

    fun getFileBytes(fileName: String): ByteArray? {
        if (File(fileName).exists()) try {
            lateinit var buffer: ByteArray
            FileInputStream(fileName).use {
                buffer = ByteArray(it.available())
                it.read(buffer)
            }
            return buffer
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getFileBytes(inputStream: InputStream): ByteArray? {
        try {
            lateinit var buffer: ByteArray
            inputStream.use {
                buffer = ByteArray(it.available())
                it.read(buffer)
            }
            return buffer
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun writeFileBytes(filename: String, bytes: ByteArray): ByteArray {
        val file = File(filename)
        try {
            if (!file.isFile) file.createNewFile()
            FileOutputStream(file).use { it.write(bytes) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bytes
    }

    fun getFileText(filename: String): String {
        var res = ""
        try {
            val file = File(filename)
            if (!file.exists()) {
                file.createNewFile()
                return res
            }
            lateinit var buffer: ByteArray
            var length: Int = 0
            FileInputStream(filename).use {
                length = it.available()
                buffer = ByteArray(length)
                it.read(buffer)
            }
            res = String(buffer, 0, length)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return res
    }

    fun writeFileText(filename: String, text: String): String {
        val file = File(filename)
        try {
            if (!file.exists()) file.createNewFile()
            FileOutputStream(filename).use {
                it.write(text.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text
    }

    fun getFileText(inputStream: InputStream): String {
        var res = ""
        try {
            lateinit var buffer: ByteArray
            inputStream.use {
                val length = inputStream.available()
                buffer = ByteArray(length)
                inputStream.read(buffer)
            }
            res = String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    fun getDataPath(): String {
        return "${context.getExternalFilesDir(null)}"
    }

    fun getPackagePath(str: String = ""): String {
        return "${context.getExternalFilesDir(null)}${File.separator}package${File.separator}${str}"
    }
}













