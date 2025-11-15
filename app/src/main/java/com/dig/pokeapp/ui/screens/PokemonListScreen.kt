package com.dig.pokeapp.ui.screens

import android.app.Application
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil.imageLoader
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.LocalImageLoader
import coil.decode.Decoder
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dig.pokeapp.data.entity.PokemonEntity
import com.dig.pokeapp.data.model.Pokemon
import com.dig.pokeapp.ui.typeColors
import com.dig.pokeapp.viewmodel.PokemonUiState
import com.dig.pokeapp.viewmodel.PokemonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview
@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPokemonList()
    }

    when (uiState) {
        is PokemonUiState.Loading -> {
            LoadingScreen()


        }

        is PokemonUiState.Success -> {
            SuccessScreen(viewModel, modifier)

        }

        is PokemonUiState.Error -> {
            ErrorScreen()

        }
    }

}
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
        , contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
               color = MaterialTheme.colorScheme.onPrimary
            )
            Text(text = "Loading", color = MaterialTheme.colorScheme.onPrimary)
        }

    }
}

@Composable
fun ErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "No se ha podido acceder a la base de datos")
        }

    }
}

@Composable
fun SuccessScreen(
    viewModel: PokemonViewModel,
    modifier: Modifier = Modifier

) {
    val uiState by viewModel.uiState.collectAsState()
    val pokemonList = (uiState as PokemonUiState.Success).pokemonFilteredList

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color(71, 105, 255, 255)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        SearchPokemonItem(
            searchQuery = (uiState as PokemonUiState.Success).searchQuery,
            onQueryChange = { newQuery ->
                viewModel.searchPokemon(newQuery)
            }
        )
        PokemonList(pokemonList, viewModel)
    }

}

@Composable
fun PokemonList(
    pokemonList: List<PokemonEntity>,
    viewModel: PokemonViewModel = hiltViewModel()
) {

    val getImageUrl =
        { id: Int -> "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        items(pokemonList) { pokemon ->
            val types = pokemon.types.split(",").map { it.trim() }.toMutableList()
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20))
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
                    .padding(vertical = 5.dp, horizontal = 10.dp)


            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    AsyncImage(
                        //bitmap = img!!,
                        model = getImageUrl(pokemon.id),
                        contentDescription = null,
                        imageLoader = viewModel.imageLoader,
                        modifier = Modifier.size(64.dp)

                    )
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                        ) {
                            Text(
                                text = pokemon.name.first().uppercase() + pokemon.name.substring(1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Text(
                                text = String.format("#%03d", pokemon.id),
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = MaterialTheme.colorScheme.onPrimary
                            )


                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),


                            ) {
                            for (type in types) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20))
                                        .background(
                                            typeColors.getOrDefault(type, Color.White)

                                        )
                                        .padding(vertical = 5.dp, horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = type.first().uppercase() + type.substring(1),
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                            }

                        }
                    }

                }

            }
        }
    }
}

@Composable
fun SearchPokemonItem(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 4.dp) // padding interno
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                onQueryChange(newValue)
            },
            placeholder = {
                Text(
                    text = "Search any Pokemon",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp
                )
            },
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp)
        )
    }
}
