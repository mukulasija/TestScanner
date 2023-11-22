package com.team.testscanner.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.models.Quiz
import com.team.testscanner.adapters.MyAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var quizList: ArrayList<Quiz> =arrayListOf<Quiz>()
    private var quizList = mutableListOf<Quiz>()
    private lateinit var adapter: MyAdapter
    private lateinit var firestore : FirebaseFirestore
    private lateinit var homeRecyclerView : RecyclerView
    private lateinit var loadingPB: ProgressBar
//    lateinit var textTitle:Array<String>
//    lateinit var textDesc:Array<String>
//    lateinit var testData:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingPB = view.findViewById(R.id.idPBLoading)
        loadingPB.visibility= View.VISIBLE
        homeRecyclerView = view.findViewById(R.id.home_recyclerView)
//        dataInitilizate()    //dummy data  //to remove it after
        adapter = MyAdapter(requireContext(),quizList,"student","")
        setUpRecyclerView(view)
        setUpFireStore()
    }

    private fun setUpRecyclerView(view : View){
        val layoutManager=LinearLayoutManager(context)
        val recyclerView: RecyclerView = view.findViewById(R.id.home_recyclerView)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_home, container, false)
    }

    fun dataInitilizate(){

//        textTitle= arrayOf(
//            getString(R.string.demo_1),
//            getString(R.string.demo_2)
//        )
//        textDesc=arrayOf(
//            g getString(R.string.demo_1),
//            getString(R.string.demo_desc_2)
//        )
        val quiz = Quiz("1","title")
//        val test=TestData( getString(R.string.demo_1), getString(R.string.demo_1))
        quizList.add(quiz)
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("quizzes").document("quizzes").set(quizList[0])

    }

    // firestore initialization
    private fun setUpFireStore() {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("quizzes")
        collectionReference.addSnapshotListener { value, error ->
            if(value == null || error != null){
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
         //   Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            adapter.notifyDataSetChanged()
            loadingPB.visibility=View.GONE
            homeRecyclerView.visibility=View.VISIBLE
        }
    }

//    private fun setUpFireStore() {
//        firestore = FirebaseFirestore.getInstance()
//        val collectionReference = firestore.collection("quizzes")
//        collectionReference.addSnapshotListener { value, error ->
//            if(value == null || error != null){
//
//                Toast.makeText(requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
//                return@addSnapshotListener
//            }
//            Log.d("mukulfirebase",value.toString())
////            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
////            quizList.clear()
////            quizList.addAll(value.toObjects(Quiz::class.java))
////            adapter.notifyDataSetChanged()
//        }
//    }
     fun addData(string1:String,string2:String){
//         val test=TestData( string1, string2)
//         quizList.add(test)
         adapter.notifyDataSetChanged()
     }









    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}