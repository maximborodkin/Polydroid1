package ru.maxim.mospolytech.polydroid.repository.remote.service

import retrofit2.http.GET
import ru.maxim.mospolytech.polydroid.model.SearchObjects
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface SearchObjectsService {

    @GET("/search-objects")
    suspend fun getSearchObjects(): SearchObjects

    companion object{
        operator fun invoke(): SearchObjectsService =
            RetrofitClient.instance.create(SearchObjectsService::class.java)
    }
}