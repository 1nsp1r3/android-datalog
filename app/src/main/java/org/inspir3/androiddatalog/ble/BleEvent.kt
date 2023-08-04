package org.inspir3.androiddatalog.ble

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import org.inspir3.androiddatalog.Console
import java.util.UUID

class BleEvent(
    private val console: Console,
) : ScanCallback() {
    private val parcelUuid = ParcelUuid(UUID.fromString("00001809-0000-1000-8000-00805f9b34fb"))

    private val temperature: Subject<UByte> = PublishSubject.create()
    private val pressure: Subject<UByte> = PublishSubject.create()

    fun getTemperature(): Subject<UByte> = temperature
    fun getPressure(): Subject<UByte> = pressure

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        console.debug("BleEvent.onBatchScanResults()")
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        /*
        val sid = result.advertisingSid
        val address = result.device.address
        val name = result.device.name
        console.debug("BleEvent.onScanResult(SID: $sid Address: $address Name: $name)")
        */

        val serviceData = result.scanRecord?.serviceData
        val byteArray = serviceData?.get(parcelUuid)
        val rawTemperature = (byteArray?.get(0)?.toUByte() ?: 0) as UByte
        val rawPressure = (byteArray?.get(1)?.toUByte() ?: 0) as UByte

        temperature.onNext(rawTemperature)
        pressure.onNext(rawPressure)

        console.debug("rawTemperature: $rawTemperature / rawPressure: $rawPressure")
    }

    override fun onScanFailed(errorCode: Int) {
        console.debug("BleEvent.onScanFailed()")
    }
}
