# PixelPlay 🎮

Aplicación móvil Android de **catálogo de videojuegos** con autenticación de usuario, favoritos por cuenta y un panel de administración. Construida con Kotlin y Jetpack Compose, consume una API REST pública y usa Firebase para identidad y datos de usuario.

**Repositorio:** https://github.com/Cardozo-Mosquera/PixelPlay
**Unidad de aprendizaje:** Desarrollo de Aplicaciones Móviles · **Proyecto Firebase:** `pixelplay-f22a9`

---

## 📱 Qué hace

PixelPlay permite explorar un catálogo de videojuegos con su imagen, nombre, género, plataforma y descripción, protegido por un inicio de sesión. Cada usuario puede marcar juegos como favoritos —guardados **en su cuenta**, no en el teléfono, así que sobreviven a reinstalar y viajan entre dispositivos— y consultar su lista personal. Los usuarios con rol de administrador acceden además a un panel para ver los usuarios registrados.

**Flujo de la app:**
`Login/Registro → Home (catálogo + búsqueda) → Detalle del juego → marcar/desmarcar favorito → Mis favoritos`, con acceso a **Perfil** (cerrar sesión) y, solo para admins, al **panel de usuarios**.

---

## ✨ Características

- **Catálogo de videojuegos** consumido desde una API REST pública, con manejo de estados de carga, éxito y error.
- **Búsqueda en vivo** sobre el catálogo cargado (filtrado en memoria, sin llamadas extra a la API).
- **Autenticación** por correo y contraseña con Firebase Auth; la sesión persiste entre reinicios.
- **Favoritos por cuenta**, almacenados en Cloud Firestore y sincronizados en tiempo real.
- **Roles de usuario** (`user` / `admin`) leídos desde Firestore.
- **Panel de administración** (solo visible para admins) que lista los usuarios registrados con su correo, rol y fecha de alta.
- **Reglas de seguridad de Firestore**: cada quien accede solo a sus datos, nadie puede auto-ascenderse a administrador, y solo un admin puede listar usuarios.
- **Interfaz Material Design 3** con tema propio (esquema claro/oscuro).

---

## 🖼️ Capturas

> Coloca las imágenes en una carpeta `screenshots/` en la raíz del repositorio con estos nombres y se mostrarán automáticamente. Sube capturas reales del emulador.

| Login | Catálogo (Home) | Detalle |
|---|---|---|
| ![Login](screenshots/login.png) | ![Home](screenshots/home.png) | ![Detalle](screenshots/detalle.png) |

| Mis favoritos | Perfil | Panel de admin |
|---|---|---|
| ![Favoritos](screenshots/favoritos.png) | ![Perfil](screenshots/perfil.png) | ![Admin](screenshots/admin.png) |

---

## 🛠️ Stack técnico

| Componente | Versión |
|---|---|
| Kotlin | 2.4.10 |
| AGP / Gradle | 9.3.3 / 9.5 |
| compileSdk / minSdk / targetSdk | 37 / 24 / 36 |
| Jetpack Compose (BOM) / Material 3 | 2026.02.01 / 1.4.0 |
| Navigation Compose | 2.9.8 |
| Lifecycle ViewModel + Runtime Compose | 2.11.0 |
| Retrofit + converter-gson | 3.0.0 |
| OkHttp logging-interceptor | 5.5.0 |
| Coil (coil-compose + coil-network-okhttp) | 3.6.3 |
| Firebase BOM | 34.11.0 |
| firebase-auth | 24.0.1 |
| firebase-firestore | 26.1.2 |
| kotlinx-coroutines-play-services | 1.10.2 |

> Solo se usa `material-icons-core` (nunca `-extended`). El tema mantiene `dynamicColor = false` para conservar la identidad visual en Android 12+.

---

## 🏗️ Arquitectura

Patrón **MVVM estricto** con flujo de datos unidireccional:

```
UI (Compose) → ViewModel (StateFlow / UiState) → Repository → Retrofit / Firebase
```

La UI nunca llama directamente a la red ni a Firebase: observa el estado que expone el ViewModel y dispara eventos. Cada pantalla modela su estado con un `sealed interface` (`Loading` / `Success` / `Error`).

La **autenticación funciona como puerta (gate)** por encima del grafo de navegación: si hay sesión se monta la app; si no, se muestra Login/Registro. El cierre de sesión es reactivo (la UI vuelve sola al login al detectar que ya no hay sesión).

### Estructura de paquetes

