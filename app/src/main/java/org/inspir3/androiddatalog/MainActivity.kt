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
        val textView: TextView = findViewById(R.id.textView)
        textView.movementMethod = ScrollingMovementMethod()

        val console = Console(textView)
        val ble = Ble(this, console)

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
