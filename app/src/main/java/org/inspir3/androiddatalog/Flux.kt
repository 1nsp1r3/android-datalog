package org.inspir3.androiddatalog

import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class Flux(
    val index: Int,
    val name: String,
    val unit: String,
) {
    private val stream: Subject<UByte> = PublishSubject.create()

    fun getStream(): Subject<UByte> = this.stream
}
