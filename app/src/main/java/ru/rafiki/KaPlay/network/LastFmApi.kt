package ru.rafiki.KaPlay.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.rafiki.KaPlay.network.model.GetTrackInfoResult
import ru.rafiki.KaPlay.network.model.SimilarTrackResult

/**
 * Created by denis.sakovich on 04.12.2017.
 */
interface LastFmApi {

    @GET("2.0/")
    fun getTrackInfo(@Query("method") method: String,
                     @Query("api_key") api_key: String,
                     @Query("artist") artist: String,
                     @Query("track") track: String,
                     @Query("format") format: String = "json"): Observable<GetTrackInfoResult>

    @GET("2.0/")
    fun getSimilarTrack(@Query("method") method: String,
                        @Query("api_key") api_key: String,
                        @Query("artist") artist: String,
                        @Query("track") track: String,
                        @Query("limit") limit: String,
                        @Query("format") format: String = "json") : Observable<SimilarTrackResult>

    companion object {
        private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        private val api: LastFmApi = create()
        fun create() : LastFmApi {
                val retrofit = Retrofit.Builder()
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://ws.audioscrobbler.com/")
                        .build()
                return retrofit.create(LastFmApi::class.java)

        }
        fun getLastFmApi() : LastFmApi {
            return api
        }
    }

}