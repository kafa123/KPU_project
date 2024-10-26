package com.example.kpu

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.kpu.databinding.FragmentFormBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var databaseHelper: DatabaseHelper
    private var LOCATION_PERMISSION_REQUEST_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        val view = binding.root

        databaseHelper = DatabaseHelper(requireContext())

        // Date picker dialog for the date field
        binding.DatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                binding.DatePicker.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)
            datePickerDialog.show()
        }

        setupLaunchers()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.AddressCheck.setOnClickListener {
            getCurrentLocation()
        }

        // Set click listener for cardImage to open the image source dialog
        binding.cardImage.setOnClickListener {
            showImageSourceDialog()  // This will show the dialog when the cardImage is clicked
        }

        // Handle form submission
        binding.btnSubmit.setOnClickListener {
            saveData()
        }

        return view
    }

    private fun setupLaunchers() {

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .into(binding.image)

                }
            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(binding.image)
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image Source")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
        builder.show()
    }

    private fun openCamera() {
        // Create a file to store the captured image
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )
        cameraLauncher.launch(imageUri!!)
    }

    private fun openGallery() {
        // Open the gallery to pick an image
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        // Create an image file in the app's private storage
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp.jpg"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, fileName)
    }

    private fun saveData(){
        with(binding){

            val name = textNama.text.toString()
            val nik = textNik.text.toString()
            val noHp = textNoHP.text.toString()
            val gender = if (radioMale.isChecked) "Male" else "Female"
            val birthDate = DatePicker.text.toString()
            val address = textAlamat.text.toString()

            val imageBitmap = (binding.image.drawable as? BitmapDrawable)?.bitmap
            var imagePath: String? = null
            var imageByteArray: ByteArray? = null

            imageBitmap?.let { bitmap ->
                val file = createImageFile()
                val outStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.flush()
                outStream.close()
                imagePath = file.absolutePath
            }

            // Save the data in the database (simplified example)
            val isSuccess = databaseHelper.insertData(name, nik, noHp, gender, birthDate, address, imagePath.toString())
            if (isSuccess) {
                Toast.makeText(requireContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show()

                // Clear the form fields
                textNama.text?.clear()
                textNik.text?.clear()
                textNoHP.text?.clear()
                DatePicker.setText("")
                textAlamat.text?.clear()
                image.setImageResource(R.drawable.logo)  // Reset image view to placeholder

                // Reset radio button selection
                radioMale.isChecked = false
                radioFemale.isChecked = false
            } else {
                Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
            }
            }
    }

    private fun getAddressInfo(latitude:Double, longitude:Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        val address: String = addresses!![0].getAddressLine(0)
        binding.textAlamat.setText(address)
    }

    private fun getCurrentLocation() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Permissions granted, get the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getAddressInfo(location.latitude,location.longitude)
                } else {
                    Log.e("CurrentLocation", "Location not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CurrentLocation", "Failed to get location: ${e.message}")
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
