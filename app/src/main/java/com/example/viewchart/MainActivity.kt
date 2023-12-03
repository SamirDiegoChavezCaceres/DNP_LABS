package com.example.viewchart
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.util.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val barChart = findViewById<BarChart>(R.id.barChart)
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        findViewById<Button>(R.id.btnBar).setOnClickListener {
            barChart.visibility = View.VISIBLE
            pieChart.visibility = View.INVISIBLE
            barChart.submitList(
                barChartList = generateRandomBars(5),
            )
            barChart.setBorder(
                borderSize = 5F,
                borderColor = Color.LTGRAY,
            )
        }

        findViewById<Button>(R.id.btnPie).setOnClickListener {
            pieChart.visibility = View.VISIBLE
            barChart.visibility = View.INVISIBLE
            pieChart.submitList(
                pieChartList = generateRandomSlices(5),
            )
            pieChart.setBorder(
                borderSize = 5f,
                borderColor = Color.LTGRAY,
            )
        }
    }
    fun generateRandomSlices(numSlices: Int): ArrayList<Slice> {
        val random = Random()
        val totalPercentage = 100f

        val sliceList = ArrayList<Slice>()

        for (i in 0 until numSlices - 1) {
            val randomPercentage = 1f + random.nextFloat() * (totalPercentage - sliceList.sumByDouble { it.percentage.toDouble() }).toFloat()

            // Generar colores RGB aleatorios
            val randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))

            sliceList.add(Slice(randomPercentage, randomColor))
        }

        // El último slice toma el porcentaje restante para asegurarse de que sumen 100%
        val remainingPercentage = totalPercentage - sliceList.sumByDouble { it.percentage.toDouble() }

        // Generar color RGB aleatorio para el último slice
        val randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        sliceList.add(Slice(remainingPercentage.toFloat(), randomColor))

        // Mezclar la lista para que los colores no estén en un orden específico
        sliceList.shuffle()

        return sliceList
    }

    fun generateRandomBars(numBars: Int): ArrayList<Bar> {
        val random = Random()

        val barList = ArrayList<Bar>()

        for (i in 0 until numBars) {
            val randomHeight = random.nextFloat() * 100f // Altura aleatoria entre 0 y 100
            val randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))

            barList.add(Bar(randomHeight, randomColor))
        }

        return barList
    }
}