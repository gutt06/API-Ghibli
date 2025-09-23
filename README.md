<div style="display: inline-block">
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/quarkus-%234794EB.svg?style=for-the-badge&logo=quarkus&logoColor=white">
<img src="https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white">
</div>

# API-Ghibli

## Descrição

API REST desenvolvida com Quarkus para gerenciamento de filmes, diretores e gêneros do Studio Ghibli. A **entidade Filme é o núcleo central da aplicação**, conectando diretores e gêneros através de relacionamentos JPA bem estruturados. A API oferece operações completas de CRUD (Create, Read, Update, Delete) com recursos de busca paginada, validações de integridade referencial e documentação interativa via Swagger UI.

### 🌐 Acesso Online
A API está disponível em produção através do **Render**:
- **URL da API:** https://api-ghibli.onrender.com
- **Swagger UI:** https://api-ghibli.onrender.com/q/swagger-ui
- **OpenAPI Spec:** https://api-ghibli.onrender.com/q/openapi

### 💻 Desenvolvimento Local
Para execução local, utilize:
- **URL da API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/q/swagger-ui

## Características Técnicas

### Stack Tecnológica
- **Framework:** Quarkus
- **Linguagem:** Java 17+
- **ORM:** Hibernate/Panache
- **Banco de Dados:** H2 (desenvolvimento)
- **Validação:** Bean Validation (JSR-303)
- **Documentação:** OpenAPI 3.0 / Swagger UI
- **Build:** Maven

### Arquitetura
- **Padrão:** RESTful API
- **Entidades:** 4 entidades principais com relacionamentos JPA
- **Validações:** Validações declarativas com Bean Validation
- **Transações:** Controle transacional com `@Transactional`
- **Serialização:** JSON via Jackson

## Modelo de Dados

A **entidade Filme é o núcleo central da aplicação**, servindo como ponte entre diretores e gêneros através de relacionamentos bem estruturados:

### Relacionamentos
- **One-to-One:** Diretor ↔ BiografiaDiretor
- **One-to-Many:** Diretor → Filmes (um diretor pode ter vários filmes)
- **Many-to-Many:** Filmes ↔ Gêneros (filmes podem ter múltiplos gêneros)

### Entidade Principal: Filme
A entidade Filme é o centro das operações da API, conectando todas as outras entidades:
```json
{
  "id": 1,
  "titulo": "A Viagem de Chihiro",
  "sinopse": "Uma menina se aventura em um mundo mágico...",
  "anoLancamento": 2001,
  "nota": 9.3,
  "idadeIndicativa": 0,
  "diretor": {
    "id": 1,
    "nome": "Hayao Miyazaki"
  },
  "generos": [
    {"id": 1, "nome": "Fantasia"},
    {"id": 2, "nome": "Aventura"}
  ]
}
```

### Diretor
```json
{
  "id": 1,
  "nome": "Hayao Miyazaki",
  "nascimento": "1941-01-05",
  "nacionalidade": "Japonesa",
  "biografia": {
    "id": 1,
    "textoCompleto": "Biografia completa...",
    "resumo": "Co-fundador do Studio Ghibli",
    "premiosRecebidos": "Oscar, Urso de Ouro..."
  }
}
```

### BiografiaDiretor
```json
{
  "id": 1,
  "textoCompleto": "Hayao Miyazaki é co-fundador do Studio Ghibli e um dos mais influentes diretores de animação da história...",
  "resumo": "Co-fundador do Studio Ghibli, mestre da animação japonesa",
  "premiosRecebidos": "Oscar de Melhor Filme de Animação, Urso de Ouro, Leone d'Oro alla Carriera"
}
```

### Gênero
```json
{
  "id": 1,
  "nome": "Fantasia",
  "descricao": "Filmes com elementos mágicos e mundos fantásticos"
}
```

## Endpoints da API

### Diretores (`/diretores`)

#### `GET /diretores`
Lista todos os diretores cadastrados.

**Resposta:** `200 OK`
```json
[
  {
    "id": 1,
    "nome": "Hayao Miyazaki",
    "nascimento": "1941-01-05",
    "nacionalidade": "Japonesa",
    "biografia": { ... }
  }
]
```

#### `GET /diretores/{id}`
Busca diretor específico por ID.

**Parâmetros:**
- `id` (path): ID do diretor

**Respostas:**
- `200 OK`: Diretor encontrado
- `404 Not Found`: Diretor não existe

#### `GET /diretores/search`
Busca diretores com filtros e paginação.

