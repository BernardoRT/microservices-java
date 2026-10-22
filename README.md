# E-Commerce Microservices Architecture 🚀

Este projeto é uma implementação de um backend de e-commerce baseado em uma **Arquitetura de Microsserviços**, desenvolvido com Java e Spring Boot. O foco principal do sistema é aplicar padrões avançados de resiliência, segurança (Autenticação Stateless e RBAC) e comunicação entre serviços (Service Discovery e API Gateway).

## 🏗️ Arquitetura do Sistema

O ecossistema é composto por 4 microsserviços principais e uma ferramenta de Service Discovery/Configuration:

1. **`gateway-api` (Porta 8080):** 
   - Atua como a "porta da frente" do sistema (Spring Cloud Gateway).
   - Resolve o roteamento de chamadas (`lb://`).
   - Responsável pela validação global de tokens JWT nas rotas protegidas (Filtros Globais).
2. **`auth-api` (Porta 8200):**
   - Responsável pelo Cadastro (Signup) e Login (Signin) de utilizadores.
   - Gera e assina o token JWT para autenticação Stateless.
3. **`product-api` (Porta 8000):**
   - Gere o catálogo de produtos.
   - Aplica regras de autorização baseadas em papéis (RBAC - Role-Based Access Control), permitindo apenas a Administradores o cadastro de novos produtos.
   - Comunica-se de forma síncrona com a `currency-api` para conversão de valores.
4. **`currency-api` (Porta 8100):**
   - Fornece as taxas de conversão de moedas.
   - Integra-se com a **API externa do Banco Central do Brasil (BCB)** via `HttpExchange` e `RestClient`.
   - Utiliza base de dados local apenas como *Fallback* em caso de indisponibilidade da API externa.
5. **HashiCorp Consul (Porta 8500):**
   - Gere a descoberta de serviços (Service Discovery).
   - Armazena de forma externalizada todas as configurações e regras de resiliência de cada microsserviço (Key/Value).

## 🛠️ Tecnologias Utilizadas

* **Java 17**
* **Spring Boot** (Data JPA, Web, Security, Actuator)
* **Spring Cloud** (Gateway, Consul Config, Consul Discovery)
* **PostgreSQL** (Bases de dados relacionais separadas por serviço)
* **Flyway** (Migrações e versionamento de base de dados)
* **Resilience4j** (Padrões de Tolerância a Falhas)
* **Caffeine** (Cache em memória)
* **JJWT** (Autenticação JSON Web Token)

## 🛡️ Padrões de Resiliência Implementados

A comunicação entre a `product-api` -> `currency-api` -> `API do BCB` é protegida pelos seguintes padrões:

* **Fallback Pattern:** Retorno de um valor padrão ou consulta à base de dados local caso a API externa/microsserviço falhe.
* **Timeout Pattern:** Limite de tempo de conexão e leitura (Connect/Read Timeout) para evitar o bloqueio eterno de *threads*.
* **Retry Pattern:** Realiza até 3 tentativas automáticas com 1 segundo de intervalo para superar falhas de rede transitórias.
* **Circuit Breaker Pattern:** Abre o circuito e interrompe chamadas se a taxa de falha atingir 50% nas últimas 5 chamadas, evitando sobrecarga em serviços em colapso.
* **Caching Pattern (Caffeine):** Armazena em memória as taxas de câmbio já consultadas, reduzindo latência e dependência da rede (limite de 1000 entradas com expiração de 10 minutos).

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos
* Java 17 instalado.
* PostgreSQL rodando localmente (porta 5432).
* Ficheiro executável do HashiCorp Consul na raiz do projeto.

### Passos para inicialização

1. **Bases de Dados:** No seu PostgreSQL, crie 3 bancos de dados vazios: `db_user`, `db_currency` e `db_product`. O Flyway encarrega-se de criar as tabelas.
2. **Consul:** Execute o ficheiro `start_consul.bat`. Ele iniciará o servidor do Consul na porta 8500 e carregará todas as chaves de configuração e regras do Resilience4j para a RAM.
3. **Microsserviços:** Execute as aplicações Java na seguinte ordem:
   - `auth-api`
   - `currency-api`
   - `product-api`
   - `gateway-api`

## 🧪 Como Testar a API

Todas as requisições agora devem ser feitas através do **Gateway (`http://localhost:8080`)**.

**1. Login de Administrador (Geração do Token):**
```http
POST /auth/signin
{
  "email": "admin@admin.dev",
  "password": "teste"
}
```
*(Copie o token gerado na resposta).*

**2. Listar Produtos (Rota Pública com Conversão e Resiliência ativas):**
```http
GET /products?targetCurrency=BRL
```
*(Se desligar a `currency-api`, verá o preço a `-1` e o erro de Fallback a aparecer no JSON).*

**3. Criar Produto (Rota Protegida - Requer Token no Header):**
```http
POST /ws/products
Authorization: Bearer <SEU_TOKEN_AQUI>
{
  "description": "POCO X8 Pro Global",
  "brand": "POCO",
  "model": "POCO X8 Pro",
  "currency": "USD",
  "price": 395.5
}
```
*(Se tentar com um utilizador que não seja administrador, o sistema retornará `403 Forbidden`).*

---
*Projeto desenvolvido para a disciplina de Paradigmas e Linguagens de Programação / Projeto Integrador.*