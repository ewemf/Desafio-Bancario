# Desafio Técnico — Transferência Bancária Simplificada

## Objetivo

Criar uma **API RESTful em Java com Spring Boot** que simule uma **plataforma de transferências bancárias simplificada**, onde usuários podem realizar e receber transferências de valores entre si, respeitando regras de negócio e validações.

---

## Contexto do Sistema

O sistema deve permitir **cadastro de usuários e lojistas**, ambos possuindo uma **carteira com saldo**.  
As transferências são realizadas **entre usuários** ou **de usuários para lojistas**.

Existem **duas categorias** de usuários:
- **Comum** → pode **enviar e receber** transferências.  
- **Lojista** → pode **apenas receber** transferências.

---

## Regras de Negócio

1. **Cadastro de Usuários**
   - Cada usuário deve possuir:
     - `nomeCompleto`
     - `cpfOuCnpj`
     - `email`
     - `senha`
     - `tipoUsuario` → `COMUM` ou `LOJISTA`
   - O `cpfOuCnpj` e o `email` devem ser **únicos**.
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
   - Deve-se validar se o **pagador possui saldo suficiente**.
   - Antes de concluir a transferência, consultar um **serviço externo autorizador**:  
     ```
     GET https://util.devi.tools/api/v2/authorize
     ```
     - A transferência **só é realizada se o serviço retornar autorização**.
   - Após a transferência, enviar **notificação de recebimento** usando:
     ```
     POST https://util.devi.tools/api/v1/notify
     ```
   - Caso alguma etapa falhe (erro de autorização, notificação ou saldo insuficiente), a transação deve ser **revertida (rollback)**.

---

## Tecnologias Sugeridas

- **Java 17+**
- **Spring Boot 3+**
- **Spring Data JPA / Hibernate**
- **H2 Database (em memória)** ou **PostgreSQL**
- **Spring Web**
- **Spring Validation**
- **RestTemplate** ou **WebClient** para chamadas externas

---

## Passos Recomendados para Implementação

1. **Crie o projeto**
   - Gere um novo projeto com o [Spring Initializr](https://start.spring.io/)
   - Dependências sugeridas:
     - Spring Web  
     - Spring Data JPA  
     - Validation  
     - H2 Database (ou PostgreSQL)

2. **Modele as entidades principais**
   - `Usuario` (atributos como nome, cpfOuCnpj, email, senha, tipoUsuario, saldo)
   - `Transferencia` (valor, pagador, recebedor, data, status)

3. **Implemente os repositórios (repositories)**  
   - `UsuarioRepository`
   - `TransferenciaRepository`

4. **Crie os serviços (services)**  
   - `UsuarioService`
   - `TransferenciaService`
     - Valide saldo e tipo de usuário  
     - Consulte o serviço autorizador  
     - Execute a transferência dentro de uma transação (`@Transactional`)  
     - Chame o mock de notificação  

5. **Crie os controladores (controllers)**  
   - `UsuarioController` → CRUD básico  
   - `TransferenciaController` → endpoint `/transferencias`  

6. **Teste o fluxo principal**
   - Crie dois usuários (um comum e um lojista)
   - Realize uma transferência válida
   - Teste os casos de falha (saldo insuficiente, lojista como pagador, serviço negando autorização)

---

## Dica

Não se preocupe em fazer tudo perfeito — o foco é ver como você **estrutura o raciocínio**, **organiza o projeto** e **implementa as regras de negócio**.  
Durante a avaliação, será discutido o que você fez, o que deixou de fazer e o que poderia melhorar.

---

## Extras (opcional)

Se quiser ir além:
- Adicione **testes unitários** com JUnit e Mockito  
- Implemente **tratamento global de exceções** com `@ControllerAdvice`  
- Documente a API com **Swagger / OpenAPI**  (Seria melhor para testes e documentação)

---

# Mock APIs

Autorizador externo:
GET https://util.devi.tools/api/v2/authorize


Notificação:
POST https://util.devi.tools/api/v1/notify
