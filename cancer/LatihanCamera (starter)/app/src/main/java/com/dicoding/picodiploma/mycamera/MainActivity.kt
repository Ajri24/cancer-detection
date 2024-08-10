package com.dicoding.picodiploma.mycamera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.picodiploma.ml.ImageClassifierHelper
import com.dicoding.picodiploma.mycamera.CameraActivity.Companion.CAMERAX_RESULT
import com.dicoding.picodiploma.mycamera.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectedImageUri: Uri

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.cameraXButton.setOnClickListener { startHistory(this) }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }


    private fun analyzeImage(uri: Uri) {
        try {
            // Mengambil gambar dari URI dan mengonversinya ke bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            // Membuat instance ImageClassifierHelper dengan menggunakan context dari Activity atau Fragment
            val imageClassifierHelper = ImageClassifierHelper(0.1f, 3, "cancer_classification.tflite", this, object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    // Kirim hasil ke ResultActivity jika hasil tidak null
                    results?.let {
                        try {
                            val classificationsArray = JSONArray()

                            it.forEachIndexed { index, classification ->
                                // Pilih kategori dengan indeks ke-1
                                val selectedCategory = classification.categories[0] // Memilih indeks 1

                                // Mendapatkan nilai kategori dan skor
                                val categoryName = selectedCategory.label
                                val score = selectedCategory.score

                                Log.d("MainActivitys", "Kategori: $categoryName, Skor: $score")

                                // Membuat objek JSON untuk setiap kategori dan skor
                                val categoryJson = JSONObject()
                                categoryJson.put("kategori", categoryName)
                                categoryJson.put("skor", score)

                                // Tambahkan objek JSON ke array JSON
                                classificationsArray.put(categoryJson)
                            }

                            // Membuat Intent
                            val intent = Intent(this@MainActivity, ResultActivity::class.java)

                            // Menambahkan data ke Intent
                            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
                            intent.putExtra(ResultActivity.EXTRA_RESULT, classificationsArray.toString())

                            // Memulai aktivitas dengan Intent yang sudah disiapkan
                            startActivity(intent)
                        } catch (e: Exception) {
                            showToast("Error creating JSON: ${e.message}")
                        }
                    } ?: run {
                        showToast("Results is null")
                    }
                }




            })

            // Melakukan klasifikasi bitmap
            imageClassifierHelper.classifyBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error analyzing image: ${e.message}")
            showToast("Error analyzing image")
        }
    }


    private val launcherUCrop = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(result.data!!)
            error?.let {
                Log.e("UCrop", "Crop error: $it")
            }
        }
    }

    private fun startUCropActivitys(uri: Uri) {
        // Generate unique filename with timestamp
        val currentTime = System.currentTimeMillis()
        val filename = "cropped_image_$currentTime.jpg"

        // Configure UCrop
        val destinationUri = Uri.fromFile(File(cacheDir, filename))
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(80)
        options.withAspectRatio(16f, 9f)
        options.withMaxResultSize(1000, 1000)

        // Start crop process
        val uCrop = UCrop.of(uri, destinationUri)
            .withOptions(options)
            .getIntent(this@MainActivity)
        launcherUCrop.launch(uCrop)
    }


    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri? = result.data?.data
            selectedImg?.let {
                selectedImageUri = it // Simpan URI gambar yang dipilih
                currentImageUri = it  // Perbarui currentImageUri dengan URI yang dipilih
                loadImage(selectedImageUri) // Tampilkan gambar yang dipilih di ImageView
                startUCropActivitys(it) // Mulai proses crop
            }
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun loadImage(imageUri: Uri) {
        // Load selected image into ImageView
        binding.previewImageView.setImageURI(imageUri)
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            // Setelah gambar diambil, mulai UCropActivity
            currentImageUri?.let { uri ->
                startUCropActivity(uri)
            }
        }
    }


    private fun startUCropActivity(uri: Uri) {
        val currentImageUri = currentImageUri ?: return // Menghentikan proses jika currentImageUri null

// Membuat Intent untuk memulai UCropActivity dengan URI gambar yang diambil
        val uCropIntent = UCrop.of(uri, currentImageUri)
            .withAspectRatio(1.0f, 1.0f) // Contoh: Mengatur rasio aspek menjadi 1:1
            .getIntent(this@MainActivity)

        // Mulai aktivitas UCropActivity
        startUCrop.launch(uCropIntent)
    }

    private val startUCrop = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Tangani hasil dari UCropActivity
            val resultUri = UCrop.getOutput(result.data!!)
            // Tampilkan gambar yang telah dipangkas
            showImage()
        }
    }




    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    fun startHistory(context: Context) {
        val intent = Intent(context, History::class.java)
        context.startActivity(intent)
    }
}
