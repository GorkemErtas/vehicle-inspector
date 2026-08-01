# 🚗 Vehicle Inspector

Vehicle Inspector is an AI-powered mobile vehicle damage inspection system developed with **Spring Boot**, **FastAPI**, **YOLO**, **PostgreSQL**, and **Flutter**.

The system analyzes vehicle images, detects visible damage, identifies affected vehicle parts, recommends repair actions, and estimates repair costs using administrator-defined pricing rules.

---

# ✨ Features

- 🔐 JWT Authentication & Authorization
- 👤 User Management
- 🚙 Vehicle Management
- 📷 Vehicle Image Upload
- 🤖 AI-Powered Damage Detection
- 🧩 Multiple Affected Vehicle Part Detection
- ⚠️ Damage Severity Classification
- 🔧 Repair Action Recommendation
- 💰 Multi-Part Repair Cost Estimation
- 📋 Part-Based Repair Price Breakdown
- 📊 Inspection History
- 🗄 PostgreSQL Database
- 🌐 RESTful API

---

# 🏗 Architecture

```
Flutter Mobile Application
            │
            ▼
     Spring Boot REST API
            │
     ┌──────┴──────┐
     │             │
PostgreSQL    FastAPI AI Service
                    │
               YOLO Models
```

- **Flutter** provides the mobile application.
- **Spring Boot** handles authentication, business logic, inspections, and repair cost estimation.
- **FastAPI** performs AI-based image analysis using YOLO models.
- **PostgreSQL** stores users, vehicles, inspections, detections, and repair prices.

---

# 🛠 Tech Stack

### Mobile

- Flutter
- Dart

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- Maven

### AI Service

- Python
- FastAPI
- Ultralytics YOLO
- PyTorch
- Roboflow

### Database

- PostgreSQL

### Tools

- IntelliJ IDEA
- Android Studio
- Visual Studio Code
- Postman
- Git
- GitHub

---

# 🔄 Inspection Workflow

```
User Login
     │
     ▼
Create Vehicle
     │
     ▼
Create Inspection
     │
     ▼
Upload Vehicle Image
     │
     ▼
AI Image Analysis
     │
     ▼
Damage Detection
     │
     ▼
Affected Part Detection
     │
     ▼
Damage Severity Calculation
     │
     ▼
Repair Recommendation
     │
     ▼
Repair Cost Estimation
     │
     ▼
Save Inspection Result
```

---

# 🤖 Example AI Response

```json
{
  "damageType": "BROKEN_PART",
  "damageSeverity": "SEVERE",
  "affectedParts": [
    "HEADLIGHT",
    "GRILLE",
    "FRONT_BUMPER",
    "HOOD"
  ],
  "recommendedAction": "PART_REPLACEMENT",
  "partReplacementRequired": true,
  "confidenceScore": 0.9212,
  "estimatedMinimumPrice": 41000.00,
  "estimatedMaximumPrice": 62000.00
}
```

---

# 💰 Repair Cost Estimation

Repair costs are calculated in the backend by matching:

- Vehicle Brand
- Vehicle Model
- Model Year
- Affected Vehicle Part
- Repair Action
- Damage Severity

The system supports **multiple affected parts** and returns:

- Total minimum repair cost
- Total maximum repair cost
- Part-based repair price details

---

# 🔒 Security

- JWT Authentication
- BCrypt Password Encryption
- Stateless Authorization
- Role-Based Access Control (User / Admin)

---

# 🚀 Future Improvements

- 📱 Flutter Mobile UI
- 📄 PDF Damage Reports
- 🎯 Improved AI Models
- 🐳 Docker Support
- ☁️ Cloud Deployment
- 🔔 Push Notifications

---

# 👨‍💻 Author

**Görkem Ertaş**

Software Engineer

---

# ⭐ Project Status

🚧 **Actively under development**

### Completed

- ✅ Authentication & Authorization
- ✅ Vehicle Management
- ✅ Inspection Management
- ✅ AI Damage Detection
- ✅ Multiple Affected Part Detection
- ✅ Damage Severity Classification
- ✅ Repair Recommendation
- ✅ Multi-Part Repair Cost Estimation
- ✅ Part-Based Price Breakdown

### In Progress

- 🔄 Flutter Mobile Application
- 🔄 AI Model Improvements
- 🔄 PDF Report Generation
