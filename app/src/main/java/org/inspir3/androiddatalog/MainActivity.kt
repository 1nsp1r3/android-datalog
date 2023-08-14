package org.inspir3.androiddatalog

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.inspir3.androiddatalog.ble.Ble

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        val buttonScan: Button = findViewById(R.id.buttonScan)
        val textViewConsole: TextView = findViewById(R.id.textViewConsole)
        textViewConsole.movementMethod = ScrollingMovementMethod()

        val console = Console(textViewConsole)
        val ble = Ble(this, console)
        val fileLog = FileLog()

        val temperature = Flux(
            index = 0,
            name = "Temperature",
            unit = "°C",
        )

        val pressure = Flux(
            index = 1,
            name = "Pressure",
            unit = "PSI",
        )

        //Temperature
        ble.addFlux(temperature)
        displayFlux(temperature)
        fileLog.addFlux(temperature)

        //Pressure
        ble.addFlux(pressure)
        displayFlux(pressure)
        fileLog.addFlux(pressure)

        console.debug("Bonjour :-)")
        try {
            //ButtonScan
            var scanning = false
            buttonScan.setOnClickListener {
                scanning = scanning.not()
                if (scanning) {
                    buttonScan.text = "Stop scan"
                    ble.startScan()
                    fileLog.start()
                } else {
                    ble.stopScan()
                    buttonScan.text = "Scan"
                    fileLog.stop()
                }
            }
        } catch (exception: Exception) {
            console.error(exception.message ?: "")
        }
    }

    private fun displayFlux(flux: Flux) {
        val res = resources
        val id = res.getIdentifier("textView${flux.name}", "id", "org.inspir3.androiddatalog")
        val textView: TextView = findViewById(id)
        flux.getStream().subscribe {
            textView.text = "$it ${flux.unit}"
        }
    }
}
