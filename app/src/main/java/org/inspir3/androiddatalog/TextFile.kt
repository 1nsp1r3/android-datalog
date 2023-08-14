package org.inspir3.androiddatalog

import android.os.Environment
import java.io.BufferedWriter
import java.io.File

/**
 * Required:
 *   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 *   <application android:requestLegacyExternalStorage="true">
 */
class TextFile {
    private lateinit var textFile: BufferedWriter

    fun open(filename: String) {
        textFile = BufferedWriter(
            File(Environment.getExternalStorageDirectory(), filename).writer()
        )
    }

    fun print(text: String) {
        textFile.write(text)
    }

    fun println(text: String) {
        print("$text\n")
    }

    fun flush() {
        textFile.flush()
    }

    fun close() {
        textFile.close()
    }
}