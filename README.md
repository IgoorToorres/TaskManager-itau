# Task Manager API

API REST simples para gerenciamento de tarefas, desenvolvida com Java e Spring Boot. O projeto permite criar, listar, consultar, atualizar e excluir tarefas.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Spring Web
- Jakarta Bean Validation
- Gradle 9.8.1 (Gradle Wrapper)
- Lombok
- JUnit 5 e MockMvc
- Spring REST Docs e Asciidoctor

## Funcionalidades

- Criar uma tarefa com título e descrição opcional
- Listar todas as tarefas
- Buscar uma tarefa pelo ID
- Atualizar parcialmente uma tarefa
- Excluir uma tarefa
- Validar os dados recebidos pela API
- Retornar `404 Not Found` quando uma tarefa não existe

Os status disponíveis são:

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`

> As tarefas são armazenadas em memória. Por isso, os dados são perdidos quando a aplicação é reiniciada.

## Estrutura do projeto

O código está separado em três partes principais:

- `domain`: entidades, regras de domínio e contrato do repositório
- `application`: casos de uso da aplicação
- `infrastructure`: controller HTTP, tratamento de erros e repositório em memória

## Como executar

### Pré-requisitos

- JDK 25 instalado

Não é necessário instalar o Gradle, pois o projeto inclui o Gradle Wrapper.

### Iniciar a aplicação

No Linux ou macOS:

```bash
./gradlew bootRun
```

No Windows:

```bat
gradlew.bat bootRun
```

A API ficará disponível em `http://localhost:8080`.

## Endpoints

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/tasks` | Cria uma tarefa |
| `GET` | `/tasks` | Lista as tarefas |
| `GET` | `/tasks/{id}` | Busca uma tarefa pelo ID |
| `PATCH` | `/tasks/{id}` | Atualiza parcialmente uma tarefa |
| `DELETE` | `/tasks/{id}` | Exclui uma tarefa |

### Criar uma tarefa

```bash
curl -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Estudar Spring Boot",
    "description": "Praticar a criação de APIs REST"
  }'
```

Exemplo de resposta:

```json
{
  "id": "11fcf30a-8ff9-4d02-bbc1-a5e72193050a",
  "title": "Estudar Spring Boot",
  "description": "Praticar a criação de APIs REST",
  "status": "PENDING"
}
```

### Atualizar uma tarefa

Todos os campos são opcionais na atualização:

```bash
curl -X PATCH http://localhost:8080/tasks/{id} \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Estudar testes com Spring",
    "status": "IN_PROGRESS"
  }'
```

## Testes

Para executar os testes automatizados:

```bash
./gradlew test
```

## Documentação da API

A documentação detalhada dos endpoints é gerada a partir dos testes com Spring REST Docs:

```bash
./gradlew asciidoctor
```

Depois da geração, abra o arquivo:

```text
build/docs/asciidoc/index.html
```

## Postman

O projeto também possui uma collection pronta para importação no Postman:

```text
postman/taskmanager-itau.postman_collection.json
```

Com a aplicação em execução, rode as requisições da collection na ordem apresentada. O ID da tarefa criada será reutilizado automaticamente nas demais chamadas.
