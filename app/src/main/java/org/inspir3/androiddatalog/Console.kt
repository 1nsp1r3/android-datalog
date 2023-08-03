package org.inspir3.androiddatalog

import android.widget.TextView

class Console(
    private val textView: TextView,
) {
    private fun println(level: String, text: String) {
        textView.append("[$level] $text\n")
    }

    fun debug(text: String) {
        println("DEBUG", text)
    }

    fun error(text: String) {
        println("ERROR", text)
    }
}