package com.dicoding.picodiploma.mycamera

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.data.response.Responses
import com.dicoding.picodiploma.mycamera.databinding.ActivityResultBinding
import com.example.myapplication.data.retrofit.ApiConfig
import com.example.myapplication.db.RepositoryClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.tensorflow.lite.task.vision.classifier.Classifications
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result_array"
    }

    private lateinit var binding: ActivityResultBinding
    private lateinit var repository: RepositoryClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = RepositoryClass(applicationContext)
        Log.d("ResultActivity", "onCreate called")

        // Get data from intent

        val resultJson = intent.getStringExtra(EXTRA_RESULT)

        resultJson?.let { json ->
            try {
                val classificationsArray = JSONArray(json)
                val stringBuilder = StringBuilder()

                var categoryName = ""
                var score = 0.0

                for (i in 0 until classificationsArray.length()) {
                    val categoryJson = classificationsArray.getJSONObject(i)
                    categoryName = categoryJson.getString("kategori")
                    score = categoryJson.getDouble("skor")

                    stringBuilder.append("Prediksi: $categoryName\n")
                    stringBuilder.append("confidence score: $score\n\n")
                }
                val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
                val imageUri = Uri.parse(imageUriString)
                binding.resultImage.setImageURI(imageUri)
                Log.d("ImageUriString", "Image URI String: $imageUriString")
                binding.resultText2.text = stringBuilder.toString()

                repository.insertHistory(categoryName ?: "", score.toFloat(), intent.getStringExtra(EXTRA_IMAGE_URI) ?: "")
            } catch (e: JSONException) {
                Log.e("ResultActivity", "Error parsing JSON: ${e.message}")
            }
        }



        val apiService = ApiConfig.getApiService()
        val apiKey = BuildConfig.TOKEN // Ganti dengan API key Anda
        val call = apiService.getTopHeadlines(apiKey = apiKey)

        Log.d("ResultActivitys", "Request: ${call.request()}")

        call.enqueue(object : Callback<Responses> {
            override fun onResponse(call: Call<Responses>, response: Response<Responses>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let { data ->
                        // Tangani data respons yang diperoleh dari API
                        val articles = data.articles

                        articles?.let { articleList ->
                            // Iterasi melalui daftar artikel dan tampilkan nilainya
                            val stringBuilder = StringBuilder()
                            for (article in articleList) {
                                if (article?.title?.trim() != "[Removed]") { // Trim the title before comparison
                                    article?.let {
                                        stringBuilder.append("Title : ${it.title}\n")
                                        stringBuilder.append("Author : ${it.author}\n\n")
                                        stringBuilder.append("Description : ${it.description}\n\n")
                                        stringBuilder.append("Kunjungi =  ${it.url}\n\n")

                                        stringBuilder.append("----------------------------------------------------------------\n\n\n")
                                    }
                                }
                            }



                            binding.resultText3.text = stringBuilder.toString()
                        }
                    }
                } else {
                    // Tangani jika respons tidak berhasil
                    Log.e("ResultActivity", "Failed to fetch data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Responses>, t: Throwable) {
                // Tangani jika terjadi kegagalan koneksi atau respons
                Log.e("ResultActivity", "Error fetching data: ${t.message}")
            }
        })






    }



}