**Parâmetros de Query:**
- `q` (string): Termo de busca por nome ou nacionalidade
- `sort` (string): Campo de ordenação [`id`, `nome`, `nascimento`, `nacionalidade`]
- `direction` (string): Direção da ordenação [`asc`, `desc`]
- `page` (int): Número da página (padrão: 0)
- `size` (int): Itens por página (padrão: 4)

**Exemplo:** `GET /diretores/search?q=miyazaki&sort=nome&direction=asc&page=0&size=10`

**Resposta:** `200 OK`
```json
{
  "Diretores": [...],
  "TotalDiretores": 5,
  "TotalPages": 1,
  "HasMore": false,
  "NextPage": ""
}
```

#### `POST /diretores`
Cria novo diretor.

**Body:**
```json
{
  "nome": "Novo Diretor",
  "nascimento": "1970-01-01",
  "nacionalidade": "Japonesa",
  "biografia": {
    "textoCompleto": "Biografia...",
    "resumo": "Resumo...",
    "premiosRecebidos": "Prêmios..."
  }
}
```

**Respostas:**
- `201 Created`: Diretor criado com sucesso
- `400 Bad Request`: Dados inválidos

#### `PUT /diretores/{id}`
Atualiza diretor existente.

**Parâmetros:**
- `id` (path): ID do diretor

**Body:** Mesmo formato do POST

**Respostas:**
- `200 OK`: Diretor atualizado
- `404 Not Found`: Diretor não existe
- `400 Bad Request`: Dados inválidos

#### `DELETE /diretores/{id}`
Remove diretor.

**Parâmetros:**
- `id` (path): ID do diretor

**Respostas:**
- `204 No Content`: Diretor removido
- `404 Not Found`: Diretor não existe
- `409 Conflict`: Diretor possui filmes vinculados

### Filmes (`/filmes`)

#### `GET /filmes`
Lista todos os filmes cadastrados.

#### `GET /filmes/{id}`
Busca filme específico por ID.

#### `GET /filmes/search`
Busca filmes com filtros e paginação.

**Parâmetros de Query:**
- `q` (string): Termo de busca por título (texto) ou ano/idade (número)
- `sort` (string): Campo ordenação [`id`, `titulo`, `sinopse`, `anoLancamento`, `nota`, `idadeIndicativa`]
- `direction` (string): Direção [`asc`, `desc`]
- `page` (int): Página (padrão: 0)
- `size` (int): Itens por página (padrão: 4)

#### `POST /filmes`
Cria novo filme.

**Body:**
```json
{
  "titulo": "Novo Filme",
  "sinopse": "Sinopse do filme...",
  "anoLancamento": 2023,
  "nota": 8.5,
  "idadeIndicativa": 0,
  "diretor": {"id": 1},
  "generos": [{"id": 1}, {"id": 2}]
}
```

#### `PUT /filmes/{id}`
Atualiza filme existente.

#### `DELETE /filmes/{id}`
Remove filme.

### Gêneros (`/generos`)

#### `GET /generos`
Lista todos os gêneros.

#### `GET /generos/{id}`
Busca gênero por ID.

#### `GET /generos/search`
Busca gêneros com filtros.

**Parâmetros de Query:**
- `q` (string): Busca por nome
- `sort` (string): Campo [`id`, `nome`, `descricao`]
- `direction`, `page`, `size`: Mesmos parâmetros dos outros endpoints

#### `POST /generos`
Cria novo gênero.

**Body:**
```json
{
  "nome": "Novo Gênero",
  "descricao": "Descrição do gênero"
}
```

#### `PUT /generos/{id}`
Atualiza gênero.

#### `DELETE /generos/{id}`
Remove gênero.

**Respostas:**
- `204 No Content`: Gênero removido
- `409 Conflict`: Gênero possui filmes vinculados

## Validações

### Diretor
- `nome`: Obrigatório, 2-100 caracteres
- `nascimento`: Data no passado
- `nacionalidade`: Obrigatória, máximo 80 caracteres
- `biografia.textoCompleto`: Máximo 2000 caracteres
- `biografia.resumo`: Máximo 100 caracteres

### Filme
- `titulo`: Obrigatório, 1-200 caracteres
- `sinopse`: Obrigatória, máximo 2000 caracteres
- `anoLancamento`: Mínimo 1986 (primeiro filme Ghibli)
- `nota`: Entre 0.0 e 10.0
- `idadeIndicativa`: Não negativa

### Gênero
- `nome`: Obrigatório, 2-50 caracteres
- `descricao`: Máximo 200 caracteres

## Códigos de Resposta HTTP

