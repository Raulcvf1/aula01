package com.example.aula01

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null

    private var passos = 0
    private var altura = 0
    private var peso = 0

    private lateinit var etxtAltura: EditText
    private lateinit var etxtPeso: EditText
    private lateinit var btnEnviar: Button
    private lateinit var txtCalorias: TextView
    private lateinit var txtHistorico: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etxtAltura = findViewById(R.id.etxt_altura)
        etxtPeso = findViewById(R.id.etxt_peso)
        btnEnviar = findViewById(R.id.btn_enviar)
        txtCalorias = findViewById(R.id.txt_calorias)
        txtHistorico = findViewById(R.id.txt_historico)

        sharedPreferences = getSharedPreferences("calories_history", Context.MODE_PRIVATE)

        // Exibir o histórico salvo
        txtHistorico.text = sharedPreferences.getString("historico", "")

        btnEnviar.setOnClickListener {
            val s_Altura = etxtAltura.text.toString()
            val s_Peso = etxtPeso.text.toString()

            if (s_Altura.isNotEmpty() && s_Peso.isNotEmpty()) {
                altura = s_Altura.toInt()
                peso = s_Peso.toInt()

                calcularCalorias()
                salvarCaloriasNoHistorico()
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroscopeSensor == null) {
            // Se o giroscópio não estiver disponível no dispositivo
            // Faça o tratamento adequado
        }
    }

    private fun calcularCalorias() {
        if (peso != 0) {
            val passosPeso = ((2700 * 70) / peso) / 100
            val calorias = passos / passosPeso
            txtCalorias.text = calorias.toString()
        } else {
            txtCalorias.text = "Peso inválido"
        }
    }

    private fun salvarCaloriasNoHistorico() {
        val historico = sharedPreferences.getString("historico", "") ?: ""
        val novoRegistro = "$passos passos, $altura cm, $peso kg\n"
        val novoHistorico = historico + novoRegistro
        sharedPreferences.edit().putString("historico", novoHistorico).apply()

        // Atualizar o TextView com o novo histórico
        txtHistorico.text = novoHistorico
    }

    override fun onResume() {
        super.onResume()
        gyroscopeSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val y = event.values[1]
            val limiar = 1
            // Faça o que quiser com os dados do giroscópio

            if (y > limiar) { // limiar é o valor limite para considerar um passo
                passos++
                calcularCalorias()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Se a precisão do sensor mudar, este método será chamado.

    }
}