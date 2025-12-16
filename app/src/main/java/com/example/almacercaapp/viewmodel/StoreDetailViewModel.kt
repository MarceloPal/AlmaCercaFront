package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.almacercaapp.R
import com.example.almacercaapp.model.ProductCategory
import com.example.almacercaapp.model.Store
import com.example.almacercaapp.model.StoreCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update// ▼▼▼ 2. AÑADE la lista de categorías de productos al UiState ▼▼▼

// Define el estado de la UI
data class StoreDetailUiState(
    val store: Store? = null,
    val storeCategoryName: String = "", // Solo necesitamos el NOMBRE de la categoría de la tienda
    val productCategories: List<ProductCategory> = emptyList(),
    val isLoading: Boolean = true
)

class StoreDetailViewModel : ViewModel() {
    //simulacion de datos
    // En una app real, esto vendría de un repositorio que consulta una API o DB

    // 1. Datos para las Categorías de Tienda (StoreCategory)
    // Ahora cada una podría pertenecer a una tienda, pero para simplificar, las haremos globales
    // y las filtraremos después. O podemos asignarlas directamente.
    // Para este ejemplo, vamos a mantenerlas como una lista global de posibilidades.
    private val allStoreCategories = listOf(
        StoreCategory(id = 501, name = "Minimarket", imageRes = R.drawable.cat_minimarket), // storeId 0 = genérica
        StoreCategory(id = 502, name = "Botillería", imageRes = R.drawable.cat_botilleria),
        StoreCategory(id = 503, name = "Bazar", imageRes = R.drawable.cat_bazar),
        StoreCategory(id = 505, name = "Almacen", imageRes = R.drawable.cat_almacen)
    )
    private val allStores = listOf(
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

    // ▼▼▼ CORRECCIÓN 1: Parámetros de ProductCategory arreglados ▼▼▼
    private val allProductCategories = listOf(
        // Tienda 2
        ProductCategory("101",
            "Vegetales y Frutas",
            R.drawable.cat_verduras,
            "2"),
        ProductCategory("102"
            , "Aceites",
            R.drawable.cat_aceites
            , "2"),
        // Tienda 1
        ProductCategory("103",
            "Lacteos",
            R.drawable.cat_lacteos,
            "3"),
        ProductCategory("104",
            "Carnes",
            R.drawable.cat_carnes,
            "3"),
        ProductCategory("105",
            "Bebestibles",
            R.drawable.cat_bebestibles,
            "1"),
        ProductCategory("106",
            "Despensa",
            R.drawable.cat_despensa,
            "1")
    )

    private val _uiState = MutableStateFlow(StoreDetailUiState())
    val uiState: StateFlow<StoreDetailUiState> = _uiState.asStateFlow()

    // La función CLAVE que la UI llamará
    fun loadStoreDetails(storeIdString: String?) {
        _uiState.update { it.copy(isLoading = true) }

        if (storeIdString == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        val storeId = storeIdString.toIntOrNull() ?: return

        // 1. Busca la tienda
        val foundStore = allStores.find { it.id == storeId }

        // 2. Busca el nombre de la categoría de la tienda (si la tienda fue encontrada)
        val categoryName = foundStore?.let { store ->
            allStoreCategories.find { it.id == store.storeCategoryId }?.name
        } ?: ""

        // 3. Busca las categorías de productos para esa tienda
        val storeProductCategories = allProductCategories.filter { it.storeId == storeId.toString() }

        // 4. Actualiza el estado de la UI con toda la información
        _uiState.update {
            it.copy(
                isLoading = false,
                store = foundStore,
                storeCategoryName = categoryName, // Pasamos solo el nombre que la UI necesita
                productCategories = storeProductCategories
            )
        }
    }
}
