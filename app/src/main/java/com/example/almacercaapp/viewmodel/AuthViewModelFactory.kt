package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.almacercaapp.data.repository.UserRepository

// Factory (Fábrica) para crear instancias de AuthViewModel
//instancias=crear un objeto especifico a partir de una clase.
// pasándole la dependencia necesaria (UserRepository).
class AuthViewModelFactory(
    private val repository: UserRepository // La dependencia que necesita AuthViewModel
) : ViewModelProvider.Factory {

    // El sistema llama a este método cuando necesita crear un ViewModel
    @Suppress("UNCHECKED_CAST") // Suprime una advertencia de casting genérico inevitable aquí
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si la clase que se pide crear es AuthViewModel...
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // ...la creamos pasándole el 'repository' que recibimos en el constructor de la Factory
            return AuthViewModel(repository) as T
        }
        // Si se pide crear cualquier otro tipo de ViewModel, lanzamos un error
        // porque esta Factory solo sabe crear AuthViewModels.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}


/*Android no sabe cómo crear automáticamente un AuthViewModel si necesita algo en su constructor.
La AuthViewModelFactory actúa como una "instrucción de construcción" ️:
le dice a Android exactamente cómo crear tu AuthViewModel,
asegurándose de pasarle el UserRepository que necesita.*/