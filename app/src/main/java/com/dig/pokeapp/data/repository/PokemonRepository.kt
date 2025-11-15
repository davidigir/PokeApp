package com.dig.pokeapp.data.repository

import android.util.Log
import com.dig.pokeapp.data.dao.PokemonDao
import com.dig.pokeapp.data.database.PokemonDatabase
import com.dig.pokeapp.data.entity.PokemonEntity
import com.dig.pokeapp.data.model.Pokemon
import com.dig.pokeapp.data.network.PokeApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao
){

    suspend fun getPokemon(): List<PokemonEntity>{
        val localData = dao.getAllPokemon()
        if(localData.isNotEmpty()){
            return localData
        }
        val detailList = coroutineScope {
            (1..151).map { id ->
                Log.d("PokemonViewModel", "Pokemon agregado: ${id}")

                async {
                    val p = api.getPokemonById(id)
                    PokemonEntity(
                        id = p.id,
                        name = p.name,
                        types = p.types.joinToString { it.type.name }
                    )

                }
            }.awaitAll() // Espera que todas terminen

        }
        dao.insertAll(detailList)
        return detailList


    }



}