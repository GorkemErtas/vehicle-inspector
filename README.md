# 🚗 Vehicle Inspector

Vehicle Inspector is an AI-powered mobile vehicle damage inspection system developed with **Spring Boot**, **FastAPI**, **YOLO**, **Gemini**, **PostgreSQL**, and **Flutter**.

The system analyzes vehicle images, detects visible damage, identifies affected vehicle parts, recommends repair actions, and generates a user-friendly AI inspection report with a city-aware estimated repair price range.

---

# ✨ Features

- 🔐 JWT Authentication & Authorization
- 🔄 Persistent Login & Automatic Session Restoration
- 👤 User Registration, Profile & Secure Logout
- 🚙 Vehicle Management
- 📍 City-Based Inspection Context
- 📷 Camera / Gallery Vehicle Image Upload
- 🤖 YOLO-Based Damage Detection
- 🧩 Multiple Affected Vehicle Part Detection
- ⚠️ Damage Severity Classification
- 🎯 Model Confidence Scoring
- 🔧 Repair Action Recommendation
- 🧠 Gemini-Powered Inspection Reports
- 💰 City-Aware Repair Cost Estimation
- 🔄 Retryable AI Report Generation
- 📊 Inspection History
- 🗄 PostgreSQL Database
- 🌐 RESTful API

---

# 🏗 Architecture

```text
Flutter Mobile Application
            │
            ▼
     Spring Boot REST API
            │
     ┌──────┼───────────────┐
     │      │               │
     ▼      ▼               ▼
PostgreSQL  FastAPI       Gemini API
              │               │
         YOLO Models      AI Report
              │          + Price Range
              ▼
      Damage / Part Analysis
```

- **Flutter** provides the mobile application and complete end-to-end inspection experience.
- **Spring Boot** handles authentication, vehicles, inspections, persistence, AI orchestration, and report lifecycle management.
- **FastAPI** performs image analysis using YOLO models.
- **YOLO** detects damage types and affected vehicle parts.
- **Gemini** converts structured ML results into a user-friendly inspection report and estimates a repair price range based on vehicle, damage, and selected city context.
- **PostgreSQL** stores users, vehicles, inspections, detections, repair recommendations, report status, and generated inspection reports.

---

# 🛠 Tech Stack

### Mobile

- Flutter
- Dart

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- Maven

### AI & LLM

- Python
- FastAPI
- Ultralytics YOLO
- PyTorch
- Roboflow
- Google Gemini API

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

```text
User Login
     │
     ▼
Create / Select Vehicle
     │
     ▼
Create Inspection
+ Select City
     │
     ▼
Upload Vehicle Image
     │
     ▼
FastAPI / YOLO Analysis
     │
     ├── Damage Detection
     ├── Affected Part Detection
     ├── Damage Severity
     └── Repair Recommendation
     │
     ▼
Persist ML Results
     │
     ▼
Gemini Report Generation
     │
     ├── Human-Readable Damage Report
     ├── Repair Recommendation Explanation
     └── City-Aware Estimated Price Range
     │
     ▼
Save Inspection Report
     │
     ▼
Return Complete Inspection Result
```

The ML inspection result and the Gemini report have separate statuses. If Gemini report generation temporarily fails, the completed ML analysis remains available and the report can be regenerated without running YOLO again.

---

# 🤖 Example Inspection Response

```json
{
  "status": "COMPLETED",
  "reportStatus": "COMPLETED",
  "reportMessage": null,
  "damageSeverity": "SEVERE",
  "damageTypes": [
    "BROKEN_PART",
    "DENT"
  ],
  "affectedParts": [
    "HEADLIGHT",
    "GRILLE",
    "FRONT_BUMPER"
  ],
  "confidenceScore": 0.9212,
  "locationCity": "Izmir",
  "repairRecommendations": [
    {
      "damageType": "BROKEN_PART",
      "recommendedAction": "PART_REPLACEMENT",
      "partReplacementRequired": true,
      "affectedParts": [
        "HEADLIGHT",
        "GRILLE",
        "FRONT_BUMPER"
      ]
    }
  ],
  "report": {
    "title": "Honda City Hasar Tespiti ve Onarım Raporu",
    "estimatedMinimumPrice": 27000.00,
    "estimatedMaximumPrice": 34000.00,
    "currency": "TRY",
    "priceInformation": "Estimated repair cost based on the vehicle, detected damage, repair requirements, and selected city.",
    "disclaimer": "The price range is an AI-generated market estimate and is not a final service quote."
  }
}
```

---

# 🧠 AI Inspection Report & Price Estimation

Repair prices are no longer managed through administrator-defined database records.

After YOLO completes the vehicle damage analysis, Spring Boot sends structured inspection information to Gemini, including:

- Vehicle brand and model
- Model year
- Mileage
- Selected city
- Damage severity
- Detected damage types
- Affected vehicle parts
- Recommended repair actions
- Part replacement requirements
- Model confidence information

Gemini uses this context to generate:

- A readable damage summary
- Detailed damage description
- Repair recommendation explanation
- Estimated minimum repair price
- Estimated maximum repair price
- Price reasoning
- A user-facing disclaimer

The current demo does **not** use live web-search pricing. The estimate is generated from the vehicle, damage, selected city, and general market context available to the language model.

To keep the estimate practical for the demo, the difference between the generated minimum and maximum price is limited to **10,000 TRY**.

---

# 🔄 AI Report Retry Flow

ML analysis and LLM report generation are handled independently.

```text
ML Analysis
    │
    ├── Success → InspectionStatus.COMPLETED
    │
    ▼
Gemini Report
    │
    ├── Success → ReportStatus.COMPLETED
    │
    └── Failure → ReportStatus.FAILED
```

If Gemini fails because of a temporary API or quota issue, the inspection is not marked as failed.

The existing ML result can be reused through:

```http
POST /api/v1/inspections/{inspectionId}/report
```

This regenerates only the AI report and does not rerun the YOLO image analysis.

---

# 🔒 Security

- JWT Authentication
- BCrypt Password Encryption
- Stateless Authorization
- Role-Based Access Control
- Secure Mobile Token Storage

---

# 🚀 Future Improvements

- 📄 PDF Damage Reports
- 🎯 Larger and More Diverse Damage Detection Dataset
- 🎯 Improved Vehicle-Part Classification
- 📷 Image Quality / Retake Validation
- 🌐 Optional Live Pricing / Search Grounding
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

- ✅ JWT Authentication & Authorization
- ✅ User Registration & Login
- ✅ Persistent Mobile Sessions
- ✅ Automatic Session Restoration
- ✅ User Profile & Logout
- ✅ Vehicle Management
- ✅ Inspection Management
- ✅ AI Damage Detection
- ✅ Multiple Affected Part Detection
- ✅ Damage Severity Classification
- ✅ Repair Recommendation
- ✅ City Selection for Inspections
- ✅ Gemini AI Report Generation
- ✅ City-Aware Repair Price Estimation
- ✅ Persistent Inspection Reports
- ✅ AI Report Status Management
- ✅ Retryable AI Report Generation
- ✅ Flutter Mobile Application
- ✅ Inspection Result Screen
- ✅ Inspection History
- ✅ End-to-End Mobile Inspection Flow

### In Progress

- 🔄 ML Model Improvements
- 🔄 Increase AI output accuracy
- 🔄 PDF Report Generation
