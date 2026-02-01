# Smart Campus Companion

## App Description

Smart Campus Companion is an Android application developed as part of an Android Development case study. The application is designed to demonstrate proper Android project structure, clean architecture layering, and Git collaboration practices.

The project follows a layered architecture approach and focuses on building a scalable and maintainable codebase using Kotlin, Jetpack Compose, and ViewModel-based state management.

---

## Program Structure

app/
├── data/ # Data Layer
│ ├── model/
│ │ ├── User.kt # Data models
│ │ └── Todo.kt
│ ├── remote/
│ │ ├── ApiService.kt # API interface
│ │ └── RetrofitClient.kt # Network client
│ └── repository/
│ └── UserRepository.kt # Data repository
│
├── domain/ # Domain Layer
│ ├── UserState.kt # UI state definitions
│ └── TodoState.kt
│
├── ui/ # Presentation Layer
│ ├── screens/
│ │ ├── UserListScreen.kt # UI components
│ │ ├── LoadingScreen.kt
│ │ ├── ErrorScreen.kt
│ │ ├── TodoScreen.kt
│ │ └── MainScreen.kt # Screen orchestrator
│ └── viewmodel/
│ ├── UserViewModel.kt # Business logic
│ └── TodoViewModel.kt
│
└── MainActivity.kt # App entry point


---

## Architecture Layers

### Data Layer
- Handles all data sources (API, database, etc.)
- Manages network calls using Retrofit
- Provides data to other layers via repositories

### Domain Layer
- Contains business logic
- Defines UI states using sealed classes
- Independent of Android framework components

### Presentation Layer
- Displays UI using Jetpack Compose
- ViewModels manage UI state using StateFlow
- Responds to user interactions and state changes

---

## Data Flow
UI → ViewModel → Repository → ApiService → Network
→ Result → ViewModel updates StateFlow
→ UI recomposes automatically


---

## Team Roles

| Role | Responsibility |
|-----|---------------|
| Alvin Matthew Ortiz | Team Leader | Oversees progress and approves merges |
| Alvin Matthew Ortiz | Git Manager | Manages branches and pull requests |
| Eloisa Papagayo | UI/UX Developer | Designs layouts and user experience |
| Gabriela Anne Pantaleon | Feature Developer | Implements application logic |
| Andrei Vincent Parala | QA / Documenter | Testing and documentation |

---

## Git Workflow

### Branches
- `main` – stable production-ready code
- `develop` – active development branch

### Workflow Rules
1. All development is done on the `develop` branch
2. Feature work is committed in small, logical commits
3. Meaningful commit messages are required
4. `main` branch only contains tested and stable code

### Commit Message Format
short description - contributor

