# InsightForge – CSV Insights Dashboard

A full-stack AI-powered CSV analytics web application.

## Live Demo
Frontend: https://insightforge-ui.vercel.app  
Backend API: https://virtuous-vision-production.up.railway.app/api

---

## Features

- Upload CSV file
- Preview data (first 50 rows)
- Generate AI insights:
    - Summary
    - Key Trends
    - Outliers
    - Recommended Checks
- Ask follow-up questions to AI
- Save report explicitly
- View last 5 reports
- Download original CSV
- System Health page (App / DB / LLM status)

---

## Tech Stack

### Backend
- Java 17+
- Spring Boot
- JPA / Hibernate
- MySQL (Railway)
- OpenAI API
- WebClient

### Frontend
- React
- Tailwind CSS
- Axios
- Recharts (for charts)

---

## How To Run Locally

### Backend

1. Clone repo
2. Create `application-local.properties`
3. Add:
   spring.datasource.url=your_db_url
   spring.datasource.username=your_user
   spring.datasource.password=your_password
   openai.api.key=your_key
4. Run:
   mvn spring-boot:run


---

### Frontend

npm install
npm run dev


---

## What Is Done

- Full upload → analyze → save flow
- Follow-up question system
- Drag & drop upload
- Auto load latest report
- Status health check endpoint
- Production deployment (Vercel + Railway)

---

## What Is Not Done

- User authentication
- Multi-user separation
- Advanced data visualization filters
- Large file streaming optimization

---

## Deployment Notes

- Secrets are stored as environment variables
- No API keys committed to repository
- Production profile used on Railway




