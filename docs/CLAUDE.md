# CLAUDE.md — PixelPlay

Reglas duras para Claude Code. Este archivo es tu única fuente de reglas.
La estrategia vive en /docs — NO la leas.

## Qué es
App Android (Kotlin + Jetpack Compose) que muestra un catálogo de videojuegos
desde una API REST. Dos pantallas: Home (lista con estados) y Detalle (ficha por id).
Norte: simple, completo y verificable > elaborado.

## Package (CONGELADO)
com.equipo.pixelplay — nunca lo cambies ni muevas archivos fuera de él.

## Arquitectura (MVVM estricto, flujo unidireccional)
UI (Compose) -> ViewModel (StateFlow/UiState) -> Repository -> Retrofit (API)
- La UI NUNCA llama a Retrofit ni al Repository directo: solo observa el estado
  del ViewModel y dispara eventos.
- Nada de lógica de red dentro de un @Composable.
- Un sealed interface de UiState por pantalla: Loading / Success(data) / Error(message).
  La UI resuelve con when(state){ ... }.
- Un archivo, una responsabilidad.

## Stack
Kotlin · Jetpack Compose + Material 3 · MVVM · Retrofit + converter-gson ·
OkHttp logging-interceptor · Coil (coil-compose, AsyncImage) · Navigation Compose ·
Lifecycle ViewModel Compose + lifecycle-runtime-compose (collectAsStateWithLifecycle) ·
Coroutines.

## Estructura de paquetes
com.equipo.pixelplay/
├─ MainActivity.kt
├─ data/remote/dto/     (GameDto, GamesResponse)
├─ data/remote/api/     (PixelPlayApiService, RetrofitClient)
├─ data/repository/     (GameRepository: suspend getGames() / getGame(id))
├─ domain/model/        (Game + mapper DTO->Game)
├─ ui/theme/            (Theme/Color/Type/Shape — Material 3 personalizado)
├─ ui/home/             (HomeScreen, HomeViewModel, HomeUiState)
├─ ui/detail/           (DetailScreen, DetailViewModel, DetailUiState)
├─ ui/components/        (GameCard, LoadingView, ErrorView)
└─ navigation/          (PixelPlayNavHost, Routes)

## Navegación
Un solo NavHost, startDestination = "home".
Rutas: "home" y "detail/{gameId}" con navArgument. Sin strings de ruta sueltos:
todo centralizado en Routes.kt.

## Protocolo (obligatorio en cada tarea)
1. Primero un PLAN detallado. NO ejecutes nada hasta que yo escriba "aprobado".
2. NO corras el emulador ni crees/arranques AVDs. SÍ puedes compilar
   (./gradlew assembleDebug) y correr unit tests JVM, y reportarlo.
   Las pruebas visuales/UI las corre el equipo en el emulador.
3. Cambios pequeños y por capa. No toques archivos fuera del alcance de la tarea.
4. Al terminar: RESUMEN — archivos creados/modificados, decisiones,
   versiones usadas, pendientes.

## Git
- Nunca commitees directo a main. Rama por tarea. Commits pequeños, en español,
  descriptivos.
- Nunca subas: local.properties, la API key, .idea/, build/, .gradle/.
  Sí sube: gradlew, gradlew.bat, gradle/wrapper/.

## Entorno
Versiones: las del template estable de Android Studio + últimas estables de las libs;
se fijan (pin) tras el primer build verde y no se cambian sin motivo.
Regla de oro: lo que no se verificó NO está hecho.