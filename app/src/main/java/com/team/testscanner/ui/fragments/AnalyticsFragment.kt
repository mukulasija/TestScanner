package com.team.testscanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.models.Quiz
import javax.xml.datatype.DatatypeConstants.DAYS
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnalyticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalyticsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore : FirebaseFirestore
    private var quizList = mutableListOf<Quiz>()
    private var scoreList = mutableListOf<Int>()
    private lateinit var mBarChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_analytics, container, false)
        mBarChart = view.findViewById<BarChart>(R.id.barChart)
////        val entries: ArrayList<BarEntry> = ArrayList()
////        for (i in 0 until scoreList.size) {
////            val barEntry = BarEntry((i + 1).toFloat(), scoreList[i].toFloat())
////            entries.add(barEntry)
////        }
////        val barDataSet = BarDataSet(entries, "Months")
//        val data = createChartData(scoreList)
//        barDataSet.formSize = 15f
//        barDataSet.setDrawValues(false)
//        barDataSet.valueTextSize = 12f
//
//        //set the BarData to chart
//        val xAxis = mBarChart.xAxis
//        val leftAxis = mBarChart.axisLeft
//        leftAxis.axisMinimum = 0f
//        leftAxis.setDrawAxisLine(true)
//        leftAxis.setLabelCount(0, true)
//
//        //set the BarData to chart
//        val data = BarData(barDataSet)
//        mBarChart.data = data
//        mBarChart.setScaleEnabled(false)
//        mBarChart.legend.isEnabled = false
//        mBarChart.setDrawBarShadow(false)
//        mBarChart.description.isEnabled = false
//        mBarChart.setPinchZoom(false)
//        mBarChart.setDrawGridBackground(true)
//        mBarChart.invalidate()
//        mBarChart.description.isEnabled = false
//        mBarChart.setDrawValueAboveBar(false)
//        val xAxis: XAxis = mBarChart.xAxis
//        val axisLeft: YAxis = mBarChart.axisLeft
//        axisLeft.granularity = 10f
//        axisLeft.axisMinimum = 0f
//        val axisRight: YAxis = mBarChart.getAxisRight()
//        axisRight.granularity = 10f
//        axisRight.axisMinimum = 0f
//        mBarChart.data = data;
//        mBarChart.invalidate();
        return view
    }

//    private fun configureChartAppearance() {
//        mBarChart.getDescription().setEnabled(false)
//        mBarChart.setDrawValueAboveBar(false)
//        val xAxis: XAxis = chart.getXAxis()
//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return DAYS.get(value.toInt())
//            }
//        }
//        val axisLeft: YAxis = chart.getAxisLeft()
//        axisLeft.granularity = 10f
//        axisLeft.axisMinimum = 0f
//        val axisRight: YAxis = chart.getAxisRight()
//        axisRight.granularity = 10f
//        axisRight.axisMinimum = 0f
//    }

    private fun createChartData(scoreList: MutableList<Int>): BarData? {
        val values: ArrayList<BarEntry> = ArrayList()
        Log.d("DATA", scoreList.size.toString())
        for (i in 0 until scoreList.size) {
            val x = i.toFloat()
            val y: Float = scoreList[i].toFloat()
            values.add(BarEntry(x, y))
        }
        val set1 = BarDataSet(values, "Marks")
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        return BarData(dataSets)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFireStore()
        setupScore()
        mBarChart = view.findViewById(R.id.barChart)

    }

    private fun setupScore() {
        Log.d("quizlist", quizList.size.toString())
        for(quiz in quizList){
            Log.d("quizscore","${quiz.score} + ${quiz.title}")
            scoreList.add(quiz.score)
        }
        createChart()
    }
    private fun createChart(){
        val data = createChartData(scoreList)
        mBarChart.description.isEnabled = false
        mBarChart.setDrawValueAboveBar(false)
        val xAxis: XAxis = mBarChart.xAxis
        val axisLeft: YAxis = mBarChart.axisLeft
        axisLeft.granularity = 10f
        axisLeft.axisMinimum = 0f

        val axisRight: YAxis = mBarChart.axisRight
        axisRight.granularity = 10f
        axisRight.axisMinimum = 0f
        axisRight.axisMaximum=100f
        mBarChart.data = data;
        mBarChart.invalidate();
    }

    private fun setUpFireStore() {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("quizzes").whereEqualTo("keyAvailable",true)
        collectionReference.addSnapshotListener { value, error ->
            if(value == null || error != null){
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
//            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            setupScore()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnalyticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalyticsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}