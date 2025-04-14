# 🏨 Hotel Management System (Java + MySQL)

A **console-based Hotel Management System** built using **Java** with **MySQL database connectivity**. This project allows hotel staff to manage room bookings, check-ins, check-outs, customer data, and billing operations efficiently via a terminal interface.

---

## 📌 Features

- ✅ Book rooms by **room type** (Single, Double, Suite)
- 🔍 Automatically checks for **room availability**
- 📅 Records **check-in and check-out dates**
- 📆 Calculates **duration of stay** based on timestamps
- 💰 Computes **total billing** based on price per room and stay
- 🔄 **Updates availability** in real time
- 🧾 Shows **generated Customer ID** after booking
- 📋 View all booking records with customer and billing info

---

## 🛠️ Tech Stack

- **Java** (JDK 8+)
- **MySQL** (via JDBC)
- **JDBC Driver**: `mysql-connector-java`
- **IDE**: IntelliJ / Eclipse / VS Code

---

## 🗄️ Database Schema

### 1. `rooms` Table

| Column        | Type         | Description                     |
|---------------|--------------|---------------------------------|
| room_no       | INT (PK)     | Unique Room Number              |
| room_type     | VARCHAR      | Single / Double / Suite         |
| price_per_day | DECIMAL      | Price per day in INR            |
| is_available  | BOOLEAN      | Availability flag (true/false)  |

### 2. `customers` Table

| Column        | Type         | Description                         |
|---------------|--------------|-------------------------------------|
| id            | INT (PK, AI) | Customer ID                         |
| name          | VARCHAR      | Customer name                       |
| phone         | VARCHAR      | Contact number                      |
| room_no       | INT (FK)     | Room number booked                  |
| check_in      | DATETIME     | Date and time of check-in           |
| check_out     | DATETIME     | Date and time of check-out          |
| days_stayed   | INT          | Duration of stay in days            |
| total_bill    | DECIMAL      | Final bill based on room & days     |

---

## 📥 Installation & Setup

1. **Clone the Repository**

```bash
git clone https://github.com/your-username/hotel-management-system.git
cd hotel-management-system
