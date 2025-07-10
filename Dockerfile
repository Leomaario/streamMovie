# --- Estágio 1: Build com Maven ---
# Usamos uma imagem oficial que já vem com o Java 17 e o Maven.
# 'AS builder' dá um nome a este estágio para que possamos referenciá-lo depois.
FROM maven:3.9-eclipse-temurin-17 AS builder

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o pom.xml primeiro. Isso é um truque de otimização.
# Se as dependências no pom.xml não mudarem, o Docker reutiliza
# as camadas já baixadas, tornando o build muito mais rápido.
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora, copia o resto do código-fonte do seu projeto.
COPY src ./src

# Executa o build do Maven para compilar e empacotar a aplicação num .jar.
# O comando '-DskipTests' pula a execução dos testes, o que é comum
# e acelera o processo de deploy.
RUN mvn clean package -DskipTests

# --- Estágio 2: Imagem final para Execução ---
# Agora, usamos uma imagem muito mais leve, que contém apenas o necessário
# para RODAR a aplicação (Java Runtime Environment), não para construí-la.
FROM eclipse-temurin:17-jre-jammy

# Define o diretório de trabalho.
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio 'builder' para a imagem final.
# O *.jar é um coringa que funciona mesmo que o nome do seu jar mude.
COPY --from=builder /app/target/*.jar app.jar

# Informa ao Docker que a aplicação dentro deste contêiner vai usar a porta 8080.
EXPOSE 8080

# Este é o comando final que será executado quando o contêiner iniciar.
# Ele simplesmente diz: "Execute este arquivo .jar com o Java".
ENTRYPOINT ["java", "-jar", "app.jar"]