# Sistema de Chamados IFRN

## Executar com Docker

Requisito: Docker Engine/Desktop com Docker Compose v2.

```bash
docker compose up --build
```

A API inicia em <http://localhost:8080>. O PostgreSQL fica disponível na porta `5433` do host e os dados persistem no volume `postgres_data`. O Compose tem credenciais locais padrão para facilitar a execução inicial. Para personalizá-las, defina `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` no ambiente ou em um `.env` local. Não use as credenciais padrão em uma implantação exposta.

As migrations Flyway criam as tabelas e dados de demonstração na primeira inicialização. Para consultar os registros de log:

```bash
docker compose logs -f api
```

O esquema novo está em `V3__criar_esquema_chamados.sql` e os registros iniciais em `V4__inserir_dados_iniciais.sql`.

## Dados de demonstração

| ID | Tipo | E-mail | Matrícula | Setor |
|---:|---|---|---|---|
| 1 | SOLICITANTE | solicitante@ifrn.edu.br | DEMO-SOL-001 | — |
| 2 | SOLICITANTE | aluno@ifrn.edu.br | DEMO-SOL-002 | — |
| 3 | TECNICO | tecnico.cotic@ifrn.edu.br | DEMO-TEC-001 | COTIC |
| 4 | TECNICO | tecnico.manut@ifrn.edu.br | DEMO-TEC-002 | MANUT |

Os IDs são informados no corpo das ações porque o projeto não implementa autenticação, conforme o requisito.

## Rotas

Cadastros:

| Método | Rota | Uso |
|---|---|---|
| POST/GET | `/setores` | Criar e listar setores; aceita filtro `nome` |
| GET/PUT/DELETE | `/setores/{id}` | Consultar, atualizar e excluir setor |
| POST/GET | `/categorias` | Criar e listar categorias; filtros `nome`, `setorId` |
| GET/PUT/DELETE | `/categorias/{id}` | Consultar, atualizar e excluir categoria |
| GET | `/setores/{id}/categorias` | Listar categorias de um setor |
| POST/GET | `/usuarios` | Criar e listar usuários; filtros `nome`, `tipo`, `setorId` |
| GET/PUT | `/usuarios/{id}` | Consultar e atualizar usuário |

Chamados:

| Método | Rota | Uso |
|---|---|---|
| POST/GET | `/chamados` | Abrir/listar chamados; ver filtros abaixo |
| GET | `/chamados/{id}` | Detalhe com interações |
| GET | `/usuarios/{id}/chamados` | Chamados do solicitante ou técnico; filtro `status` |
| PATCH | `/chamados/{id}/assumir` | Técnico assume chamado aberto do próprio setor |
| PATCH | `/chamados/{id}/resolver` | Técnico atribuído encerra com solução |
| PATCH | `/chamados/{id}/cancelar` | Solicitante cancela enquanto aberto |
| POST | `/chamados/{id}/interacoes` | Solicitante ou técnico atribuído registra interação |

Toda rota de coleção usa `page`, `size` e `sort`. O padrão é 10 registros e o tamanho máximo é 50. Em `/chamados`, os filtros são `status`, `prioridade`, `setorId`, `categoriaId`, `solicitanteId`, `tecnicoId`, `dataInicio` e `dataFim` (datas no formato `AAAA-MM-DD`). Filtros podem ser combinados.

Exemplo de paginação e filtro:

```text
GET /chamados?status=ABERTO&setorId=1&prioridade=ALTA&page=0&size=10&sort=dataAbertura,desc
```

Listagens retornam `itens`, `pagina`, `tamanho`, `totalElementos` e `totalPaginas`. Erros são JSON com `timestamp`, `status`, `mensagem` e `caminho`; dados inválidos geram 400, recurso ausente 404 e conflito de regra/valor único 409.

## Regras implementadas

- Todo chamado começa como /`ABERTO`, recebe prioridade `MEDIA` se omitida e tem data de abertura atribuída pelo servidor.
- Apenas solicitantes abrem e cancelam os próprios chamados; cancelamento só é possível enquanto aberto.
- Um técnico assume chamado aberto apenas se pertencer ao setor da categoria.
- Só o técnico atribuído resolve o chamado; solução é obrigatória e a data de fechamento é registrada.
- Chamados encerrados não podem receber interação; somente solicitante e técnico atribuído podem interagir.
- Categorias com chamados não podem ser removidas. Setores com categorias ou técnicos vinculados devem ser desassociados antes da remoção para preservar integridade referencial.

Regras de negócio ficam na camada `service`. Controllers recebem/validam requests e delegam. DTOs impedem a exposição direta das entidades JPA. IDs são `Long` com geração no banco; `matricula` também é única para identificar usuários sem ambiguidade operacional.

## Migrations e validação do esquema

Flyway registra as versões executadas em `flyway_schema_history`, incluindo versão, descrição e checksum do arquivo. Se o conteúdo de uma migration aplicada mudar, seu checksum deixa de corresponder ao histórico e o Flyway acusa divergência na validação. Por isso, migrations aplicadas não devem ser editadas: correções e mudanças entram em uma nova versão sequencial (`V5__...sql`).

O Hibernate usa `ddl-auto=validate`: compara o mapeamento das entidades com as tabelas e falha na inicialização se houver incompatibilidade. `ddl-auto=update` não é adequado como controle de esquema porque altera o banco implicitamente, não oferece histórico de mudanças revisável e pode não representar com segurança migrações de dados ou mudanças destrutivas.

## Estrutura por partes

1. `model` e `db/migration`: entidades, enums, relacionamentos e esquema/seeds do banco.
2. `repository`, `service`, `controller` e `api/dto`: persistência, regras de negócio, recursos e contratos HTTP.
3. `api/GlobalExceptionHandler`: erros HTTP padronizados.
4. `Dockerfile`, `docker-compose.yml` e `requests/chamados.http`: execução local e exemplos para exercitar as rotas.

## Requisições de exemplo

Abra `requests/chamados.http` em uma IDE compatível com arquivos HTTP para ver exemplos de todos os endpoints, incluindo fluxo completo de chamado. Requisições de exemplo usam os IDs dos dados de demonstração.

Para compilar localmente com Java 21 e Maven:

```bash
./mvnw -DskipTests package
```
