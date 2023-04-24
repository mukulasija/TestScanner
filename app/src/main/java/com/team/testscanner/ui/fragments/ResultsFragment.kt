package com.team.testscanner.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.adapters.MyAdapter
import com.team.testscanner.adapters.ResultsAdapter
import com.team.testscanner.models.Quiz

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firestore : FirebaseFirestore
    private var quizList = mutableListOf<Quiz>()
    private lateinit var adapter: ResultsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ResultsAdapter(requireContext(),quizList)
        setUpFireStore()
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view : View){
        val layoutManager= LinearLayoutManager(context)
        val recyclerView: RecyclerView = view.findViewById(R.id.results_recyclerView)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter
    }

    private fun setUpFireStore() {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("quizzes").whereEqualTo("attempted",true)
        collectionReference.addSnapshotListener { value, error ->
            if(value == null || error != null){
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
//            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            adapter.notifyDataSetChanged()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}