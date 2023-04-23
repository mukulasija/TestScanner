package com.team.testscanner.ui.fragments


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import com.team.testscanner.other.ResponseManipulator
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTestIntro.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTestIntro : Fragment() {
    private lateinit var response : JSONObject
    private val PICK_IMAGES = "image/*"
    private lateinit var galleryButton: Button
    private var imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var etTitle : TextInputEditText
    private lateinit var etDescription : TextInputEditText
//    private lateinit var fragmentContext: Context
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        fragmentContext = context
//    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris != null && uris.isNotEmpty()) {
                imageUris.addAll(uris)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_test_intro, container, false)
        galleryButton = view.findViewById(R.id.button_gallery)
        galleryButton.setOnClickListener {
            Log.i("TAG", "CLICKING ON THE BUTTON")
            openGallery()
        }
        val cameraButton:Button=view.findViewById(R.id.button_camera)
        cameraButton.setOnClickListener {
            Log.i("TAG", "CLICKING ON THE CAMERA BUTTON")

        }

        etTitle =view.findViewById(R.id.edit_test_title)
        etDescription=view.findViewById(R.id.edit_test_description)
        val submitButton:Button=view.findViewById(R.id.button_submit_test_intro)
        submitButton.setOnClickListener {
            if(imageUris.size==0){
                Toast.makeText(requireContext(),"Please Select Atleast one image",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            generate(requireContext())
            return@setOnClickListener
            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = HomeFragment()
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.my_fragment, fragment)
            transaction.commit()
        }
        return view
    }

    private fun generateTest() {
        processImages(requireContext(),getSelectedImages())
    }

    private fun addQuizToFireStore(quiz: Quiz) {
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.get().addOnSuccessListener { querySnapshot ->
            val numDocuments = querySnapshot.size()
            val newDocumentNumber = numDocuments + 1
            quiz.id = "quiz$newDocumentNumber"
            val newQuizRef = collectionRef.document("quiz$newDocumentNumber")
            newQuizRef.set(quiz)
                .addOnSuccessListener {
//                    Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { exception ->
            println("Error getting number of documents in collection: $exception")
        }
    }

    private fun openGallery() {
        if (isPermissionGranted()) {
            Log.i("TAG", "opening gallery")
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = PICK_IMAGES
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryLauncher.launch("image/*")
        } else {
            requestPermission()
        }
    }



    private fun isPermissionGranted(): Boolean {
            return ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission needed")
                .setMessage("This permission is needed to access the gallery.")
                .setPositiveButton("Ok") { _, _ ->
                    requestPermissionLauncher.launch(
                        READ_EXTERNAL_STORAGE
                    )
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
                .show()
        } else {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Can't access gallery.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    fun getSelectedImages(): List<Uri> {
        return imageUris
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateTestIntro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateTestIntro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
    private fun generate(context: Context){
        val url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDZjEOYn_0CMi23uO29JLhThjATi8Qo5MI"
        val queue= Volley.newRequestQueue(this.context)
        val numRequests : Int = imageUris.size
        val quiz = Quiz()
        quiz.title = etTitle.text.toString()
        var questions : MutableMap<String,Question> = mutableMapOf()
        quiz.questions = questions
        var numResponse = 0
        for(uri in imageUris){
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val YOUR_IMAGE_CONTENT = bitmapToBase64(bitmap)
            val jsonRequest : JSONObject = getJsonImageObject(YOUR_IMAGE_CONTENT)
            val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonRequest,
                Response.Listener { response ->
                    // Handle the response here
                    numResponse++
                    val questionlist = ResponseManipulator(requireContext(),response).getgetquestionlist()
                    questions.addAllQuestions(questionlist)
                    if(numRequests==numResponse){
//                        Toast.makeText(context,"$numRequests",Toast.LENGTH_SHORT).show()
                        quiz.questions= questions
                        addQuizToFireStore(quiz)
                    }
//                    Log.d("visionApi",response.getString("textAnnotations"))
                },
                Response.ErrorListener { error ->
                    // Handle the error here
                    Log.d("visionerror",error.toString())
                    error
                }) {

                // Override the getHeaders() method to add custom headers to the request
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }

    }
    fun processImages(context: Context, uris: List<Uri>) {
        val quiz = Quiz()
        quiz.title = etTitle.text.toString()
        val questions : MutableMap<String,Question> = mutableMapOf()
        for (uri in uris) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val YOUR_IMAGE_CONTENT = bitmapToBase64(bitmap)
            val questionList = getJsonResponse(YOUR_IMAGE_CONTENT)
            inputStream?.close()
//            val questionlist : MutableList<Question> = ResponseManipulator(requireContext(),response = response).getgetquestionlist()
            questions.addAllQuestions(questionList)
        }
        quiz.questions = questions
        addQuizToFireStore(quiz)
    }
    private fun getJsonResponse(YOUR_IMAGE_CONTENT: String) : MutableList<Question>{
        // for a jsonObjectRequest
        var questionlist : MutableList<Question> = mutableListOf()
        response = JSONObject()
        val queue = Volley.newRequestQueue(this.context)
        val url =
            "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDZjEOYn_0CMi23uO29JLhThjATi8Qo5MI"
        val jsonRequest : JSONObject = getJsonImageObject(YOUR_IMAGE_CONTENT)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonRequest,
            Response.Listener { response ->
                // Handle the response here
                this.response = response
                Log.d("visionApi",response.getString("textAnnotations"))
                questionlist = ResponseManipulator(requireContext(),response).getgetquestionlist()
            },
            Response.ErrorListener { error ->
                // Handle the error here
                Log.d("visionerror",error.toString())
                error
            }) {

            // Override the getHeaders() method to add custom headers to the request
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
//        sendData(response.toString())
//        writeStringToFile(requireContext(), response.toString())
//        File("output.txt").writeText(response.toString())
        return questionlist

    }

    private fun sendData(response : JSONObject) {

    }

    //    private fun writeStringToFile(context: Context, response: String) {
//        try {
//            val file = File(context.filesDir, "filename.txt")
//            val writer = FileWriter(file)
//            writer.write(response)
//            writer.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
    fun writeStringToFile(context: Context, response: String) {
        try {
            val file = File(context.filesDir, "filename.txt")
            val writer = FileWriter(file)
            writer.write(response)
            writer.close()

            // Copy the file from internal storage to assets folder
            val inputStream: InputStream = FileInputStream(file)
            val outputStream: OutputStream = context.assets.openFd("filename.txt").createOutputStream()
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun getStringResponse(YOUR_IMAGE_CONTENT: String){
        val queue = Volley.newRequestQueue(requireContext())
        val url =
            "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDZjEOYn_0CMi23uO29JLhThjATi8Qo5MI"
        val jsonRequest : JSONObject = getJsonImageObject(YOUR_IMAGE_CONTENT)
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener { response ->
                // handle successful response
//                ResponseManipulator(requireContext(),JSONObject(response)).getFirstLetter()
                Log.d("visionapi", response)
            },
            Response.ErrorListener { error ->
                // handle error response
                Log.d("visionEroor", error.toString())
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return jsonRequest.toString().toByteArray(Charsets.UTF_8)
            }
        }
        queue.add(request)
    }

    private fun getJsonImageObject(YOUR_IMAGE_CONTENT : String): JSONObject {
        val jsonRequest = JSONObject()
        jsonRequest.put("requests", JSONArray().apply {
            put(JSONObject().apply {
                put("image", JSONObject().apply {
                    put(
                        "content",
                        YOUR_IMAGE_CONTENT
                    ) // replace with your base64-encoded image data
                })
                put("features", JSONArray().apply {
                    put(JSONObject().apply {
                        put("type", "TEXT_DETECTION")
                    })
                })
            })
        })
        return jsonRequest
    }
}

fun MutableMap<String, Question>.addAllQuestions(questions: MutableList<Question>) {
    val nextKey = (this.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0) + 1
    for ((index, question) in questions.withIndex()) {
        this[(nextKey + index).toString()] = question
    }
}