| Código | Descrição |
|--------|-----------|
| `200` | OK - Operação bem-sucedida |
| `201` | Created - Recurso criado |
| `204` | No Content - Recurso removido |
| `400` | Bad Request - Dados inválidos |
| `404` | Not Found - Recurso não encontrado |
| `409` | Conflict - Violação de integridade referencial |
| `500` | Internal Server Error - Erro interno |

## Exemplos de Uso da API

### Testando com curl

#### Listar todos os filmes:
```bash
curl -X GET "https://api-ghibli.onrender.com/filmes" \
  -H "Accept: application/json"
```

#### Buscar filmes por título:
```bash
curl -X GET "https://api-ghibli.onrender.com/filmes/search?q=chihiro&size=10" \
  -H "Accept: application/json"
```

#### Criar um novo diretor:
```bash
curl -X POST "https://api-ghibli.onrender.com/diretores" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Novo Diretor",
    "nascimento": "1970-01-01", 
    "nacionalidade": "Japonesa",
    "biografia": {
      "textoCompleto": "Biografia completa do diretor...",
      "resumo": "Resumo da carreira",
      "premiosRecebidos": "Prêmios conquistados"
    }
  }'
```

### Exemplos de Resposta

#### GET /filmes/1 (A Viagem de Chihiro):
```json
{
  "id": 1,
  "titulo": "A Viagem de Chihiro",
  "sinopse": "Chihiro, uma menina de 10 anos, se aventura em um mundo mágico...",
  "anoLancamento": 2001,
  "nota": 9.3,
  "idadeIndicativa": 0,
  "diretor": {
    "id": 1,
    "nome": "Hayao Miyazaki",
    "nascimento": "1941-01-05",
    "nacionalidade": "Japonesa"
  },
  "generos": [
    {"id": 1, "nome": "Fantasia"},
    {"id": 2, "nome": "Aventura"},
    {"id": 3, "nome": "Família"},
    {"id": 4, "nome": "Drama"}
  ]
}
```

## Executando o Projeto

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker (opcional para banco PostgreSQL)

### Desenvolvimento
```bash
# Clonar repositório
git clone <repository-url>
cd studio-ghibli-api

# Executar em modo desenvolvimento
mvn quarkus:dev
```

### Produção
```bash
# Build
mvn clean package

# Executar
java -jar target/quarkus-app/quarkus-run.jar
```

## Acessando a Documentação

### 🌐 Produção (Render)
- **Swagger UI:** https://api-ghibli.onrender.com/q/swagger-ui
- **OpenAPI Spec:** https://api-ghibli.onrender.com/q/openapi

### 💻 Desenvolvimento Local
- **Swagger UI:** http://localhost:8080/q/swagger-ui
- **OpenAPI Spec:** http://localhost:8080/q/openapi

### Testando a API
Você pode testar a API de três formas:
1. **Swagger UI Online:** Acesse https://api-ghibli.onrender.com/q/swagger-ui para testar diretamente no navegador
2. **Swagger UI Local:** Execute o projeto localmente e acesse http://localhost:8080/q/swagger-ui
3. **Ferramentas REST:** Use Postman, Insomnia ou curl com a URL base https://api-ghibli.onrender.com

## Configuração do Banco de Dados

### Desenvolvimento (H2)
```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=import.sql
```

## Dados de Exemplo

A API vem pré-configurada com dados do Studio Ghibli:

### Diretores
- Hayao Miyazaki
- Isao Takahata
- Gorō Miyazaki
- Hiromasa Yonebayashi
- Yoshifumi Kondō

### Filmes Clássicos
- A Viagem de Chihiro (2001)
- Meu Vizinho Totoro (1988)
- O Castelo Animado (2004)
- Princesa Mononoke (1997)
- Túmulo dos Vagalumes (1988)
- E outros...

### Gêneros
- Fantasia
- Aventura
- Família
- Drama
- Romance
- Guerra

## Funcionalidades Especiais

### Busca Inteligente
- **Filmes:** Busca por texto (título) ou número (ano/idade)
- **Diretores:** Busca por nome ou nacionalidade
- **Gêneros:** Busca por nome

### Integridade Referencial
- Impede deleção de diretores com filmes
- Impede deleção de gêneros vinculados a filmes
- Validação de existência de entidades relacionadas

### Paginação Avançada
- Controle de página e tamanho
- Informações de navegação (hasMore, nextPage)
- Contadores totais

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## Contato

Para dúvidas sobre a API, consulte a documentação interativa no Swagger UI ou abra uma issue no repositório.