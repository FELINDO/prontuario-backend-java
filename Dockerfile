# --- Estágio 1: Compilação (Build) ---
# Usamos uma imagem oficial do Maven com Java 17 para compilar nosso projeto
FROM maven:3.8-openjdk-17 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo pom.xml primeiro para aproveitar o cache de dependências do Docker
COPY pom.xml .

# Baixa todas as dependências do projeto
RUN mvn dependency:go-offline

# Copia o resto do código-fonte do projeto
COPY src ./src

# Executa o comando para compilar e empacotar a aplicação em um .war
RUN mvn clean package


# --- Estágio 2: Execução (Run) ---
# Usamos uma imagem oficial do Tomcat, que é leve e pronta para produção
FROM tomcat:10.1-jdk17-temurin

# Remove o aplicativo de exemplo que vem com o Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o arquivo .war que foi gerado no estágio de compilação para a pasta webapps do Tomcat
# O nome do artefato é definido no pom.xml (<finalName>prontuario-backend</finalName>)
COPY --from=build /app/target/prontuario-backend.war /usr/local/tomcat/webapps/ROOT.war

# Expõe a porta 8080, que é a porta padrão do Tomcat
EXPOSE 8080

# Comando para iniciar o servidor Tomcat quando o container rodar
CMD ["catalina.sh", "run"]
