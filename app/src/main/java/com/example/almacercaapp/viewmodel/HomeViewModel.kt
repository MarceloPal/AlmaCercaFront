package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.almacercaapp.R
import com.example.almacercaapp.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update // Útil para actualizar el estado inmutable
import com.example.almacercaapp.model.Category
// 1. Define el estado COMPLETO que necesita la UI
data class HomeUiState(
    val query: String = "", //define el texto que el usuario escribe en el componente de barra de busqueda
    val searchResults: List<Store> = emptyList(), // define la lista de resultados que debe mostrarse mientras el usuario escribe o pulsa la tecla "buscar"
    val categories: List<Category> = emptyList(), // Usa tu  clase Category
    val storesInYourArea: List<Store> = emptyList(), //tiendas en la zona
    @androidx.annotation.DrawableRes val bannerRes: Int? = null, // muestra el banner y es local
    val isLoading: Boolean = false // definimos si la UI debe mostrar un indicador de carga oara darle un feedback al usuario
)

class HomeViewModel : ViewModel() {



    // --- SIMULACIÓN DE UN REPOSITORIO (datos de prueba) ---
    private val categoriesData = listOf(
        Category(501,"Minimarket", R.drawable.cat_minimarket),
        Category(502,"Botillería", R.drawable.cat_botilleria),
        Category(504,"Bazar", R.drawable.cat_bazar),
        Category(505,"Almacen", R.drawable.cat_almacen)
    )

    // 1. Datos para las tiendas "En tu zona"
    private val storesData = listOf(
        Store(
            id = 1,
            name = "Botillería La Esquina",
            storeCategoryId = 502,
            address = "...",
            distance = "150 m",
            logoRes = R.drawable.logo_esquina,
            // ▼▼▼ AÑADE LOS PARÁMETROS FALTANTES ▼▼▼
            rating = 4.8f,
            description = "La mejor botillería de la zona, con una gran variedad de licores y bebidas."
        ),
        Store(
            id = 2,
            name = "Almacen y Bazar Sandra",
            storeCategoryId = 505,
            address = "...",
            distance = "300 m",
            logoRes = R.drawable.logo_sandra,
            // ▼▼▼ AÑADE LOS PARÁMETROS FALTANTES ▼▼▼
            rating = 4.5f,
            description = "Tu almacén de confianza para las compras del día a día. Frutas, verduras y abarrotes."
        ),
        Store(
            id = 3,
            name = "Almacen Mar",
            storeCategoryId = 505,
            address = "...",
            distance = "500 m",
            logoRes = R.drawable.logo_mariluna,
            // ▼▼▼ AÑADE LOS PARÁMETROS FALTANTES ▼▼▼
            rating = 4.2f,
            description = "Especialistas en productos del mar y conservas. Frescura garantizada."
        )
    )

    // 2. Datos para las categorías


    // --- FIN DE LA SIMULACIÓN ---

    // 2. Estado Mutable Privado (MutableStateFlow)
    private val _uiState = MutableStateFlow(
        HomeUiState(
            storesInYourArea = storesData, // Carga las tiendas "En tu zona"
            categories = categoriesData,   // Carga las categorías con imágenes
            bannerRes = R.drawable.banner_lucas // Carga el banner
        )
    )
    // 3. Estado Público Inmutable (StateFlow)
    val uiState: StateFlow<HomeUiState> = _uiState

    // --- LÓGICA DE BÚSQUEDA (casi no cambia) ---
    fun onQueryChange(newQuery: String) {
        // Actualiza el 'query' en el StateFlow
        _uiState.update { it.copy(query = newQuery) }
        filterResults(newQuery)
    }

    // 5. Método de búsqueda general
    private fun filterResults(currentQuery: String) {
        val filteredList = if (currentQuery.isNotBlank()) {
            storesData.filter { store ->
                // Buscamos el nombre de la categoría correspondiente al ID de la tienda
                val categoryName = categoriesData.find { it.id == store.storeCategoryId }?.name ?: ""
                store.name.contains(currentQuery, ignoreCase = true) ||
                        categoryName.contains(currentQuery, ignoreCase = true)
            }
        } else {
            emptyList()
        }
        _uiState.update { it.copy(searchResults = filteredList) }
    }

    // 6. onSearch: Puede reutilizar filterResults o agregar lógica extra (ej. guardar historial)
    fun onSearch(query: String) {
        filterResults(query)
        // Lógica adicional: aquí podrías añadir la búsqueda al historial de recientes
    }

    // Método para limpiar la búsqueda si es necesario
    fun clearSearch() {
        _uiState.update { it.copy(query = "", searchResults = emptyList()) }
    }
}