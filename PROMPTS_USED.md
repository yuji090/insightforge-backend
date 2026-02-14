# Prompts Used During Development

This document records the exact prompts used during development of the InsightForge application.

No API keys or secrets are included.

---

## 1️⃣ Insight Generation Prompt

Used in: OpenAiService.generateInsights()

"You are a data analyst.

Analyze the dataset below and return ONLY valid JSON in this format:

{
"summary": "...",
"key_trends": ["..."],
"outliers": ["..."],
"recommended_checks": ["..."]
}

Do not include markdown or backticks.

Dataset:
<CSV preview JSON data>
"

Purpose:
- Generate structured analytics output
- Force strict JSON format
- Avoid markdown formatting issues
- Ensure predictable frontend parsing

Model Used:
gpt-4o-mini

---

## 2️⃣ Follow-up Question Prompt

Used in: OpenAiService.generateFollowUpAnswer()

"You are a data analyst.

Dataset:
<raw CSV data>

Previous Insights:
<summary JSON>

User Question:
<user question>

Give a clear and direct answer in plain text.
Do NOT return JSON.
Do NOT use markdown.
Just return a simple explanation.
"

Purpose:
- Answer contextual follow-up questions
- Combine dataset + previously generated insights
- Force plain text output (no JSON)
- Avoid formatting artifacts

Model Used:
gpt-4o-mini

---

## 3️⃣ UI Development Assistance Prompts

Used during frontend refinement:

- Improve Tailwind UI layout
- Make control bar sticky
- Add loading spinners
- Improve drag-and-drop UX
- Improve follow-up answer rendering
- Add proper error handling UI

---

## 4️⃣ Backend Design Assistance Prompts

- How to structure upload → analyze → save flow
- How to separate preview vs full raw CSV
- How to protect API keys in production
- How to configure Spring profiles
- How to structure explicit save logic
