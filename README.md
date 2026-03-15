# Smart Campus Companion (Phase 2)

## 📱 App Description
Smart Campus Companion is a robust Android application built with **Jetpack Compose** designed to streamline student life. Moving beyond static layouts, Phase 2 introduces dynamic data persistence and a scalable architecture.

### **Phase 2 Technical Milestones:**
* **User Authentication System:** Secure Registration and Login powered by **Room Database**, featuring password validation and session persistence via **SharedPreferences**.
* **Dynamic Task Manager:** A full CRUD (Create, Read, Update, Delete) module for personal academic tracking.
* **Interactive Announcement Hub:** Real-time filtering (Read/Unread states) and "Smart Seeding" for campus-wide notices.
* **Architecture:** Implementation of the **MVVM (Model-View-ViewModel)** pattern for clean separation of concerns.
* **Local Persistence:** A multi-table SQLite database managed via Room for offline-first reliability.

---

## 👥 The Team

| Role | Name | Responsibility |
| :--- | :--- | :--- |
| **Team Leader** | Alvin Matthew Ortiz | Project oversight, architecture design, and merge approvals. |
| **Git Manager** | Alvin Matthew Ortiz | Branch strategy, pull request reviews, and conflict resolution. |
| **UI/UX Developer** | Eloisa Papagayo | Jetpack Compose styling, theme engine, and interactive components. |
| **Feature Developer** | Gabriela Anne Pantaleon | Room DAO implementation, ViewModel logic, and data filtering. |
| **QA / Documenter** | Andrei Vincent Parala | Logic testing, bug tracking, and technical documentation. |

---

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Database:** Room (SQLite)
* **Navigation:** Compose Navigation
* **Local Storage:** SharedPreferences (Theme & Session management)
* **Concurrency:** Kotlin Coroutines & StateFlow

---

## 🖇️ Git Workflow (Advanced)

Phase 2 utilized a **Feature Branch Workflow** to manage complex database migrations and multi-member integration.

### **Branching Strategy**
* **`main`**: The production-ready stable core.
* **`feature/` branches**: Individual modules (e.g., `feature/task-manager`, `feature/announcements`) developed in isolation to prevent breaking the main build.

### **The "Midterm" Challenge: Merge Conflict Resolution**
During the integration of the `Registration` and `Login` modules, the team encountered a **Merge Conflict** in the `AppDatabase.kt` file. 
* **The Cause:** Two members added different entities (User and Announcement) to the Database class simultaneously.
* **The Fix:** Resolved manually by merging the DAO declarations and incrementing the Database version to `3`, utilizing `fallbackToDestructiveMigration()` to ensure schema stability during testing.

### **Commit Standard**
Commits are prefixed by the module name for clarity:
* `[Room] Implemented UserDao and Registration logic - Pantaleon`
* `[UI] Added Filter Chips to Announcement screen - Papagayo`

---
> **Note:** This project is part of the Midterm requirement for the College of Computer Studies at Pamantasan ng Cabuyao.
