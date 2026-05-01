FinSecure — Mobile Banking Application
A secure Android mobile banking application for performing financial transactions and accessing real-time economic news.

1. About the Project
FinSecure is a mobile banking application that allows users to manage their finances, perform transactions, and stay informed with real-time economic news — all in one platform.
The system focuses on security, usability, and clean architecture, implementing modern best practices such as JWT authentication, HTTPS, and server-side validation.

Goals:
Build a secure mobile banking system
Provide a user-friendly interface
Enable real-time financial operations

2. Key Features
Feature	                          Description
Authentication	          User registration & login using JWT
Account                 	View balance and account details
Money Transfer          	Send money between users
Security	                JWT, HTTPS, server-side validation
Transactions            	Transaction history tracking
Notifications           	Alerts for incoming transactions
News Integration	        Real-time economic news

3. Technology Stack
   
Backend

Technology	          Description
Node.js	            Server runtime
Express.js	        Backend framework
PostgreSQL	        Database
Prisma ORM	        Database management
JWT	                Authentication
bcrypt	            Password hashing

Frontend (Android)

Technology	              Description
Kotlin	              Main programming language
Android Studio	      Development environment
Retrofit	            API communication
OkHttp	              Network logging
Gson	                JSON parsing
MVVM	                Architecture pattern
DataStore	            Secure token storage

4. Architecture
Android App (Kotlin)
        \/
   Retrofit API
        \/
Backend (Node.js + Express)
        \/
 PostgreSQL Database

5. Project Structure
finsecure/
├── server/
│   ├── controllers/
│   ├── routes/
│   ├── middleware/
│   ├── prisma/
│   └── index.js
│
├── android-app/
│   └── app/src/main/java/
│       ├── ui/
│       ├── viewmodel/
│       ├── repository/
│       ├── network/
│       └── utils/
│
└── README.md

6. Installation & Setup
Requirements
Node.js
PostgreSQL
Android Studio

   Backend Setup
cd server
npm install
npm run dev

   Database Setup (Prisma)
npx prisma migrate dev
npx prisma generate

   Android App Setup
Open project in Android Studio
Run emulator or connect device
Click Run

8. Usage
   
7.1 Registration
Sign up using email or phone number

7.2 Login
JWT token is generated
Stored securely using DataStore

7.3 Money Transfer
Enter amount
Select recipient
Confirm transaction

7.4 Balance
Displayed on the main screen

7.5 Transaction History
View all past transactions

10. API Endpoints
Authentication
POST /api/auth/register
POST /api/auth/login
GET /api/auth/me

Account
GET /api/account
Transactions
POST /api/transfer
GET /api/transactions

News
GET /api/news


12. Security
JWT Authentication
HTTPS Encryption
Password hashing (bcrypt)
Input validation
Protection against common attacks

14. Author
This project was developed as a Bachelor’s thesis.
    Author: Nurdoolot
    Ala-Too International University



