package org.inspir3.androiddatalog.ble

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import io.reactivex.rxjava3.subjects.Subject
import org.inspir3.androiddatalog.Console

class Ble(
    private val context: Context,
    private val console: Console,
) {
    private val bleEvent = BleEvent(console)

    fun getTemperature(): Subject<UByte> = bleEvent.getTemperature()
    fun getPressure(): Subject<UByte> = bleEvent.getPressure()

    private fun getBluetoothLeScanner(): BluetoothLeScanner {
        console.debug("Ble.getBluetoothLeScanner()")
        val bluetoothManager = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?) ?: throw Exception("Unable to retrieve BluetoothManager")
        return bluetoothManager.adapter.bluetoothLeScanner
    }

    private fun filterByName(name: String): ScanFilter = ScanFilter.Builder().setDeviceName(name).build()

    private fun getScanSettings() = ScanSettings.Builder()
        .setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build()

    fun startScan() {
        console.debug("Ble.startScan()")

        /**
         * La permission de la "Localisation" doit être accordée à l'application
         * Nécessite dans le manifest :
         * - android.permission.BLUETOOTH
         * - android.permission.BLUETOOTH_ADMIN
         * - android.permission.ACCESS_FINE_LOCATION
         */
        this.getBluetoothLeScanner().startScan(
            listOf(
                filterByName("MX5"),
            ),
            getScanSettings(),
            bleEvent,
        )
    }

    fun stopScan() {
        console.debug("Ble.stopScan()")
        this.getBluetoothLeScanner().stopScan(bleEvent)
    }
}
