# 🛒 AlmaCerca App

**Plataforma de Comercio Local**
AlmaCercaApp es una aplicación móvil que conecta directa y eficientemente a Compradores y Vendedores locales.

![Estado](https://img.shields.io/badge/Estado-Producci%C3%B3n-green)
![Tech](https://img.shields.io/badge/Stack-Kotlin%20|%20Spring%20Boot%20|%20MongoDB-blue)

## 📱 Funcionalidades Principales

El sistema separa completamente los flujos de trabajo según el rol del usuario:

### 👤 Rol: Comprador
* **Interfaz de Compra:** Navegación fluida con barra inferior.
* **Carrito de Compras:** Funcionalidad completa para agregar, eliminar y gestionar cantidades.
* **Persistencia:** El carrito se guarda en la base de datos y se recupera al iniciar sesión.

### 🏪 Rol: Vendedor
* **Gestión de Inventario (CRUD):** Agregar, editar y eliminar productos.
* **Panel de Control:** Menú lateral con acceso a soporte y gestión.
* **Carga de Imágenes:** Gestión visual de los productos.

## 🛠️ Stack Tecnológico y Arquitectura

El proyecto utiliza una arquitectura **MVVM (Model-View-ViewModel)** para separar la interfaz gráfica de la lógica de negocio.

* **Frontend:** Android Nativo con **Kotlin** y **Jetpack Compose**.
* **Backend:** API REST desarrollada en **Java (Spring Boot)**.
* **Base de Datos:** **MongoDB Atlas** (NoSQL).
* **Infraestructura:** Desplegado en **Render**.
* **Seguridad:** Autenticación mediante **JWT Tokens**.

---

## 🚀 Cómo ejecutar el proyecto

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/YjaelBelenLisboaMardones/AlmaCercaAplicacionMovil.git](https://github.com/YjaelBelenLisboaMardones/AlmaCercaAplicacionMovil.git)
    ```
2.  **Abrir en Android Studio:**
    * Usa una versión reciente (Ladybug o superior recomendado).
    * Espera a que Gradle sincronice las dependencias.
3.  **Ejecutar:**
    * Selecciona el dispositivo (Emulador o Físico, mín API 24).
    * Dale al botón de "Run".
    * *Nota: La app ya está configurada para conectarse al servidor de producción en Render.*

---

## 🔌 API Endpoints (Backend)

La aplicación móvil consume los siguientes servicios desplegados en la nube:

| Método | Endpoint | Rol Requerido | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Público | Registro de usuario |
| `POST` | `/api/auth/login` | Público | Login (Retorna Token + Rol) |
| `GET` | `/api/products` | Público | Catálogo de productos |
| `GET` | `/api/products/category/{idCategoria}` | Público | Buscar por categoría |
| `GET` | `/api/products/{idProducto}` | Público | Buscar detalle de producto |
| `GET` | `/api/cart` | BUYER | Ver carrito |
| `POST` | `/api/cart/add` | BUYER | Agregar ítem al carrito |
| `GET` | `/api/cart/items/{idProducto}` | BUYER | Ver solo ese item del Carrito |
| `PUT` | `/api/cart/items/{idProducto}` | BUYER | Actualizar Cantidad |
| `DELETE` | `/api/cart/items/{idProducto}` | BUYER | Eliminar Producto del Carrito |
| `GET` | `/api/admin/products` | ADMIN | Listar productos |
| `POST` | `/api/admin/products` | ADMIN | Crear nuevo producto |
| `PUT` | `/api/admin/products/{idProducto}` | ADMIN | Editar producto |
| `DELETE` | `/api/admin/products/{idProducto}` | ADMIN | Eliminar producto |




---

## 🐛 Bitácora de Incidencias (Troubleshooting)

Registro de problemas reales solucionados durante la migración a Producción:

* **Error de Cold Start (Render):** Se detectó que la primera petición tarda ~60 segundos debido a la suspensión del servidor gratuito. **Estado:** Documentado como comportamiento esperado.
* **Exposición de Credenciales:** Un archivo `.gitignore` mal configurado expuso configuraciones locales. **Solución:** Se limpió el historial de Git y se corrigió el archivo.
* **Error de Puertos:** Render no detectaba el arranque de Spring Boot. **Solución:** Se inyectó la variable `PORT=8080` en el entorno de nube.
