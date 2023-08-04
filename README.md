## Espruino test program
```js
let rawTemperature = 0
let rawPressure = 255


function loop(){
  rawTemperature++
  if (rawTemperature == 256) rawTemperature = 0

  rawPressure--
  if (rawPressure == 0) rawTemperature = 255


  NRF.setAdvertising({ //data
    0x1809: [Math.round(rawTemperature), Math.round(rawPressure)], //[UByte, UByte]
  }
  ,{ //options
    name: 'MX5',
    interval: 1000, //ms
  })
}

setInterval(loop, 1000)
loop()
```