package ru.maxim.mospolytech.polydroid.repository.remote.service

import retrofit2.Call
import retrofit2.http.GET
import ru.maxim.mospolytech.polydroid.model.SearchObject.SearchObjects
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface SearchObjectsService {

    @GET("/search-objects")
    fun getSearchObjects(): Call<SearchObjects>

    companion object{
        operator fun invoke(): SearchObjectsService =
            RetrofitClient.instance.create(SearchObjectsService::class.java)
    }
}