# [English](README.md) | [Español](README.ES.md)

---

<div align="center">

# 🖥️ Panel de Monitoreo con IA

**Monitoreo en tiempo real de infraestructura para servicios distribuidos de IA**

[![Spring Boot 4.1](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 25](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/25/)
[![Next.js 16](https://img.shields.io/badge/Next.js-16-black?logo=next.js)](https://nextjs.org)
[![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)](https://react.dev)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org)

![Vista Previa del Dashboard](https://placehold.co/1200x600/0F172A/6366F1?text=AI+Monitoring+Dashboard&font=roboto)

</div>

### 🖼️ Capturas de Pantalla

![Dashboard](./screenshots/Home.png)

### Dashboard

![Dashboard](./screenshots/dashboard_1.png)
![Dashboard](./screenshots/dashboard_2.png)

### Autenticacion

![SignIn](./screenshots/SignIn.png)
![SignUp](./screenshots/SignUp.png)

### Servicios

![Services](./screenshots/services.png)
![Create](./screenshots/create_service.png)
![Delete](./screenshots/delete_service.png)
![Edit](./screenshots/edit_service.png)
![View](./screenshots/view_service_1.png)
![View](./screenshots/view_service_2.png)
![View](./screenshots/view_service_3.png)

---

## 📖 Tabla de Contenidos

- [Acerca de](#-acerca-de)
- [Para Quién](#-para-quién)
- [Características Principales](#-características-principales)
- [Arquitectura](#-arquitectura)
- [Stack Tecnológico](#-stack-tecnológico)
- [Inicio Rápido](#-inicio-rápido)
  - [Prerequisitos](#prerequisitos)
  - [Clonar los Repositorios](#clonar-los-repositorios)
  - [Configuración del Backend](#configuración-del-backend)
  - [Configuración del Frontend](#configuración-del-frontend)
  - [Configuración con Docker](#configuración-con-docker)
- [Uso](#-uso)
- [Variables de Entorno](#-variables-de-entorno)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Seguridad](#-seguridad)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Hoja de Ruta](#-hoja-de-ruta)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)

---

## 📌 Acerca de

**Panel de Monitoreo con IA** es una plataforma full-stack diseñada para rastrear, visualizar y alertar sobre la salud de servicios HTTP en tiempo real. Resuelve el problema crítico de **monitorear servicios desplegados** (APIs, web apps, microservicios) que necesitan disponibilidad 24/7.

El panel proporciona métricas de latencia, tasa de errores y disponibilidad en tiempo real vía Server-Sent Events (SSE), gráficos históricos con selección de rango de tiempo flexible, y autenticación multi-usuario — todo esencial para equipos que ejecutan infraestructura de producción a escala.

**Despliegue en vivo:**
- 🌐 [Frontend](https://frontend-monitoring-ai.vercel.app) — Vercel
- ⚙️ [Backend API](https://monitoring-api-txfu.onrender.com) — Render

---

## 👥 Para Quién

Equipos de backend, ingenieros DevOps, SREs, y estudiantes que necesitan monitoreo HTTP de servicios ligero en tiempo real.

---

## ✨ Características Principales

| Característica | Descripción |
|---|---|
| 📊 Dashboard en tiempo real | Métricas en vivo vía SSE: latencia, errores, uptime % |
| 🔐 Auth multi-usuario | Registro/login con JWT (cookies httpOnly) + Argon2 |
| 🛡️ Seguridad primero | Rate limiting (60 req/min/IP), protección SSRF, headers de seguridad, CSP |
| 📈 Gráficos históricos | Recharts: línea de latencia, barras de errores, área de disponibilidad |
| ⏱️ Selector de rango | Últimos 60 min / Últimas 24 horas / Últimos 7 días |
| 🔔 Detección de anomalías | Backend detecta picos de latencia, picos de errores, caídas |
| 📱 Totalmente responsive | Diseño mobile-first con sidebar hamburguesa |
| ⚡ Streaming SSE | Actualizaciones push en tiempo real sin polling |
| 🗃️ Paginación | Página de servicios: 3 elementos/página con paginación personalizada |
| 🧪 Suite de pruebas | 47+ pruebas unitarias con JUnit 5 + Mockito |

---

## 🏗️ Arquitectura

```
Next.js (Vercel) → Spring Boot (Render) → PostgreSQL (Supabase)
```

**Flujo:** Login → Cookie JWT → API REST → Scheduler sondea servicios cada 30s → Métricas almacenadas → SSE transmite al dashboard → Gráficos + detección de anomalías

---

## 🛠️ Stack Tecnológico

| Capa | Stack |
|---|---|
| Backend | Java 25, Spring Boot 4.1, JPA, PostgreSQL (Supabase), Spring Security, JJWT, Argon2 |
| Frontend | Next.js 16, React 19, TypeScript 5, Tailwind CSS 4, Recharts 3 |
| Pruebas | JUnit 5 + Mockito (47+ tests) |
| Despliegue | Render (backend) + Vercel (frontend) |

---

## 🚀 Inicio Rápido

**Prerequisitos:** Java 25+, Node.js 20+, PostgreSQL (o Supabase)

```bash
# Clonar repos
git clone https://github.com/Dage10/BackendMonitoringAi.git
git clone https://github.com/Dage10/FrontendMonitoringAi.git

# Backend
cd BackendMonitoringAi/monitoring-api/monitoring-api
./gradlew bootRun  # http://localhost:8080

# Frontend
cd FrontendAiMonitoring/ai-monitoring-dashboard
npm install && npm run dev  # http://localhost:3000
```

> Docker: `docker compose up --build` (solo backend)

---

## 💡 Uso

1. **Registra** una nueva cuenta en `/auth/register`
2. **Inicia sesión** con tus credenciales
3. **Agrega servicios** para monitorear (proporciona un nombre y URL de health check)
4. **Ve el dashboard** — los gráficos en tiempo real se actualizan vía SSE cada 30 segundos
5. **Cambia rangos de tiempo** — Últimos 60 min / Últimas 24 horas / Últimos 7 días
6. **Revisa anomalías** — el backend señala picos de latencia y aumentos en tasa de errores

---

## 🔧 Variables de Entorno

### Backend

| Variable | Descripción | Por defecto |
|---|---|--|
| `DB_URL` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:5432/monitoring` |
| `DB_USER` | Usuario de la base de datos | — |
| `DB_PASSWORD` | Contraseña de la base de datos | — |
| `JWT_SECRET` | Clave secreta para firmar JWT | — |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring activo | `local` |
| `CORS_ORIGINS` | Orígenes permitidos separados por coma | `http://localhost:3000` |

### Frontend

| Variable | Descripción | Por defecto |
|---|---|---|
| `NEXT_PUBLIC_API_URL` | URL base de la API (SSR/cliente) | `/api` |
| `API_PROXY_URL` | URL del backend para proxy API de Next.js | `http://localhost:8080` |

---

## 📡 Endpoints de la API

| Endpoint | Método | Descripción |
|---|---|---|
| `/api/auth/{register,login,me}` | POST/GET | Autenticación (cookies JWT) |
| `/api/services[/{id}]` | GET/POST/PUT/DELETE | CRUD de servicios |
| `/api/services/{id}/metrics` | GET | Métricas paginadas |
| `/api/metrics/stream` | GET | Stream SSE en tiempo real |
| `/api/metrics/anomalies` | GET | Anomalías detectadas |
| `/actuator/health` | GET | Health check |

---

## 🔒 Seguridad

Argon2 passwords · Cookies httpOnly JWT · Rate limiting (60/min/IP) · Protección SSRF · CSP + headers de seguridad · CORS restringido · Audit logging

---

## 📁 Estructura del Proyecto

```
Backend:  auth/ config/ metrics/ services/ users/
Frontend: app/(protected)/ components/ lib/
```

- **Backend**: API REST, auth JWT, streaming SSE, detección de anomalías, rate limiting
- **Frontend**: Dashboard con Recharts, CRUD de servicios, sidebar responsive

---

## 🗺️ Hoja de Ruta

- [ ] Alertas por email/webhook
- [ ] Intervalos de verificación personalizados
- [ ] Grupos de servicios y etiquetas
- [ ] Roles de usuario (admin/viewer)
- [ ] Tema oscuro/claro
- [ ] Exportar a CSV
- [ ] Integración Kubernetes/Docker

---

## 🤝 Contribuir

Fork → rama → commit con [Conventional Commits](https://www.conventionalcommits.org/) → PR. Asegúrate de que `./gradlew test` y `npm run lint` pasen.

---

## 📝 Licencia

Este proyecto está licenciado bajo la Licencia MIT — ver [LICENSE](../LICENSE) para detalles.

---

<div align="center">

**Hecho usando Spring Boot, Next.js y mejores prácticas modernas 2026**

</div>
