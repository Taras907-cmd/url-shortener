# URL Shortener

REST API для скорочення посилань з реєстрацією/автентифікацією користувачів (JWT),
відстеженням статистики переходів та терміном дії посилань.

## Технологічний стек

- Java 17
- Spring Boot 3.3.3 (Web, Data JPA, Security, Validation)
- PostgreSQL
- Flyway (міграції БД)
- JWT (io.jsonwebtoken)
- OpenAPI 3.0 / Swagger UI (springdoc)
- JUnit 5, Mockito, Testcontainers
- Docker, Docker Compose
- GitHub Actions (CI)

## Функціонал

- Реєстрація та автентифікація користувачів (JWT)
- Створення, перегляд та видалення коротких посилань (лише власником)
- Перехід за коротким посиланням з підрахунком статистики (доступно без автентифікації)
- Термін дії посилань (30 днів від створення)
- Валідація вхідних даних на всіх ендпоінтах

## API-версіонування

Усі ендпоінти доступні під префіксом `/api/v1/...`.

## Документація API

Після запуску застосунку Swagger UI доступний за адресою:
http://localhost:8080/swagger-ui/index.html


## Змінні оточення

| Змінна | Опис | Значення за замовчуванням |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL підключення до PostgreSQL | `jdbc:postgresql://postgres:5432/url_shortener_db` |
| `SPRING_DATASOURCE_USERNAME` | Користувач БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `postgrespassword` |
| `JWT_SECRET` | Секретний ключ для підпису JWT-токенів (мінімум 32 байти) | вбудоване dev-значення, **обов'язково змінити на проді** |

При локальному запуску через Docker Compose ці значення вже прописані в `docker-compose.yml`.
Для production-розгортання рекомендується передавати їх ззовні (наприклад, через `.env`-файл або секрети CI/CD), а не зберігати в репозиторії.

## Запуск через Docker Compose

```bash
docker-compose up --build
```

Застосунок буде доступний на `http://localhost:8080`, PostgreSQL — на `localhost:5432`.

## Запуск локально (без Docker)

1. Підніми PostgreSQL окремо (наприклад, через Docker: `docker run -e POSTGRES_PASSWORD=postgrespassword -e POSTGRES_DB=url_shortener_db -p 5432:5432 postgres:16-alpine`).
2. Онови `src/main/resources/application.properties` під свої параметри підключення.
3. Запусти:
```bash
./gradlew bootRun
```

## Тести

```bash
./gradlew test
```


Інтеграційні тести використовують [Testcontainers](https://java.testcontainers.org/) —
для їх запуску потрібен встановлений і запущений Docker.

## Приклад використання API

**Реєстрація:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "taras", "password": "Password123"}'
```

**Логін:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "taras", "password": "Password123"}'
```

**Створення короткого посилання** (потрібен токен з логіну):
```bash
curl -X POST http://localhost:8080/api/v1/links \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"originalUrl": "https://www.google.com"}'
```
