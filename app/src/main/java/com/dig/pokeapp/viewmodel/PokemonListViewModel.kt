package com.dig.pokeapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import coil.Coil.imageLoader
import coil.ImageLoader
import coil.request.ImageRequest
import com.dig.pokeapp.data.model.Pokemon
import com.dig.pokeapp.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

sealed class PokemonUiState {
    object Loading : PokemonUiState()
    data class Success(
        val pokemonList: List<Pokemon>,
        val pokemonFilteredList: List<Pokemon>,
        val searchQuery: String = ""
    ) : PokemonUiState()

    data class Error(val message: String) : PokemonUiState()

}

class PokemonViewModel(
) : ViewModel() {

    private val _UiState = MutableStateFlow<PokemonUiState>(PokemonUiState.Loading)
    val uiState: StateFlow<PokemonUiState> = _UiState.asStateFlow()

    fun fetchPokemonList() {
        _UiState.value = PokemonUiState.Loading
        viewModelScope.launch {
            try {
                //Multihilo para q haga varias llamadas a la API
                val detailList = coroutineScope {
                    (1..151).map { id ->
                        Log.d("PokemonViewModel", "Pokemon agregado: ${id}")

                        async {
                            RetrofitClient.api.getPokemonById(id)
                        }
                    }.awaitAll() // Espera que todas terminen

                }

                _UiState.value = PokemonUiState.Success(
                    pokemonList = detailList,
                    pokemonFilteredList = detailList
                )


            } catch (e: Exception) {
                _UiState.value = PokemonUiState.Error("No se ha podido obtener la base de datos")

            }
        }

    }


    fun searchPokemon(query: String) {
        val filteredList = (_UiState.value as PokemonUiState.Success).pokemonList
        val searchQuery = query.trim().lowercase()

        val filtered = filteredList.filter { pokemon ->
            pokemon.name.lowercase().contains(searchQuery)
        }
        _UiState.update {
            PokemonUiState.Success(
                pokemonList = (_UiState.value as PokemonUiState.Success).pokemonList,
                pokemonFilteredList = filtered,
                searchQuery = query
            )
        }

    }

    init {
        fetchPokemonList()
    }


}