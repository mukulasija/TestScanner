package com.team.testscanner.ui.fragments


import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.team.testscanner.R

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
    private val PICK_IMAGES = "image/*"
    private lateinit var galleryButton: Button
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1
    private var imageUris: MutableList<Uri> = mutableListOf()

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
        val textViewTitle=view.findViewById<TextView>(R.id.edit_test_title)
        val textViewDesc=view.findViewById<TextView>(R.id.edit_test_description)
        val submitButton:Button=view.findViewById(R.id.button_submit_test_intro)
        submitButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = HomeFragment()
            fragment.addData(textViewTitle.text.toString(),textViewDesc.text.toString())
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.my_fragment, fragment)
            transaction.commit()
        }
        return view
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
}