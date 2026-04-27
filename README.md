# Smart Campus Companion (Full Phase Release)

## 📱 Project Overview
Smart Campus Companion is a comprehensive Android ecosystem built with **Jetpack Compose** designed to centralize and secure student life. The project evolved from a static UI prototype into a fully functional, multi-user application with strict data isolation and enterprise-ready architecture.

---

## 🚀 Development Roadmap

### **Phase 1: Foundations (The Blueprint)**
* **High-Fidelity UI:** Implementation of the **Material 3** design system with custom brand gradients.
* **Theming Engine:** Dynamic Dark/Light mode persistence using **DataStore**.
* **Navigation Architecture:** Centralized `AppNavGraph` utilizing Compose Navigation for seamless screen transitions.
* **Splash & Branding:** Custom animated entry sequences and brand identity (formerly Morplit, now **Morpwatch**).

### **Phase 2: Local Persistence (The Engine)**
* **User Authentication:** Secure registration and login modules powered by **Room Database**.
* **MVVM Architecture:** Strict separation of concerns using ViewModels and StateFlow for reactive UI updates.
* **CRUD Task Manager:** Initial implementation of personal academic tracking.
* **Announcement Hub:** Status-based filtering (Read/Unread) with smart database seeding.

### **Phase 3: Security & Multi-Tenancy (Current Phase)**
* **Strict Data Isolation:** Implementation of a **User-Aware architecture** where all database queries are filtered by `studentId` to prevent cross-user data leakage.
* **String-Based Identity Mapping:** Transitioned from integer IDs to **String-based UUIDs** to match official University Student ID formats (e.g., `2024-10001`).
* **Session Management:** Robust session handling using **SharedPreferences**, injecting the active User ID into ViewModels at the Navigation level.
* **Enterprise Integration:** Initial setup for **Ktor** (Networking) and **Firebase** (Cloud Auth/Messaging) for future cloud synchronization.

---

## 👥 The Team

| Role | Name | Responsibility |
| :--- | :--- | :--- |
| **Team Leader** | Alvin Matthew Ortiz | Project oversight, architecture design, and system integration. |
| **Git Manager** | Alvin Matthew Ortiz | Branch strategy, shelf management, and conflict resolution. |
| **UI/UX Developer** | Eloisa Papagayo | Jetpack Compose styling, theme engine, and interactive components. |
| **Feature Developer** | Gabriela Anne Pantaleon | Room DAO implementation, ViewModel logic, and data filtering. |
| **QA / Documenter** | Andrei Vincent Parala | Logic testing, bug tracking, and technical documentation. |

---

## 🛠️ Tech Stack
* **Language:** Kotlin (Coroutines & StateFlow)
* **UI Framework:** Jetpack Compose (Material 3)
* **Database:** Room SQLite (Multi-Table with Foreign Key logic)
* **Session:** SharedPreferences (String-based Session Persistence)
* **Networking:** Ktor Client (Content Negotiation & Serialization)
* **Cloud:** Firebase (Auth & Messaging Ready)

---

## 🖇️ Git Workflow & Troubleshooting

### **Advanced Feature Branching**
We utilized a `feature/` branch strategy to isolate high-risk changes, such as the transition from `Int` to `String` for primary keys and the implementation of session-based filtering.

### **Key Challenge: Data Leakage & Type Mismatch**
During Phase 3, we identified a critical bug where users could see each other's tasks.
* **The Root Cause:** ViewModels were initialized at the top level of the NavGraph, causing them to capture a default `-1` ID before login was complete.
* **The Fix:** Implemented **Scoped ViewModel Initialization** inside individual `composable` routes. This ensures the `user_id` is pulled from the session *after* authentication, guaranteeing that data is only fetched for the specific `studentId` logged in.

### **Database Versioning**
* **Version 1-2:** Initial setup and User/Announcement table integration.
* **Version 3:** Implemented `fallbackToDestructiveMigration()` to update the `userId` column from `Int` to `String` to support alphanumeric Student IDs and ensure schema stability.

---
> **Note:** This project is a Midterm requirement for the College of Computer Studies at **Pamantasan ng Cabuyao**. It serves as a proof-of-concept for secure, offline-first campus management systems.
