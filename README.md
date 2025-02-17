### Запуск проекта

1. **Запустить PostgreSQL в Docker**
   ```sh
   docker compose -f dev-docker-compose.yaml up -d
   ```

3. **Запустить приложение**
   ```sh
   ./gradlew bootRun
   ```
   Это запустит Spring Boot приложение.

4. **Проверить работу**
    - API доступно по адресу `http://localhost:8080`

5. **Остановка контейнера**
   ```sh
   docker compose -f dev-docker-compose.yaml down
   ```

