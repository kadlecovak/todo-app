# Todo App 

This project is a clean and minimalist Todo application.

## Key Features
* Add, edit, and delete tasks.
* Support for task priorities (High, Medium, Low) and Due Dates.
* Filtering and sorting handled by the database (sort by ID, Priority, or Due Date).
* Responsive, clean, and practical design without unnecessary visual clutter.

## How to Run the Project

### 1. Running the Backend
Open your terminal and navigate to the backend directory:
`cd todo-backend`

Run the Spring Boot application using the provided Maven wrapper (the database and tables will be initialized automatically):
* On Windows: `mvnw.cmd spring-boot:run`
* On Mac/Linux: `./mvnw spring-boot:run`

The backend server will start on port **8080** (`http://localhost:8080`).

### 2. Running the Frontend
Open a new terminal window and navigate to the frontend directory:
`cd todo-frontend`

Install the required dependencies:
`npm install`

Start the development server:
`npm run dev`

The application will be available in your browser (default is `http://localhost:5173`).