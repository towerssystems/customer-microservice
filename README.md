# Microserviço de Clientes

Este projeto implementa um microserviço de gerenciamento de clientes utilizando Spring Boot, JDK 11 e Maven. Ele oferece funcionalidades CRUD (Criar, Ler, Atualizar, Excluir) para clientes, integração com banco de dados H2 em memória, segurança com OAuth2 (Password Flow) e comunicação assíncrona via RabbitMQ.

## Requisitos

*   JDK 11
*   Maven
*   Docker (opcional, para execução em container)
*   RabbitMQ (local ou em container)
*   Postman (ou ferramenta similar para testar a API)

## Configuração do Projeto

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/towerssystems/customer-microservice.git
    cd customer-microservice
    ```

2.  **Configuração do RabbitMQ:**

    Certifique-se de que o RabbitMQ esteja em execução. Você pode iniciá-lo via Docker:

    ```bash
    docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
    ```

    As configurações do RabbitMQ estão em `src/main/resources/application.properties`.

## Construção e Execução

### Com Maven

1.  **Compile o projeto:**

    ```bash
    mvn clean install
    ```

2.  **Execute a aplicação:**

    ```bash
    mvn spring-boot:run
    ```

    A aplicação estará disponível em `http://localhost:8080`.

### Com Docker

1.  **Construa a imagem Docker:**

    ```bash
    docker build -t customer-microservice .
    ```

2.  **Execute o container Docker:**

    ```bash
    docker run -p 8080:8080 customer-microservice
    ```

    A aplicação estará disponível em `http://localhost:8080`.

## Endpoints da API

Todos os endpoints requerem autenticação OAuth2.

### Autenticação (OAuth2 Password Flow)

Para obter um `access_token`, faça uma requisição POST para:

`POST /oauth/token`

**Headers:**

*   `Content-Type: application/x-www-form-urlencoded`
*   `Authorization: Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=` (Base64 encoded de `client-id:client-secret`)

**Body (x-www-form-urlencoded):**

*   `grant_type: password`
*   `username: user`
*   `password: password`

Exemplo de resposta:

```json
{
    "access_token": "<YOUR_ACCESS_TOKEN>",
    "token_type": "bearer",
    "expires_in": 3599,
    "scope": "read write",
    "jti": "<JTI_VALUE>"
}
```

Curl para obter o token:

```
curl -X POST \
http://localhost:8080/oauth/token \
-H 'Authorization: Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'grant_type=password&username=user&password=password'
```

Retorno: 
`{"access_token":"Fzj-t1xW45q5lqaYNO8Q198aZaU","token_type":"bearer","expires_in":3532,"scope":"read write"}`
`{"access_token":"lX2nDTCXZv9F99HQxIgt3fgiJu4","token_type":"bearer","expires_in":3599,"scope":"read write"}`

### Operações CRUD de Clientes

Utilize o `access_token` obtido no header `Authorization: Bearer <YOUR_ACCESS_TOKEN>` para todas as requisições abaixo.

*   **Listar todos os clientes:**

    `GET /api/customers`

Curl:

`curl -X GET \
http://localhost:8080/api/customers \
-H 'Authorization: Bearer <SEU_ACCESS_TOKEN>'
`

Com token de obtido para teste:

`curl -X GET \
http://localhost:8080/api/customers \
-H 'Authorization: Bearer Fzj-t1xW45q5lqaYNO8Q198aZaU'
`
`
*   **Obter cliente por ID:**

    `GET /api/customers/{id}`

Curl:

`curl -X GET \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer <SEU_ACCESS_TOKEN>'
`

Com token de obtido para teste:

`curl -X GET \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer Fzj-t1xW45q5lqaYNO8Q198aZaU'
`

*   **Criar novo cliente:**

    `POST /api/customers`

    **Body (JSON):**

    ```json
    {
        "name": "Nome do Cliente",
        "email": "email@example.com",
        "document": "123.456.789-00"
    }
    ```

Curl:

`curl -X POST \
http://localhost:8080/api/customers \
-H 'Authorization: Bearer <SEU_ACCESS_TOKEN>' \
-H 'Content-Type: application/json' \
-d '{
"name": "João da Silva",
"email": "joao.silva@exemplo.com",
"document": "123.456.789-00"
}'
`

Com token de obtido para teste:


`curl -X POST \
http://localhost:8080/api/customers \
-H 'Authorization: Bearer lX2nDTCXZv9F99HQxIgt3fgiJu4' \
-H 'Content-Type: application/json' \
-d '{
"name": "João da Silva",
"email": "joao.silva@exemplo.com",
"document": "123.456.789-00"
}'
`


*   **Atualizar cliente existente:**

    `PUT /api/customers/{id}`

    **Body (JSON):**

    ```json
    {
        "name": "Nome Atualizado",
        "email": "novoemail@example.com",
        "document": "000.987.654-32"
    }
    ```

Curl:

`curl -X PUT \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer Fzj-t1xW45q5lqaYNO8Q198aZaU' \
-H 'Content-Type: application/json' \
-d '{
"name": "João da Silva Atualizado",
"email": "joao.novo@exemplo.com",
"document": "123.456.789-00"
}'
`

Com token de obtido para teste:

`curl -X PUT \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer <SEU_ACCESS_TOKEN>' \
-H 'Content-Type: application/json' \
-d '{
"name": "João da Silva Atualizado",
"email": "joao.novo@exemplo.com",
"document": "123.456.789-00"
}'
`

*   **Excluir cliente:**

    `DELETE /api/customers/{id}`

Curl:

`curl -X DELETE \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer <SEU_ACCESS_TOKEN>'
`

Com token de obtido para teste:

`
curl -X DELETE \
http://localhost:8080/api/customers/1 \
-H 'Authorization: Bearer Fzj-t1xW45q5lqaYNO8Q198aZaU'
`

## Testes Unitários

Para executar os testes unitários, utilize o Maven:

```bash
mvn test
```

## H2 Console

O console do H2 está disponível em `http://localhost:8080/h2-console`. Utilize as seguintes credenciais:

*   **JDBC URL:** `jdbc:h2:mem:customerdb`
*   **User Name:** `sa`
*   **Password:** `password`

## Observações

*   A segurança OAuth2 está configurada com um usuário em memória (`user`/`password`) e um cliente (`client-id`/`client-secret`) para fins de teste.
*   As mensagens do RabbitMQ são logadas no console da aplicação.
*   Este projeto não inclui uma interface web, sendo projetado para ser consumido via API (ex: Postman, Curl).


Exemplo de teste na fila do rabbit:

2026-02-24 00:25:27.564  INFO 33735 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2026-02-24 00:25:27.566  INFO 33735 --- [           main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2026-02-24 00:25:27.607  INFO 33735 --- [           main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#2ff8d39b:0/SimpleConnection@1b901f7b [delegate=amqp://guest@127.0.0.1:5672/, localPort= 51215]
2026-02-24 00:25:27.657  INFO 33735 --- [           main] c.d.claro.customer.CustomerApplication   : Started CustomerApplication in 4.236 seconds (JVM running for 4.632)
2026-02-24 00:25:57.526  INFO 33735 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-02-24 00:25:57.526  INFO 33735 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-02-24 00:25:57.527  INFO 33735 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2026-02-24 00:27:07.365  INFO 33735 --- [nio-8080-exec-2] c.d.c.customer.service.CustomerService   : Sending message to RabbitMQ: Customer created/updated: João da Silva
2026-02-24 00:27:07.376  INFO 33735 --- [ntContainer#0-1] c.d.c.customer.service.CustomerService   : Message received from RabbitMQ: Customer created/updated: João da Silva

