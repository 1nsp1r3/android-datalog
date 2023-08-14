package org.inspir3.androiddatalog

import android.util.Log
import io.reactivex.rxjava3.disposables.Disposable
import java.time.LocalDateTime

class FileLog {
    private val streams: MutableList<Flux> = mutableListOf()
    private val subscriptions: MutableList<Disposable> = mutableListOf()

    private val textFile = TextFile()

    private var maxIndex: Int = -1
    private var currentLine: String = ""
    private var nbBufferedLines: Int = 0

    fun addFlux(stream: Flux): Boolean {
        if (stream.index > maxIndex) maxIndex = stream.index
        return this.streams.add(stream)
    }

    fun start() {
        Log.i("Ble", "FileLog.start()")

        textFile.open(
            filename = getFilename(),
        )

        textFile.println(
            text = generateHeader()
        )

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
            currentLine = "${getDateTime()},$currentLine"
            currentLine = currentLine.dropLast(1)
            textFile.println(currentLine)
            currentLine = ""
            nbBufferedLines++
            if (nbBufferedLines == 5) {
                textFile.flush()
                nbBufferedLines = 0
            }
        }
    }

    private fun generateHeader(): String {
        var ret = "Time,"
        streams.forEach {
            //Header
            ret += it.name + ","
        }
        return ret.dropLast(1)
    }

    private fun getDateTime(): String {
        val date = LocalDateTime.now()
        val month = date.monthValue.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val hour = date.hour.toString().padStart(2, '0')
        val minute = date.minute.toString().padStart(2, '0')
        val second = date.second.toString().padStart(2, '0')
        return "${day}/$month/${date.year} $hour:$minute:${second}"
    }

    fun stop() {
        Log.i("Ble", "FileLog.stop()")
        subscriptions.forEach {
            it.dispose()
        }
        subscriptions.clear()
        textFile.close()
    }
}