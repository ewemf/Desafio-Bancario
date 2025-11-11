# Desafio Técnico — Transferência Bancária Simplificada

API RESTful em Java 17 + Spring Boot 3 que simula uma plataforma de transferências entre usuários, respeitando regras de negócio, validações, integração com autorizador externo e notificação. Toda a transferência roda dentro de transação — qualquer falha gera rollback.
---

## Arquitetura & Tecnologias

- Spring Boot 3, Spring Web, Spring Data JPA (Hibernate), Bean Validation
- H2 (memória por padrão)
- DTOs para requests/responses
- @Transactional na transferência + ControllerAdvice para mapear erros (400/422/502/500)

---

## Regras de Negócio

1. **Cadastro de Usuários**
   - Cada usuário possui:
     - `nomeCompleto`
     - `cpfOuCnpj`
     - `email`
     - `senha`
     - `tipoUsuario` → `COMUM` ou `LOJISTA`
   - O `cpfOuCnpj` e o `email` **únicos**.
   - Todos os campos são **obrigatórios**.

2. **Transferências**
   - Endpoint:  
     ```
     POST /transferencias
     Content-Type: application/json

     {
       "valor": 100.0,
       "pagadorId": 4,
       "recebedorId": 15
     }
     ```
   - O **usuário comum** pode enviar transferências.  
   - O **lojista não pode enviar** transferências.  
   - Saldo suficiente **obrigatório**.
   - Antes de concluir a transferência, consultar um **serviço externo autorizador**:  
     ```
     GET https://util.devi.tools/api/v2/authorize
     ```
     - A transferência **só é realizada se o serviço retornar autorização**.
   - Após a transferência, enviar **notificação de recebimento** usando:
     ```
     POST https://util.devi.tools/api/v1/notify
     ```
   - Falhou qualquer etapa → rollback (nenhuma linha é persistida).

---

## Como rodar

- **Java 17+**
- **Spring Boot 3+**
- **Spring Data JPA / Hibernate**
- **H2 Database (em memória)** ou **PostgreSQL**
- **Spring Web**
- **Spring Validation**
- **RestTemplate** ou **WebClient** para chamadas externas

---

## Passos Recomendados para Implementação

Pré-requisitos: JDK 17, Maven (ou wrapper), porta 8080 livre.

```
mvn clean package -DskipTests

mvn spring-boot:run
```

---

## Endpoints

### Usuários

- POST /usuarios — cria usuário

- GET /usuarios — lista

- GET /usuarios/{id} — consulta por id

### Request (POST /usuarios) - Se tipo for *COMUM*
```
{
  "nomeCompleto": "Pagador",
  "cpfOuCnpj": "111123",
  "email": "pagador@x.com",
  "senha": "123",
  "tipoUsuario": "COMUM",
  "saldoInicial": 200.00
}
```

### Request (POST /usuarios) - Se tipo for *LOJISTA*
```
{
  "nomeCompleto": "Pagador",
  "cpfOuCnpj": "122223",
  "email": "pagador@x.com",
  "senha": "123",
  "tipoUsuario": "LOJISTA",
  "saldoInicial": 0
}
```

### Transferências

- POST /transferencias — executa transferência

- GET /transferencias — lista transferências (DTO, sem N+1)

### Request (POST /transferencias)

```
{
  "valor": 100.00,
  "pagadorId": 1,
  "recebedorId": 2
}
```

---

## Cenários de teste (inclui rollback)

- Crie dois usuários (um comum e um lojista)

- Realize uma transferência válida
  
- Teste os casos de falha (saldo insuficiente, lojista como pagador, serviço negando autorização)

---

## Tratamento de erros

- 400 — validação do corpo (campos obrigatórios/formatos)

- 404 — recurso não encontrado (quando aplicável)

- 409 — violação de unicidade (cpfOuCnpj/email)

- 422 — regras de negócio (ex.: lojista como pagador, saldo insuficiente)

- 502 — serviços externos (autorizador/notify) falharam/negados

- 500 — erro interno não mapeado

---

# Mock APIs

Autorizador externo:
GET https://util.devi.tools/api/v2/authorize


Notificação:
POST https://util.devi.tools/api/v1/notify
