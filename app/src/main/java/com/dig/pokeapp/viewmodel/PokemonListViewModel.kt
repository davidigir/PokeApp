package com.dig.pokeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.dig.pokeapp.data.entity.PokemonEntity
import com.dig.pokeapp.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

sealed class PokemonUiState {
    object Loading : PokemonUiState()
    data class Success(
        val pokemonList: List<PokemonEntity>,
        val pokemonFilteredList: List<PokemonEntity>,
        val searchQuery: String = ""
    ) : PokemonUiState()

    data class Error(val message: String) : PokemonUiState()

}

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository,
    val imageLoader: ImageLoader
) : ViewModel() {

    private val _UiState = MutableStateFlow<PokemonUiState>(PokemonUiState.Loading)
    val uiState: StateFlow<PokemonUiState> = _UiState.asStateFlow()

    fun fetchPokemonList() {
        _UiState.value = PokemonUiState.Loading
        viewModelScope.launch {
            try {
                val detailList= repository.getPokemon()


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



}