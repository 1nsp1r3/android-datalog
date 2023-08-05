package org.inspir3.androiddatalog

import android.content.Context
import io.reactivex.rxjava3.disposables.Disposable
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.LocalDateTime

class FileLog(
    private val context: Context,
    private val console: Console,
) {
    private val streams: MutableList<Flux> = mutableListOf()
    private val subscriptions: MutableList<Disposable> = mutableListOf()

    private lateinit var writer: OutputStreamWriter

    private var maxIndex: Int = -1
    private var currentLine: String = ""

    fun addFlux(stream: Flux): Boolean {
        if (stream.index > maxIndex) maxIndex = stream.index
        return this.streams.add(stream)
    }

    fun start() {
        console.debug("FileLog.start()")

        context.openFileOutput(getFilename(), Context.MODE_PRIVATE)
        writer = OutputStreamWriter(context.openFileOutput(getFilename(), Context.MODE_PRIVATE))

        writeHeader()
        streams.forEach { flux ->
            subscriptions.add(
                flux.getStream().subscribe {
                    addValue(it)
                }
            )
        }
    }

    /**
     * @return String "20230805_1545_datalog.csv"
     */
    private fun getFilename(): String {
        val date = LocalDateTime.now()
        val month = date.monthValue.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val hour = date.hour.toString().padStart(2, '0')
        val minute = date.minute.toString().padStart(2, '0')
        val second = date.second.toString().padStart(2, '0')
        return "${date.year}$month${day}_$hour$minute${second}_datalog.csv"
    }

    private fun addValue(value: UByte) {
        currentLine += "$value,"
        val count = currentLine.count { it == ',' }
        if (count == streams.size) {
            currentLine = "${Instant.now().epochSecond},$currentLine"
            currentLine = currentLine.dropLast(1)
            writeCurrentLine()
        }
    }

    private fun writeHeader() {
        currentLine = "timestamp,"
        streams.forEach {
            //Header
            currentLine += it.name + ","
        }
        currentLine = currentLine.dropLast(1)
        writeCurrentLine()
    }

    private fun writeCurrentLine() {
        console.debug("FileLog.writeCurrentLine($currentLine)")
        writer.write("$currentLine\n")
        currentLine = ""
    }

    fun stop() {
        console.debug("FileLog.stop()")
        subscriptions.forEach {
            it.dispose()
        }
        subscriptions.clear()
        writer.close()
    }
}