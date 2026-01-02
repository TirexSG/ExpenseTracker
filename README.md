# 💰 Expense Tracker

Aplicación Android de seguimiento de gastos construida con arquitectura moderna y buenas prácticas de testing.

## 📊 Estado del Proyecto

<img src="https://github.com/TirexSG/ExpenseTracker/actions/workflows/android-ci.yml/badge.svg?branch=develop" alt="Android CI">

## 🎬 Demo (20s)

<a href="https://github.com/TirexSG/ExpenseTracker/releases/download/v0.1.0/expensetrackerdemo.mp4">
  <img src="assets/demo.gif" width="320" alt="Demo" />
</a>

## 🖼️ Capturas

<p align="left">
  <img src="assets/screenshots/01.jpeg" width="250" />
  <img src="assets/screenshots/02.jpeg" width="250" />
  <img src="assets/screenshots/03.jpeg" width="250" />
</p>

## 🏗️ Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM + Clean Architecture
- **Base de datos:** Room
- **Inyección de dependencias:** Hilt
- **Asincronía:** Coroutines + Flow
- **Navegación:** Type-Safe Navigation Compose
- **Testing:** JUnit, MockK, Turbine, Coroutines Test

## 🧪 Testing

- **Tests:** Incluye tests unitarios (ViewModels, UseCases) y tests instrumentados en `androidTest` (persistencia y base de datos)
- **Cobertura:** 83% en capa de dominio
- **CI/CD:** GitHub Actions con testing automatizado

**Ejecutar tests unitarios:**
```
./gradlew test
```

**Ejecutar tests instrumentados (UI y base de datos):**
```
./gradlew connectedAndroidTest
```

**Generar reporte de cobertura:**
```
./gradlew jacocoTestReport
```
El reporte estará en: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

## 📱 Funcionalidades

- ✅ Crear y editar gastos
- ✅ Filtrar gastos por categorías
- ✅ Eliminar gastos al deslizar
- ✅ Categorizar gastos (Comida, Transporte, Compras, etc.)
- ✅ Seguimiento de gastos del mes actual
- ✅ Dashboard de estadísticas (MVP disponible)
- ⏳ Estadísticas avanzadas (próximamente)

## 🏛️ Arquitectura

Clean Architecture con separación en tres capas:

- **Domain:** Lógica de negocio (UseCases, Models, interfaces de Repository)
- **Data:** Fuentes de datos (Room, implementación de Repository)
- **UI:** Capa de presentación (Compose, ViewModels)
- **Inyección de dependencias:** Hilt

## 🎓 Proyecto de Aprendizaje

Construido para practicar y demostrar:

- Arquitectura limpia y código escalable
- Testing profesional (patrón AAA, mocking, testing de Flows)
- Buenas prácticas de Kotlin y Compose
- CI/CD con GitHub Actions

## 📦 Setup

1. Clona el repositorio.
2. Abre en Android Studio Hedgehog+.
3. Sync Gradle.
4. Ejecuta la app.

**Requisitos:**

- Android Studio Hedgehog+
- JDK 17
- Android SDK 26+

## 📄 Licencia

Proyecto de aprendizaje personal.
