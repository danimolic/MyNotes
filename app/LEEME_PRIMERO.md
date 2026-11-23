# MyNotes – cómo incorporarlo a Android Studio

Este paquete contiene SOLO el código y los recursos (carpeta `app/src/main`), para que lo pegues
sobre un proyecto nuevo creado con la plantilla **Empty Activity** de tu versión de Android Studio.
Así los archivos de Gradle son los que tu Studio espera y no hay choques de versiones.

## 1. Crear el proyecto base
File > New > New Project > **Empty Activity** (Jetpack Compose)
- Name: `MyNotes`
- Package name: `com.example.mynotes`   (debe ser exactamente este)
- Language: Kotlin
- Minimum SDK: **API 29 (Android 10)**
- Build configuration language: Kotlin DSL
Espera a que termine el primer Gradle Sync.

## 2. Agregar Room y KSP (3 archivos de Gradle)
**gradle/libs.versions.toml**
```toml
[versions]
room = "2.8.5"
ksp = "2.3.10"        # si el Sync no la encuentra, usa la última de github.com/google/ksp/releases

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```
(agrega cada línea dentro de la sección que ya existe; no dupliques los encabezados)

**build.gradle.kts (raíz del proyecto)** – dentro de `plugins { }`:
```kotlin
alias(libs.plugins.ksp) apply false
```
**app/build.gradle.kts** – dentro de `plugins { }`:
```kotlin
alias(libs.plugins.ksp)
```
y dentro de `dependencies { }`:
```kotlin
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)
```
Pulsa **Sync Now**.

## 3. Copiar los archivos
Copia la carpeta `app/src/main/` de este paquete sobre la de tu proyecto, aceptando **reemplazar**:
- `java/com/example/mynotes/` (MainActivity, NoteViewModel, data/, ui/, ui/theme/)
- `res/drawable-nodpi/` (tus PNG ya recortados y renombrados)
- `res/values/strings.xml`
No toques `AndroidManifest.xml`: no necesita permisos (la imagen se elige con el selector del sistema).

## 4. Ejecutar
Emulador o teléfono con Android 10 o superior > Run.

## Qué hace cada PNG (cambia el uso en un solo lugar si lo interpreté mal)
| Archivo original | Drawable | Dónde se usa |
|---|---|---|
| Pantalla_1_de_notas | fondo_principal | Fondo de la lista |
| boton_nueva_nota | btn_nueva_nota | Botón para crear nota (NotesScreen) |
| nota_guardada | card_nota | Tarjeta de cada nota en la lista (NoteItem) |
| funcion_deslizar_para_eliminar... | btn_eliminar | Aparece por debajo de la tarjeta al deslizarla |
| mensaje_no_has_creado_una_nota | msg_sin_notas | Mensaje cuando no hay notas |
| fondo_pantalla_de_notas | fondo_editor | Fondo del editor |
| Titulo | titulo_hint | Texto guía del título (editor) |
| Boton_para_meterse_a_la_nota | libreta_guia | Hoja con líneas y guía, visible mientras la nota está vacía |
| libreta_demostrativa_con_la_nota | libreta_vacia | Hoja donde se escribe la descripción |
| Boton_adjuntar_archivo | btn_adjuntar | Adjuntar imagen |
| boton_regresar | btn_regresar | Guardar y volver |
| mensaje_de_nota_guardada | msg_nota_guardada | Confirmación al guardar |

## Preparativos opcionales
- Tus PNG medían 712x1600 con transparencia; ya los recorté a su contenido (no tienes que hacer nada).
- Hoja con líneas SIN texto: la hoja con líneas ("libreta_guia") trae escrito "Insertar Título/Descripción",
  por eso solo se muestra con la nota vacía. Si exportas una versión con líneas y sin texto, reemplaza
  `libreta_vacia.png` y las líneas se verán siempre.
- Tipografía manuscrita: copia tu .ttf a `res/font` y cambia `NoteFont` en `ui/theme/Type.kt`.
- Si tu Studio no abre/sincroniza: instala el SDK Platform más reciente (SDK Manager), usa JDK 17+ (el
  integrado) y conexión a internet para el primer Sync.
