# ==========================================
# Stage 1: Build & Package (JDK)
# ==========================================
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# Step A: Copy build wrapper and manifest first for layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer is cached unless pom.xml changes)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Step B: Copy source code and compile
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Runtime Image (Lightweight JRE)
# ==========================================
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Security best practice: Never run containers as root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy ONLY the compiled .jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]