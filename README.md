# PixelPlay

Catálogo de videojuegos para Android que consume una API REST pública y muestra los juegos en una lista con su ficha de detalle.

## Características actuales

- **Home**: lista de juegos en una grilla de 2 columnas, con manejo de los tres estados de UI: **carga**, **éxito** (grilla de tarjetas con imagen, nombre y género) y **error** (mensaje + botón *Reintentar*).
- **Detalle**: ficha de un juego por su `id`, con imagen grande, nombre, género, plataforma y descripción (si el juego no trae descripción larga, se usa la corta como respaldo). Contenido desplazable.
- **Navegación**: flujo Home → Detalle mediante Navigation Compose, con botón de volver en la barra superior.

## Arquitectura

MVVM con flujo de datos unidireccional:

```
UI (Compose)  ->  ViewModel (StateFlow + UiState)  ->  Repository  ->  Retrofit (API)
```

- La UI solo observa el estado del ViewModel; no llama a la red directamente.
- Cada pantalla expone un `UiState` sellado con el patrón **Loading / Success / Error**, que la UI resuelve con `when(state)`.
- El `Repository` convierte los DTO de red al modelo de dominio antes de exponerlos.

## Stack técnico

Versiones tomadas de `gradle/libs.versions.toml`:

| Componente | Versión |
|---|---|
| Kotlin | 2.4.10 |
| Android Gradle Plugin (AGP) | 9.3.3 |
| Jetpack Compose (BOM) | 2026.02.01 |
| Material 3 | vía Compose BOM |
| Retrofit | 3.0.0 |
| Converter Gson | 3.0.0 |
| OkHttp (logging-interceptor) | 5.5.0 |
| Coil | 3.6.3 |
| Navigation Compose | 2.9.8 |
| Lifecycle | 2.11.0 |

## API

Los datos provienen de **FreeToGame**: `https://www.freetogame.com/api/`

- Es una API pública: **no requiere API key ni login**.
- Endpoints usados: lista de juegos (`games`) y detalle por id (`game?id={id}`).
- **Nota**: las descripciones de los juegos vienen **en inglés**, ya que es el idioma de la fuente de datos.

## Estructura del proyecto

```
com.equipo.pixelplay/
├─ data/
│  ├─ remote/api/     Interface Retrofit (PixelPlayApiService) y cliente (RetrofitClient)
│  ├─ remote/dto/     DTO de red (GameDto)
│  └─ repository/     GameRepository (expone el modelo de dominio)
├─ domain/model/      Modelo Game y mapper DTO -> dominio
├─ navigation/        Rutas centralizadas (Routes) y NavHost (PixelPlayNavHost)
├─ ui/
│  ├─ theme/          Tema Material 3 (Color, Type, Shape, Theme)
│  ├─ components/     Composables reutilizables (GameCard, LoadingView, ErrorView)
│  ├─ home/           Home: UiState, ViewModel y pantalla
│  └─ detail/         Detalle: UiState, ViewModel y pantalla
└─ MainActivity.kt    Punto de entrada; monta el tema y la navegación
```

## Requisitos

- Android Studio reciente.
- **SDK Platform 37** (el proyecto compila contra `compileSdk 37`; `targetSdk 36`).
- JDK 11 o compatible.
- **minSdk 24**.

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Dejar que Gradle haga la sincronización de dependencias.
4. Ejecutar la app en un emulador o dispositivo.

No es necesario configurar ninguna clave: FreeToGame no la requiere.

## Capturas

_Pega aquí las capturas del emulador._

### Home

<!-- ![Home](docs/screenshots/home.png) -->

### Detalle

<!-- ![Detalle](docs/screenshots/detail.png) -->
