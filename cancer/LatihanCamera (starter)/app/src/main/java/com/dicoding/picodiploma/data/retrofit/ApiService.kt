import com.dicoding.picodiploma.data.response.Responses
import com.example.myapplication.data.response.DResponse
import com.example.myapplication.data.response.ResponseFollowersItem
import com.example.myapplication.data.response.SResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("q") query: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String
    ): Call<Responses>
}