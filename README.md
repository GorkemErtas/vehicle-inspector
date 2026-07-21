# 🚗 Vehicle Inspector

An AI-powered vehicle damage inspection system that automatically analyzes vehicle images, detects visible damage, and estimates repair recommendations using a hybrid Spring Boot + FastAPI architecture.

## 📌 Overview

Vehicle Inspector is a full-stack backend project designed to automate vehicle damage assessment.

The system allows users to:

- Register and authenticate securely using JWT
- Manage their vehicles
- Upload vehicle damage images
- Analyze images with an AI service
- Detect damage type and severity
- Identify the damaged vehicle part
- Recommend repair actions
- Store inspection history for future reference

Unlike traditional AI-only solutions, repair cost calculation is handled by the backend using business rules and database-driven pricing, making the system easier to maintain and extend.

---

# ✨ Features

- 🔐 JWT Authentication & Authorization
- 👤 User Management
- 🚙 Vehicle Management
- 📷 Image Upload
- 🤖 AI-powered Damage Detection
- 🧩 Vehicle Part Identification
- ⚠️ Damage Severity Classification
- 🔧 Repair Recommendation
- 📊 Inspection History
- 🗄 PostgreSQL Database
- 🌐 RESTful API
- 📝 Clean Layered Architecture

---

# 🏗 Architecture

```
                +----------------+
                |     Client     |
                +-------+--------+
                        |
                        |
                 Spring Boot API
                        |
        +---------------+----------------+
        |                                |
 PostgreSQL                      FastAPI AI Service
        |                                |
 Vehicle Data               YOLO / Computer Vision
 Repair Prices              Damage Detection
 Inspection History         Classification
```

The project follows a microservice-style architecture:

- **Spring Boot** handles authentication, business logic, data persistence and pricing.
- **FastAPI** performs AI-based image analysis.
- **PostgreSQL** stores application data.

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT Authentication
- Maven

## AI Service

- Python
- FastAPI
- OpenCV
- YOLO (Ultralytics)

## Database

- PostgreSQL

## Tools

- IntelliJ IDEA
- VS Code
- Postman
- Git
- GitHub

---

# 📂 Project Structure

```
vehicle-inspector/
│
├── backend/
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── mapper
│   ├── security
│   └── config
│
├── ai-service/
│   ├── models
│   ├── routes
│   ├── services
│   └── main.py
│
└── uploads/
```

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
Spring Boot
      │
      ▼
FastAPI AI Analysis
      │
      ▼
Damage Detection
      │
      ▼
Vehicle Part Detection
      │
      ▼
Repair Recommendation
      │
      ▼
Save Inspection Result
```

---

# 🤖 AI Output

The AI service returns:

- Damage Type
- Damage Severity
- Vehicle Part
- Recommended Repair Action
- Replacement Requirement
- Confidence Score
- Analysis Message

Example:

```json
{
  "damageType": "SCRATCH",
  "damageSeverity": "MINOR",
  "vehiclePart": "FRONT_BUMPER",
  "recommendedAction": "POLISHING",
  "partReplacementRequired": false,
  "confidenceScore": 0.91,
  "analysisMessage": "Minor scratch detected on the front bumper."
}
```

---

# 🔒 Security

- JWT Authentication
- Stateless Authorization
- Password Encryption (BCrypt)
- Protected REST Endpoints

---

# 🚀 Future Improvements

- Repair cost estimation using pricing database
- Frontend (React)
- Damage report PDF generation
- AI model improvements
- Insurance claim integration
- Docker support
- CI/CD Pipeline
- Cloud deployment (AWS)

---

# 📸 Screenshots

> Screenshots of the API, Postman requests and future React frontend will be added here.

---

# 👨‍💻 Author

**Görkem**

Software Engineer

- Java
- Spring Boot
- REST APIs
- PostgreSQL
- AI Integration
- Computer Vision

---

# ⭐ Project Status

🚧 **Actively under development**

Current Progress:

- ✅ JWT Authentication
- ✅ Vehicle CRUD
- ✅ Inspection CRUD
- ✅ Image Upload
- ✅ AI Integration
- ✅ Damage Analysis
- 🔄 Repair Cost Engine
- 🔄 React Frontend
