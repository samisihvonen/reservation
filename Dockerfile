# ============================================
# Root Dockerfile - Multi-service build
# Place this in the root of your repository
# ============================================

# Build backend
FROM maven:3.9.12-eclipse-temurin-17-alpine AS backend-builder
WORKDIR /build
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Build frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/src ./src
COPY frontend/index.html .
COPY frontend/tsconfig*.json .
COPY frontend/vite.config.ts .
COPY frontend/eslint.config.js .
RUN npm run build

# Runtime - Backend
FROM eclipse-temurin:17-jre-alpine AS backend-runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=backend-builder /build/target/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 CMD java -jar /app/app.jar || exit 1
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime - Frontend
FROM nginx:alpine AS frontend-runtime
RUN rm /etc/nginx/conf.d/default.conf
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-builder /app/dist /usr/share/nginx/html
RUN addgroup -S nginx && adduser -S nginx -G nginx
USER nginx
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 CMD wget --quiet --tries=1 --spider http://localhost:80 || exit 1
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]