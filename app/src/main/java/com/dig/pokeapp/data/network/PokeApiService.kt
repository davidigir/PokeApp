package com.dig.pokeapp.data.network

import com.dig.pokeapp.data.model.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService{
    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): Pokemon

}