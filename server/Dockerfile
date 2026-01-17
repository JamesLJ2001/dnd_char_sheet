# 第一阶段：编译 (Builder)
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 跳过测试打包，加快速度
RUN mvn clean package -DskipTests

# 第二阶段：运行 (Runner)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 从第一阶段把 jar 包拷过来
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]