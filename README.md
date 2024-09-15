# Consulta - Demo Project para Spring Boot

Este projeto é um exemplo de aplicação Spring Boot que utiliza PostgreSQL como banco de dados, Maven para gerenciamento de dependências, e Docker/Docker Compose para o ambiente de contêinerização. A aplicação também inclui uma interface front-end desenvolvida com Vue.js.

## Tecnologias Utilizadas

- **Java 17** - Linguagem principal da aplicação
- **Spring Boot** - Framework Java para desenvolvimento da aplicação
    - **Spring Boot Data JPA** - Integração com o banco de dados
    - **Spring Boot Validation** - Validação de dados
    - **Spring Boot Docker Compose** - Suporte ao Docker Compose
    - **Spring Boot DevTools** - Ferramentas de desenvolvimento
- **PostgreSQL** - Banco de dados relacional
- **JUnit 5** - Testes unitários
- **Mockito** - Biblioteca para criação de mocks em testes
- **Lombok** - Reduz a verbosidade no código Java
- **Docker e Docker Compose** - Para containerização e orquestração dos serviços
- **Vue.js** - Framework JavaScript para o front-end

## Estrutura do Projeto

O projeto está dividido em três serviços principais, definidos no arquivo `docker-compose.yml`:

1. **db** - Serviço de banco de dados PostgreSQL.
2. **app** - Serviço da aplicação Java Spring Boot.
3. **frontend** - Serviço da interface front-end desenvolvida com Vue.js.

## Requisitos

- **Docker** versão 20.10 ou superior
- **Docker Compose** versão 1.29 ou superior
- **Maven** versão 3.8.5 ou superior
- **Java 17**

## Configuração e Execução

### Clonando o Repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

## Executando no Docker
- **Construir a imagem Docker e executar container:**
```bash
docker-compose up -d --build
```