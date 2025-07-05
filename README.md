# Recruitment Agent

![Java](https://img.shields.io/badge/Backend-Java%2017%2B-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-brightgreen?logo=springboot)
![Angular](https://img.shields.io/badge/Frontend-Angular-red?logo=angular)
![License](https://img.shields.io/badge/license-MIT-green)

Aplicación web para la gestión de procesos de reclutamiento, compuesta por un backend en Java (Spring Boot) y un frontend en Angular. Permite analizar la compatibilidad de candidatos con vacantes, gestionar vacantes y visualizar métricas avanzadas.

---

## 🚦 Stack Tecnológico

- **Backend:** Java 17+, Spring Boot, Maven, Spring Data JPA, H2/MySQL (configurable), Apache POI (exportación Excel)
- **Frontend:** Angular, TypeScript, HTML5, CSS3
- **Otros:** Cohere API (análisis de compatibilidad), FileSaver.js (descarga de archivos en frontend)

---

<details>
<summary><b>📁 Ver estructura del proyecto</b></summary>

```
Recruitment Agent VSC/
  backend/    # API REST en Java Spring Boot
    src/main/java/com/example/recruitmentagent/
      controller/   # Controladores REST (CV, Vacantes, Métricas)
      model/        # Entidades JPA (CV, Vacancy, Metrics)
      repository/   # Repositorios Spring Data
      service/      # Lógica de negocio y análisis de CVs
    src/main/resources/
      application.properties  # Configuración de base de datos y app
  frontend/   # Aplicación Angular
    src/app/
      cvs/           # Gestión y análisis de CVs (solo texto pegado)
      vacancies/     # Gestión de vacantes
      metrics/       # Visualización de métricas y exportación
      filters/       # Filtros avanzados de búsqueda
      shared.service.ts # Servicios compartidos
```
</details>

---

## ✨ Características Principales

- 🚀 **Análisis de compatibilidad** de candidatos con vacantes (en español, usando IA).
- 📝 **Gestión de vacantes:** crear, editar, eliminar y listar vacantes.
- 👤 **Gestión de candidatos:** registrar candidatos pegando texto de CV, listar, buscar y filtrar.
- 📊 **Métricas avanzadas:** total de vacantes, promedio de experiencia, top skills, distribución educativa, vacante con más candidatos, fecha del último CV procesado, y más.
- 📥 **Exportación profesional a Excel** de métricas, con gráficos y estilos.
- ✅ **Validaciones y UI moderna:** formularios verticales, validación de campos, modales para edición, confirmaciones y mensajes de éxito.
- ❌ **Sin subida de archivos:** el análisis se realiza únicamente pegando el texto del CV.

---

## 🔗 Endpoints principales (Backend)

| Recurso    | Método | Endpoint                                 | Descripción                                 |
|------------|--------|------------------------------------------|---------------------------------------------|
| CVs        | POST   | `/cvs`                                   | Registrar un CV                             |
| CVs        | GET    | `/cvs`                                   | Listar todos los CVs                        |
| CVs        | GET    | `/cvs/search`                            | Buscar CVs por skills, experiencia, ubicación|
| CVs        | PUT    | `/cvs/{id}`                              | Editar datos de un CV                       |
| CVs        | DELETE | `/cvs/{id}`                              | Eliminar un CV                              |
| CVs        | GET    | `/cvs/apt-for-vacancy/{vacancyId}`       | Listar candidatos aptos para una vacante    |
| Vacantes   | POST   | `/vacancies`                             | Crear una vacante                           |
| Vacantes   | GET    | `/vacancies`                             | Listar todas las vacantes                   |
| Vacantes   | GET    | `/vacancies/search`                      | Buscar vacantes                             |
| Vacantes   | PUT    | `/vacancies/{id}`                        | Editar una vacante                          |
| Vacantes   | DELETE | `/vacancies/{id}`                        | Eliminar una vacante                        |
| Vacantes   | POST   | `/vacancies/{id}/publish`                | Publicar vacante (simulado)                 |
| Métricas   | GET    | `/metrics`                               | Obtener métricas generales                  |
| Métricas   | GET    | `/metrics/export`                        | Exportar métricas a Excel                   |
| Métricas   | PUT    | `/metrics`                               | Actualizar métricas manualmente (opcional)  |

---

## ⚡ Ejecución

### Backend

1. Navega a la carpeta `backend`:
   ```powershell
   cd backend
   ```
2. Compila y ejecuta:
   ```powershell
   mvn spring-boot:run
   ```
   O ejecuta el JAR:
   ```powershell
   java -jar target/recruitment-agent-1.0.0.jar
   ```

### Frontend

1. Navega a la carpeta `frontend`:
   ```powershell
   cd frontend
   ```
2. Instala dependencias:
   ```powershell
   npm install
   ```
3. Inicia la app:
   ```powershell
   ng serve
   ```
   Accede a `http://localhost:4200`.

> **Nota:** El backend corre por defecto en `http://localhost:8080` y el frontend en `http://localhost:4200`.

---

## 📝 Notas adicionales

- ⚙️ Configura la base de datos en `backend/src/main/resources/application.properties`.
- 🔄 El frontend requiere el backend activo para funcionar correctamente.
- 🌎 El análisis de compatibilidad y las métricas se muestran en español.
- 🧪 Para pruebas, puedes usar descripciones de candidatos y vacantes variadas (ver ejemplos en la documentación interna o solicita ejemplos).

---

## 👨‍💻 Autor

Forte Innovation — CID
