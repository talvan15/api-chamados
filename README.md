# Sistema de Chamados IFRN

API REST para registrar e acompanhar solicitações de suporte no IFRN. O sistema organiza chamados por categoria e setor responsável, permite que técnicos acompanhem o atendimento e mantém as interações registradas no histórico.

## Visão geral

O projeto contempla cinco recursos principais:

- **Setores** e **categorias** para organizar as áreas responsáveis pelos atendimentos.
- **Usuários**, classificados como `SOLICITANTE` ou `TECNICO`.
- **Chamados**, com prioridade, status, local, datas e solução.
- **Interações**, que registram as mensagens do solicitante e do técnico atribuído.

Um chamado começa em `ABERTO`. Um técnico do setor responsável pode assumi-lo, alterando o status para `EM_ATENDIMENTO`, e depois resolvê-lo com uma solução registrada. O solicitante pode cancelar enquanto o chamado estiver aberto. O histórico acompanha o atendimento.

## Tecnologias

- Java 21 e Spring Boot 4
- Spring Web MVC, Spring Data JPA e Bean Validation
- PostgreSQL 17
- Flyway para versionamento do esquema do banco
- Docker e Docker Compose

## Como executar

Requisitos: Docker Engine ou Docker Desktop com Docker Compose v2.

Na raiz do projeto, execute:

```bash
docker compose up --build
```

Quando os serviços iniciarem, a API estará disponível em <http://localhost:8080>. O PostgreSQL fica acessível na porta `5433` do computador e os dados são mantidos no volume Docker `postgres_data`.

O Compose já fornece configurações locais padrão. Se desejar personalizá-las, defina `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` no ambiente ou em um arquivo `.env` na raiz.

Para parar os serviços, use `Ctrl+C` no terminal. Para iniciar em segundo plano:

```bash
docker compose up --build -d
```

E para pará-los:

```bash
docker compose down
```

Na primeira inicialização, o Flyway executa as migrations e cria as tabelas e os dados de demonstração. Os registros de inicialização podem ser acompanhados com:

```bash
docker compose logs -f api
```

## Começar a usar

Confira a API e os dados iniciais:

```bash
curl "http://localhost:8080/setores?page=0&size=10"
curl "http://localhost:8080/categorias?page=0&size=10"
curl "http://localhost:8080/usuarios?page=0&size=10"
```

Para abrir um chamado, envie um `POST` para `/chamados`, informando o solicitante e a categoria:

```bash
curl -X POST http://localhost:8080/chamados \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Projetor sem imagem",
    "descricao": "O projetor liga, mas não exibe imagem.",
    "local": "Bloco A, sala 4",
    "categoriaId": 3,
    "solicitanteId": 1,
    "prioridade": "MEDIA"
  }'
```

O solicitante pode acompanhar seus chamados em `/usuarios/{id}/chamados`. Técnicos podem assumir e resolver chamados pelas rotas `/chamados/{id}/assumir` e `/chamados/{id}/resolver`. As mensagens são incluídas em `/chamados/{id}/interacoes`.

### Usuários de demonstração

| ID | Perfil | E-mail | Setor |
|---:|---|---|---|
| 1 | SOLICITANTE | `solicitante@ifrn.edu.br` | — |
| 2 | SOLICITANTE | `aluno@ifrn.edu.br` | — |
| 3 | TECNICO | `tecnico.cotic@ifrn.edu.br` | COTIC |
| 4 | TECNICO | `tecnico.manut@ifrn.edu.br` | MANUT |

Esta versão não tem autenticação; as ações recebem o ID do usuário no corpo da requisição. Os dados de demonstração são criados pelas migrations iniciais.

## Recursos da API

| Recurso | Rotas principais |
|---|---|
| Setores | `/setores`, `/setores/{id}`, `/setores/{id}/categorias` |
| Categorias | `/categorias`, `/categorias/{id}` |
| Usuários | `/usuarios`, `/usuarios/{id}`, `/usuarios/{id}/chamados` |
| Chamados | `/chamados`, `/chamados/{id}` |
| Atendimento | `/chamados/{id}/assumir`, `/chamados/{id}/resolver`, `/chamados/{id}/cancelar`, `/chamados/{id}/interacoes` |

As coleções aceitam paginação com `page`, `size` e `sort`; o padrão é 10 itens por página e o máximo é 50. As listagens de setores, categorias, usuários e chamados também aceitam filtros por query string.

As respostas de erro incluem data e hora, código HTTP, mensagem e caminho da requisição. Os principais códigos são 400 para dados inválidos, 404 para recursos inexistentes e 409 para conflitos com as regras do sistema.

## Organização do código

- `model`: entidades e enumerações do domínio.
- `repository`: acesso aos dados com Spring Data JPA.
- `service`: regras de negócio e operações da aplicação.
- `controller`: endpoints HTTP.
- `api/dto`: formatos de entrada e saída e tratamento de erros.
- `src/main/resources/db/migration`: migrations do Flyway e dados iniciais.
- `requests/chamados.http`: exemplos de requisições para IDEs compatíveis com arquivos HTTP.

O esquema é criado pelas migrations do Flyway e validado pelo Hibernate na inicialização.
