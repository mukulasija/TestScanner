package com.team.testscanner.ui.fragments


import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
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
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.team.testscanner.models.HighStart
import com.team.testscanner.models.MyMap
import com.team.testscanner.ui.activities.CameraActivity
import com.team.testscanner.ui.activities.MainActivity
import com.team.testscanner.ui.activities.PreviewActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Properties


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val REQUEST_CODE_CAMERA_PERMISSION = 1001

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTestIntro.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTestIntro(var classroomId : String,var studentId : String) : Fragment() {
    private lateinit var response : JSONObject
    private lateinit var galleryButton: Button
    private var imageUris: MutableList<Uri> = mutableListOf()
    private var imageUrls : MutableList<String> = mutableListOf()
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null
    private val imageFiles = mutableListOf<File>()
    private lateinit var viewFinder:TextureView
    private lateinit var etTitle : TextInputEditText
    private lateinit var timePicker : TimePicker
    private lateinit var radioGroup: RadioGroup
    private lateinit var loadingPB: ProgressBar
    private lateinit var submitButton : Button
    private lateinit var submitBtnHighAccuracy : Button
    private lateinit var cameraButton : Button
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
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris != null && uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                imageUris.addAll(uris)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    private fun updateSelectedImageCount():Int {
        return imageUris.size
    }

    // Add this in CreateTestIntro class
    private val startCameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.getStringExtra("imageUri")
                imageUri?.let {
                    // Handle the received image URI
                    imageUris.add(Uri.parse(it))
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_test_intro, container, false)
        viewFinder=view.findViewById(R.id.viewFinder)
        galleryButton = view.findViewById(R.id.button_gallery)
        timePicker = view.findViewById(R.id.timer_picker)
        setinitTime(timePicker)
        radioGroup = view.findViewById(R.id.choose_question_format)

        galleryButton.setOnClickListener {
            Log.i("TAG", "CLICKING ON THE BUTTON")
            openGallery()
            Log.d("PhotoPicker", "image count: ${imageUris.size}")
        }

        cameraButton = view.findViewById(R.id.button_camera)
        cameraButton.setOnClickListener {
            val intent = Intent(context, CameraActivity::class.java)
            startCameraActivity.launch(intent)
        }

        outputDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (checkSelfPermission(requireContext(),CAMERA)== PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        loadingPB = view.findViewById(R.id.idPBCreateLoading)
        etTitle =view.findViewById(R.id.edit_test_title)
        submitButton =view.findViewById(R.id.button_submit_test_intro)
        submitBtnHighAccuracy = view.findViewById(R.id.btn_submit_high_accuracy)
        submitBtnHighAccuracy.setOnClickListener {
            if(!validateFields())
                return@setOnClickListener
//            upload(imageUris)
            showProgressBar(true)
            genHighAccuracy(requireContext(),1)
        }
        submitButton.setOnClickListener {
            if(!validateFields())
                return@setOnClickListener
//            upload(imageUris)
            showProgressBar(true)
            genHighAccuracy(requireContext(),0)
//            genHighAccuracy(requireContext())
//            generate(requireContext())
            return@setOnClickListener
//            val fragmentManager = requireActivity().supportFragmentManager
//            val fragment = HomeFragment()
//            val transaction = fragmentManager.beginTransaction()
//            transaction.replace(R.id.my_fragment, fragment)
//            transaction.commit()
        }
        return view
    }

    fun getSelectedFormat(): String {
        val selectedId = radioGroup.checkedRadioButtonId
        return when (selectedId) {
            R.id.radio_numeric -> "1"
            R.id.radio_Q -> "Q"
            else -> "" // Return empty string if no radio button is selected
        }
    }


    private fun validateFields(): Boolean {
        if(imageUris.size==0){
            Toast.makeText(requireContext(),"Please Select Atleast one image",Toast.LENGTH_SHORT).show()
            return false
        }
        if(getTimePickerTime()==0L){
            Toast.makeText(requireContext(),"Please select Test Duration",Toast.LENGTH_SHORT).show()
            return false
        }
        if(etTitle.text!!.isEmpty()){
            Toast.makeText(requireContext(),"Test Title is Required",Toast.LENGTH_SHORT).show()
            return false
        }
        if(getSelectedFormat()==""){
            Toast.makeText(requireContext(),"Select Question Type",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getTimePickerTime() : Long{
        val durationInMillis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((timePicker.hour)*60+ timePicker.minute)*60*1000
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        return durationInMillis.toLong()
    }

    private fun setinitTime(timePicker: TimePicker) {
        timePicker.setIs24HourView(true)
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val time = LocalTime.parse("00:00", formatter)
        timePicker.hour=time.hour
        timePicker.minute=time.minute
    }

    private fun takePhoto() {
        // Create output file for the image
        val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${error.message}", error)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    imageFiles.add(photoFile)

                    // Display a thumbnail of the captured image
//                    thumbnail_image.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
                }
            }
        )
    }
    private fun showProgressBar(bol : Boolean){
        if(bol){
            submitButton.visibility=View.GONE
            loadingPB.visibility=View.VISIBLE
        }
        else{
            submitButton.visibility=View.GONE
            loadingPB.visibility=View.GONE
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Camera provider is now guaranteed to be available
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

           //  Set up preview use case
         //   val preview = Preview?.setSurfaceProvider(viewFinder.surfaceProvider)


            // Set up image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            Log.i("TAG", "ERROR FOUND")
            // Set up image analysis use case
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), { image ->
                        // Do something with the image analysis results
                        image.close()
                    })
                }

            // Bind all use cases to the camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind any previously bound use cases
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector,imageCapture, imageAnalyzer
                )

            } catch (ex: Exception) {
                Log.e(TAG, "Error binding camera use cases", ex)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
//    }



//    fun processImages(context: Context, uris: List<Uri>) {
//        for (uri in uris) {
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            val YOUR_IMAGE_CONTENT = bitmapToBase64(bitmap)
//            val response = getJsonResponse(YOUR_IMAGE_CONTENT)
//            inputStream?.close()
//            ResponseManipulator(requireContext(), response = response)
//        }
//    }
//    private fun generateTest() {
//        processImages(requireContext(),getSelectedImages())
//    }

    private fun addQuizToFireStore(quiz: Quiz,questions: MutableMap<String,Question>) {
        quiz.questions= questions
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.get().addOnSuccessListener { querySnapshot ->
            val numDocuments = querySnapshot.size()
            val newDocumentNumber = numDocuments + 1
            quiz.id = "quiz$newDocumentNumber"
            val newQuizRef = collectionRef.document("quiz$newDocumentNumber")
            newQuizRef.set(quiz)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Test Created Successfully",Toast.LENGTH_SHORT).show()
                    showProgressBar(false)
                    val intent = Intent(context,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }.addOnFailureListener{
                    showProgressBar(false)
                    Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { exception ->
            showProgressBar(false)
            println("Error getting number of documents in collection: $exception")
        }
    }

    private fun openGallery() {
        if (isPermissionGranted()) {
            Log.i("TAG", "opening gallery")
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = PICK_IMAGES
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
           // galleryLauncher.launch("image/*")

             pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

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
        private val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            CAMERA,
            WRITE_EXTERNAL_STORAGE
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
    fun upload(imageUris : MutableList<Uri>){
        val numRequests : Int = imageUris.size
        for(imageUri in imageUris){
            val storageRef = FirebaseStorage.getInstance().reference.child("images")
            if (imageUri != null) {
                val imageName = imageUri.lastPathSegment
                val imageRef = storageRef.child(imageName!!)
                // Upload the image file to Firebase Storage
                imageRef.putFile(imageUri).addOnSuccessListener {
                    // Get the download URL of the image file
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        Log.d("upload","success")
                        // Store the download URL in Firestore\

                        Toast.makeText(context,"done",Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    // Handle the upload error
                    Log.e(TAG, "Upload failed: ", e)
                    Toast.makeText(context,"failed",Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    private fun readApiKey(): String {
        val configFile = "config.properties"
        val properties = Properties()

        try {
            val inputStream = FileInputStream(configFile)
            properties.load(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            // Handle exceptions
        }

        return properties.getProperty("API_KEY", "")
    }
    private fun genHighAccuracy(context: Context,mode : Int){
        MyMap.myMap.clear()
//        val key = readApiKey()
        val url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyB-5RM3Pao77g8aWGj5BmieRuipY6J8ST0"
//        val url = "https://vision.googleapis.com/v1/images:annotate?key={$key}"
        val queue= Volley.newRequestQueue(this.context)
        val numRequests : Int = imageUris.size
        val quiz = Quiz()
        val questionType = getSelectedFormat()
//        val highMap = mutableMapOf<String,List<HighStart>>()
        quiz.duration= getTimePickerTime()
        quiz.title = etTitle.text.toString()
//        var questions : MutableMap<String,Question> = mutableMapOf()
//        quiz.questions = questions
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
                    val highStartList = ResponseManipulator(requireContext(),response,YOUR_IMAGE_CONTENT).highAccuracy(questionType)
//                    val questionlist = ResponseManipulator(requireContext(),response,YOUR_IMAGE_CONTENT).main(questionType)
//                    val questionlist = ResponseManipulator(requireContext(),response,uri).getgetquestionlist()
//                    questions.addAllQuestions(questionlist)
                    if(numRequests==numResponse){
//                        Toast.makeText(context,"$numRequests",Toast.LENGTH_SHORT).show()
                        showPreview(quiz,mode)
//                        quiz.questions= questions
//                        addQuizToFireStore(quiz,questions)
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

    private fun showPreview(quiz: Quiz,mode: Int) {
        val intent = Intent(context,PreviewActivity::class.java)
//        val json  = Gson().toJson(quiz)
        intent.putExtra("quizTitle",quiz.title)
        intent.putExtra("quizDuration",quiz.duration)
        intent.putExtra("previewMode",mode)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun generate(context: Context){
        val url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDZjEOYn_0CMi23uO29JLhThjATi8Qo5MI"
        val queue= Volley.newRequestQueue(this.context)
        val numRequests : Int = imageUris.size
        val quiz = Quiz()
        val questionType = getSelectedFormat()
        quiz.duration= getTimePickerTime()
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
                    val questionlist = ResponseManipulator(requireContext(),response,YOUR_IMAGE_CONTENT).main(questionType)
//                    val questionlist = ResponseManipulator(requireContext(),response,uri).getgetquestionlist()
                    questions.addAllQuestions(questionlist)
                    if(numRequests==numResponse){
//                        Toast.makeText(context,"$numRequests",Toast.LENGTH_SHORT).show()
                        quiz.questions= questions
                        addQuizToFireStore(quiz,questions)
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
//                questionlist = ResponseManipulator(requireContext(),response).getgetquestionlist()
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
            Method.POST,
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

    private fun openCamera() {
        // Check if the camera permission is granted
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            // Open camera here
            takePhoto()
        } else {
            // Request camera permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(CAMERA),
                REQUEST_CODE_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            // Handle permission denial
            Toast.makeText(
                requireContext(),
                "Camera permission is required to take photos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

fun MutableMap<String, Question>.addAllQuestions(questions: MutableList<Question>) {
    val nextKey = (this.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0) + 1
    for ((index, question) in questions.withIndex()) {
        this[(nextKey + index).toString()] = question
    }
}