```
com.equipo.pixelplay/
├─ MainActivity.kt              → Gate de autenticación
├─ data/
│  ├─ remote/
│  │  ├─ dto/                   → GameDto
│  │  └─ api/                   → PixelPlayApiService · RetrofitClient
│  └─ repository/               → GameRepository · AuthRepository · UserRepository
│                                 · FavoritesRepository · AdminRepository
├─ domain/
│  └─ model/                    → Game · GameMapper · UsuarioAdmin
├─ ui/
│  ├─ theme/                    → Theme · Color · Type · Shape
│  ├─ home/                     → catálogo + búsqueda
│  ├─ detail/                   → ficha del juego + toggle de favorito
│  ├─ auth/                     → login / registro
│  ├─ profile/                  → perfil + cerrar sesión
│  ├─ favorites/                → "Mis favoritos"
│  ├─ admin/                    → panel de usuarios (solo admin)
│  └─ components/               → GameCard · LoadingView · ErrorView
└─ navigation/                  → PixelPlayNavHost · Routes
```

---

## 🌐 API

El catálogo proviene de la **API pública de [FreeToGame](https://www.freetogame.com/api-doc)** (REST, sin clave de API):

- Lista de juegos: `GET https://www.freetogame.com/api/games`
- Detalle de un juego: `GET https://www.freetogame.com/api/game?id={id}`

Las peticiones incluyen un header `User-Agent` (algunas respuestas fallan sin él). Las descripciones del catálogo llegan en inglés porque así las sirve la fuente; la interfaz de la app está en español.

---

## 🔥 Firebase

El proyecto usa dos servicios de Firebase:

- **Authentication** (correo/contraseña): identidad de los usuarios.
- **Cloud Firestore**: datos por usuario.

### Modelo de datos

```
users/{uid}                     → { email, role: "user" | "admin", createdAt }
users/{uid}/favorites/{gameId}  → { name, thumbnail, genre, addedAt }
gameStats/{gameId}              → { name, favoriteCount }
config/{doc}                    → configuración editable por admin (destacados, próximamente)
```

El documento de usuario se crea automáticamente al registrarse o iniciar sesión. El campo `role` nunca se degrada ni se sobrescribe desde la app.

### 🔒 Seguridad

Las reglas de Firestore garantizan que:

- Cada usuario solo lee y escribe **sus propios** favoritos.
- Un usuario solo puede leer su propio documento; **solo un admin** puede listar todos los usuarios.
- Al crear su documento, el rol siempre es `user`, y en una actualización el rol **no puede cambiar** — es decir, nadie puede convertirse en administrador desde la aplicación.
- La asignación de administradores la hace manualmente el dueño de la base de datos desde la consola de Firebase.

El archivo de reglas está versionado en `firestore.rules`.

---

## 🚀 Cómo ejecutar

1. **Clona** el repositorio.
2. Abre el proyecto con una versión **reciente de Android Studio** (AGP 9.3.3 no abre en versiones antiguas) e instala el **SDK Platform 37** y un JDK compatible.
3. Crea (o usa) un proyecto en [Firebase](https://console.firebase.google.com/) con **Authentication (correo/contraseña)** y **Cloud Firestore** habilitados, y descarga tu propio **`google-services.json`** en la carpeta `app/`.
   > Por seguridad, `google-services.json` no se incluye en el repositorio.
4. Publica las reglas de `firestore.rules` en tu base de datos de Firestore.
5. Sincroniza Gradle y ejecuta la app en un emulador o dispositivo.

### Convertir una cuenta en administrador

1. Regístrate normalmente dentro de la app.
2. En la **consola de Firebase → Firestore → Datos**, abre tu documento en `users/{uid}`.
3. Cambia el campo `role` de `"user"` a `"admin"`.
4. Reinicia la app: ahora verás el acceso al panel de administración.

---

## ✅ Requisitos de la entrega

| Requisito | Implementación |
|---|---|
| Arquitectura MVVM | UI → ViewModel (StateFlow/UiState) → Repository → API/Firebase |
| Consumo de API REST | Retrofit + FreeToGame, con estados de carga/éxito/error |
| Material Design 3 | Tema propio (`Theme`, `Color`, `Type`, `Shape`), `dynamicColor = false` |
| Navigation Compose | `NavHost` + `NavController` + argumentos de ruta (`gameId`) |
| Control de versiones (GitHub) | Ramas por tarea, PRs y commits descriptivos en español |

**Extras sobre lo pedido:** autenticación con Firebase, búsqueda, favoritos por cuenta, roles y panel de administración, y reglas de seguridad de Firestore.

---

## 👤 Autor

[@Cardozo-Mosquera](https://github.com/Cardozo-Mosquera)