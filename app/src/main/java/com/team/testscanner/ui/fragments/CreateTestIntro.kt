package com.team.testscanner.ui.fragments


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.team.testscanner.R
import com.team.testscanner.ui.ResponseManipulator
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var response : JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTestIntro.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTestIntro : Fragment() {
    private val PICK_IMAGES = "image/*"
    private lateinit var galleryButton: Button
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1
    private var imageUris: MutableList<Uri> = mutableListOf()
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
        return view
    }

    fun processImages(context: Context, uris: List<Uri>) {
        for (uri in uris) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val YOUR_IMAGE_CONTENT = bitmapToBase64(bitmap)
            getStringResponse(YOUR_IMAGE_CONTENT)
            inputStream?.close()
        }
    }

    private fun openGallery() {
        if (isPermissionGranted()) {
            Log.i("TAG", "opening gallery")
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = PICK_IMAGES
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryLauncher.launch("image/*")
            processImages(requireContext(),imageUris)
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
    private fun getJsonResponse(YOUR_IMAGE_CONTENT: String){
        // for a jsonObjectRequest
        val queue = Volley.newRequestQueue(this.context)
        val url =
            "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDZjEOYn_0CMi23uO29JLhThjATi8Qo5MI"
        val jsonRequest : JSONObject = getJsonImageObject(YOUR_IMAGE_CONTENT)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonRequest,
            Response.Listener { response ->
                // Handle the response here
                Log.d("visionApi",response.toString())
                ResponseManipulator(requireContext(),response).getFirstLetter()
            },
            Response.ErrorListener { error ->
                // Handle the error here
                error
            }) {

            // Override the getHeaders() method to add custom headers to the request
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
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
                ResponseManipulator(requireContext(),JSONObject(response)).getFirstLetter()
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