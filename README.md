# SC2002-Group-Project
# 🏙️ Build-To-Order (BTO) Management System

## 📋 Project Overview

This is a command-line application built as part of the SC2002 Object-Oriented Design & Programming course at NTU. The system simulates a real-world Build-To-Order (BTO) housing application platform, where **Applicants**, **HDB Officers**, and **HDB Managers** interact with BTO projects in role-specific ways.

---
## 🧠 Team Members

| Name                          | Student ID |                
|-------------------------------|------------|
| Yeo Yu Xuan Dazzel            | U2423800E  |
| Mohamed Fahath Mohammed Adhil | U2423664G  |
| Zhang Yuhe                    | U2422060C  | 
| Ng Zheng Da                   | U2322077H  | 
| Huang Yitian                  | U2423017H  | 
---

## 👨‍💻 Features

### ✅ Core Features
- User login via NRIC and password (Singpass simulation)
- Role-based dashboard for:
  - **Applicants**: View/apply/withdraw BTO projects, submit enquiries, receive notifications.
  - **HDB Officers**: Handle bookings, respond to enquiries, generate receipts.
  - **HDB Managers**: Create/manage projects, approve/reject applications and officer registrations, generate reports.
- Flat application workflow with eligibility rules
- Enquiry management and notifications
- Flat booking management
- Persistent in-session filters and system-wide validation

### 🌟 Additional Features
- Notification system for user actions
- Persistent filter settings across menus

---

## 🛠️ Technologies Used

- **Language**: Java 23
- **IDE**: IntelliJ IDEA / Visual Studio Code
- **Version Control**: GitHub
- **UML Tool**: Visual Paradigm

---

## 📂 Project Structure (MVC Style)

```plaintext
├── entity/            # Domain models (e.g., Applicant, Project, Enquiry)
├── controller/        # Business logic classes (e.g., ProjectController)
├── boundary/          # CLI User Interfaces for different roles
├── utility/           # Shared helpers (e.g., CSVFileHandler)
├── Main.java          # Application entry point
```

---

## 🧪 Testing Strategy

- **Approach**: Manual black-box testing
- **Coverage**: Login validation, project visibility, flat application/booking workflows, enquiry handling, role-based restrictions
- See the full report for a detailed test case table (30+ cases covered)

---

## 📈 Object-Oriented Principles Applied

- **Single Responsibility**: Each class encapsulates one concept
- **Open/Closed**: Abstract classes and interfaces used to promote extensibility
- **Liskov Substitution**: All subclasses override base behavior correctly
- **Interface Segregation**: Role-based controller interfaces avoid bloat
- **Dependency Inversion**: Interfaces separate data access logic from business logic

---

## 🧾 How to Run

1. Ensure Java 23 is installed.
2. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/bto-management-system.git
   ```
3. Navigate to the project root directory.
4. Compile and run:
   ```bash
   javac Main.java
   java Main
   ```

---

## 🔍 GitHub Repository

[🔗 Click here to view the repository](https://github.com/JamesHackerMP/SC2002-Group-Project.git)

---

## 📜 License

This project is for academic purposes only and is not intended for production use.

---


