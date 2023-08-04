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
        val textViewTemperature: TextView = findViewById(R.id.textViewTemperature)
        val textViewPressure: TextView = findViewById(R.id.textViewPressure)
        val textViewConsole: TextView = findViewById(R.id.textViewConsole)
        textViewConsole.movementMethod = ScrollingMovementMethod()

        val console = Console(textViewConsole)
        val ble = Ble(this, console)

        ble.getTemperature().subscribe {
            textViewTemperature.text = "$it °C"
        }

        ble.getPressure().subscribe {
            textViewPressure.text = "$it PSI"
        }

        console.debug("Bonjour :-)")

        try {
            //ButtonScan
            var scanning = false
            buttonScan.setOnClickListener {
                scanning = scanning.not()
                if (scanning) {
                    buttonScan.text = "Stop scan"
                    ble.startScan()
                } else {
                    ble.stopScan()
                    buttonScan.text = "Scan"
                }
            }
        } catch (exception: Exception) {
            console.error(exception.message ?: "")
        }
    }
}
