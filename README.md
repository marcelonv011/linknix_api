# LinkNix API

API REST integradora para classificação de chamados provenientes de sistemas Help Desk por meio de modelos de inteligência artificial.

Este guia explica, desde o início, como preparar o projeto no Windows, instalar o PostgreSQL e o pgAdmin 4, configurar o `application.properties`, criar o banco de dados e executar a aplicação sem definir variáveis no CMD ou no PowerShell.

## 1. Programas necessários

### 1.1 Java 21 JDK

O projeto utiliza Java 21. Recomenda-se o Eclipse Temurin JDK 21:

<https://adoptium.net/temurin/releases/?version=21>

Na página de download, selecione:

- Version: `21 - LTS`.
- Operating System: `Windows`.
- Architecture: `x64`, exceto se o computador utilizar ARM.
- Package Type: `JDK`.
- Formato: instalador `.msi`.

Durante a instalação, marque as opções para configurar o `JAVA_HOME` e adicionar o Java ao `PATH`, caso elas sejam apresentadas pelo instalador.

### 1.2 PostgreSQL Server e pgAdmin 4

Baixe o PostgreSQL pela página oficial:

<https://www.postgresql.org/download/windows/>

É importante instalar o PostgreSQL Server, e não apenas o pgAdmin 4. O pgAdmin é uma ferramenta gráfica para administrar bancos de dados, mas não substitui o servidor PostgreSQL.

No instalador, mantenha selecionados:

- PostgreSQL Server.
- pgAdmin 4.
- Command Line Tools.

O `Stack Builder` é opcional e não é necessário para o LinkNix.

Durante a instalação:

1. Mantenha o diretório de instalação sugerido.
2. Escolha uma senha para o usuário administrador `postgres`.
3. Guarde essa senha, pois ela será utilizada no pgAdmin e no `application.properties`.
4. Mantenha a porta padrão `5432`.
5. Mantenha a configuração regional sugerida.
6. Conclua a instalação.

### 1.3 IntelliJ IDEA

Baixe o IntelliJ IDEA em:

<https://www.jetbrains.com/idea/download/>

Os recursos essenciais para projetos Java e Maven podem ser utilizados gratuitamente. Não é necessário instalar o Maven separadamente, pois o projeto possui o Maven Wrapper.

## 2. Senhas que não devem ser confundidas

Durante a preparação, podem aparecer senhas diferentes:

1. **Senha do usuário PostgreSQL `postgres`:** definida durante a instalação do PostgreSQL. A aplicação utiliza essa senha para se conectar ao banco de dados.
2. **Senha mestra do pgAdmin 4:** o pgAdmin pode solicitá-la na primeira abertura. Ela serve apenas para proteger as senhas salvas dentro do pgAdmin.
3. **API keys dos modelos de IA:** pertencem à OpenAI, Claude ou DeepSeek. Elas ainda não são utilizadas nesta etapa e nunca devem ser escritas diretamente no código.

## 3. Registrar o PostgreSQL no pgAdmin 4

Abra o pgAdmin 4 pelo menu Iniciar do Windows.

Se ele solicitar uma senha mestra, crie uma senha que você consiga lembrar. Ela não precisa ser igual à senha do usuário `postgres`.

### 3.1 Abrir o cadastro do servidor

No painel esquerdo:

1. Clique com o botão direito em `Servers`.
2. Selecione `Register`.
3. Selecione `Server...`.

### 3.2 Aba General

No campo `Name`, escreva:

```text
PostgreSQL Local
```

Esse nome é apenas uma identificação visual dentro do pgAdmin.

### 3.3 Aba Connection

Preencha os campos da seguinte forma:

| Campo | Valor |
|---|---|
| Host name/address | `localhost` |
| Port | `5432` |
| Maintenance database | `postgres` |
| Username | `postgres` |
| Password | A senha escolhida durante a instalação do PostgreSQL |

Ative a opção `Save password` caso não queira digitar a senha em todas as conexões.

Depois, clique em `Save`.

### 3.4 Erros frequentes de conexão

#### Either Host name or Service must be specified

Abra a aba `Connection` e escreva `localhost` no campo `Host name/address`.

#### Connection refused

Normalmente, esse erro significa que o PostgreSQL Server não está instalado ou que o serviço está parado. Abra `Serviços` do Windows e verifique se existe um serviço com nome semelhante a `postgresql-x64` e se o status está como `Em execução`.

#### Password authentication failed for user postgres

A senha informada não corresponde à senha definida durante a instalação do PostgreSQL. Não utilize nesse campo a senha mestra do pgAdmin.

## 4. Criar o banco de dados LinkNix

Depois de conectar ao servidor:

1. Expanda `Servers`.
2. Expanda `PostgreSQL Local`.
3. Clique com o botão direito em `Databases`.
4. Selecione `Create`.
5. Selecione `Database...`.
6. No campo `Database`, escreva exatamente `linknix`.
7. No campo `Owner`, selecione `postgres`.
8. Clique em `Save`.

O banco deve se chamar `linknix`, em letras minúsculas, pois esse é o nome utilizado na configuração da aplicação.

Não crie as tabelas manualmente. O Flyway criará as tabelas quando a aplicação for iniciada pela primeira vez.

## 5. Abrir o projeto no IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Selecione `Open`.
3. Localize a pasta do projeto `linknix_api` ou a pasta onde o repositório foi baixado.
4. Selecione a pasta que contém o arquivo `pom.xml`.
5. Clique em `Open`.
6. Caso o IntelliJ pergunte se você confia no projeto, selecione `Trust Project`.
7. Aguarde o Maven baixar e indexar as dependências.

Verifique se o SDK do projeto está configurado como Java 21:

1. Abra o menu `File`.
2. Abra `Project Structure`.
3. Em `Project SDK`, selecione o JDK 21 instalado.
4. Em `Language level`, selecione `21`.

## 6. Configurar o application.properties

Abra o arquivo:

```text
src/main/resources/application.properties
```

A configuração é:

```properties
spring.application.name=linknix

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/linknix}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:ALTERE_AQUI_SUA_SENHA}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

linknix.jwt.secret=${JWT_SECRET:linknix-chave-local-segura-com-32-caracteres}
linknix.jwt.expiracao-minutos=${JWT_EXPIRATION_MINUTES:120}
```

Altere somente esta linha:

```properties
spring.datasource.password=${DB_PASSWORD:ALTERE_AQUI_SUA_SENHA}
```

Substitua o texto pela senha real do usuário PostgreSQL `postgres`. Por exemplo, se durante a instalação você escolheu `MinhaSenhaLocal123`, a linha local ficará assim:

```properties
spring.datasource.password=${DB_PASSWORD:MinhaSenhaLocal123}
```

Não adicione aspas e não deixe espaços antes ou depois da senha.

> **Segurança:** nunca envie para o GitHub um `application.properties` contendo sua senha real. Antes de criar um commit, restaure o marcador `ALTERE_AQUI_SUA_SENHA`. Em etapas posteriores, serão utilizados perfis locais ou variáveis de ambiente para proteger credenciais.

### O que significa cada propriedade

- `spring.datasource.url`: informa que o PostgreSQL está no computador local, na porta `5432`, e que o banco se chama `linknix`.
- `spring.datasource.username`: usuário utilizado pela aplicação para se conectar.
- `spring.datasource.password`: senha do usuário PostgreSQL.
- `spring.datasource.driver-class-name`: driver JDBC do PostgreSQL.
- `spring.jpa.hibernate.ddl-auto=validate`: o Hibernate verifica as tabelas, mas não cria nem altera o esquema.
- `spring.jpa.open-in-view=false`: evita manter sessões JPA abertas durante toda a resposta HTTP.
- `spring.flyway.enabled=true`: ativa as migrações do banco de dados.
- `spring.flyway.locations`: informa onde estão os scripts SQL versionados.

## 7. Executar o LinkNix sem utilizar CMD

No IntelliJ IDEA:

1. Abra `src/main/java/br/com/linknix/LinkNixApplication.java`.
2. Localize o método `main`.
3. Clique no triângulo verde ao lado da classe ou do método `main`.
4. Selecione `Run 'LinkNixApplication'`.

Durante a primeira inicialização:

1. O Spring Boot se conectará ao banco `linknix`.
2. O Flyway lerá o diretório `src/main/resources/db/migration`.
3. O Flyway executará `V1__create_initial_schema.sql`.
4. As tabelas do domínio serão criadas.
5. O Hibernate verificará se as tabelas correspondem às entidades.

Quando tudo funcionar corretamente, o console do IntelliJ exibirá mensagens semelhantes a:

```text
Successfully applied 1 migration
Tomcat started on port 8080
Started LinkNixApplication
```

Não interrompa a execução enquanto quiser manter a API ativa.

## 8. Verificar as tabelas no pgAdmin 4

Depois de iniciar o LinkNix:

1. Volte ao pgAdmin 4.
2. Expanda `Databases`.
3. Expanda `linknix`.
4. Expanda `Schemas`.
5. Expanda `public`.
6. Clique com o botão direito em `Tables`.
7. Selecione `Refresh`.

As seguintes tabelas devem aparecer:

```text
categorias_classificacao
chamados
classificacoes_ia
clientes_helpdesk
criterios_comparacao
execucoes_teste
flyway_schema_history
metricas_classificacao
modelos_ia
prompts
provedores_ia
resultados_comparativos
usuarios
```

A tabela `flyway_schema_history` é criada automaticamente pelo Flyway para registrar quais migrações foram executadas.

### Verificar pelo Query Tool

1. Clique com o botão direito no banco `linknix`.
2. Selecione `Query Tool`.
3. Cole a consulta abaixo:

```sql
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

4. Clique no botão `Execute/Refresh` ou pressione `F5`.

Para verificar a migração do Flyway, execute:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Deve aparecer a migração de versão `1`, descrição `create initial schema` e `success = true`.

## 9. Problemas frequentes ao iniciar a aplicação

### FATAL: password authentication failed

A senha definida no `application.properties` não corresponde à senha do usuário `postgres`.

### FATAL: database linknix does not exist

O banco `linknix` ainda não foi criado no pgAdmin ou possui outro nome.

### Connection refused

O PostgreSQL Server não está iniciado, o host não é `localhost` ou a porta não é `5432`.

### Unsupported class file ou versão incorreta do Java

O IntelliJ está utilizando uma versão diferente do Java. Configure o `Project SDK` e a configuração de execução com o JDK 21.

### Flyway encontrou tabelas existentes sem histórico

O banco não estava vazio antes da primeira inicialização. Para um teste inicial, exclua o banco `linknix`, crie-o novamente vazio pelo pgAdmin e execute a aplicação outra vez.

### As tabelas não aparecem no pgAdmin

Verifique se a aplicação iniciou sem erros e depois utilize `Refresh` em `Tables`. Confirme também que você está visualizando o banco `linknix` e o schema `public`.

## 10. Migrações do banco de dados

A migração inicial está localizada em:

```text
src/main/resources/db/migration/V1__create_initial_schema.sql
```

As futuras alterações do esquema devem ser adicionadas como novas migrações, por exemplo:

```text
V2__descricao_da_alteracao.sql
V3__outra_modificacao.sql
```

Não altere uma migração que já tenha sido executada em um banco compartilhado. O Flyway controla a integridade de cada arquivo por meio de checksum.

## 11. O que a aplicação configura automaticamente

Ao iniciar a aplicação em um banco vazio, o Flyway executa três migrações:

1. `V1__create_initial_schema.sql`: cria as tabelas, relacionamentos, índices e restrições.
2. `V2__insert_initial_categories.sql`: cadastra as categorias `DEV` e `SUPORTE`.
3. `V3__insert_initial_ai_configuration.sql`: cadastra o prompt ativo, o critério de maioria, os providers OpenAI, Claude e DeepSeek e um modelo simulado de cada provider.

Os providers são simulados. Portanto, não é necessário comprar créditos nem cadastrar chaves da OpenAI, Anthropic ou DeepSeek para testar o TCC.

## 12. Abrir o Swagger

Com a aplicação em execução, abra o navegador em:

```text
http://localhost:8080/swagger-ui.html
```

O Swagger apresenta todos os endpoints e permite executar as requisições sem instalar o Postman.

## 13. Criar o primeiro administrador

O banco começa sem usuários e não possui uma senha padrão insegura. O primeiro administrador é criado uma única vez.

No Swagger:

1. Abra `Auth Controller`.
2. Abra `POST /api/auth/bootstrap`.
3. Clique em `Try it out`.
4. Informe, por exemplo:

```json
{
  "nome": "Administrador",
  "email": "admin@linknix.com",
  "senha": "SenhaSegura123"
}
```

5. Clique em `Execute`.

A resposta deve possuir status `201 Created`. Depois que o primeiro usuário existir, esse endpoint retorna conflito e não permite criar outro administrador sem autenticação.

## 14. Fazer login e autorizar o Swagger

1. Abra `POST /api/auth/login`.
2. Informe o e-mail e a senha cadastrados.
3. Copie somente o valor do campo `token` da resposta.
4. Clique no botão `Authorize`, localizado na parte superior do Swagger.
5. Cole o token no campo apresentado.
6. Clique em `Authorize` e depois em `Close`.

O Swagger enviará automaticamente o cabeçalho `Authorization: Bearer <token>` nos endpoints protegidos.

## 15. Cadastrar um cliente Help Desk

Antes de enviar chamados, cadastre o sistema externo que realizará a integração. Abra `POST /api/admin/clientes-helpdesk` e informe:

```json
{
  "nome": "JEDi",
  "sistemaOrigem": "JEDi Educa",
  "apiKey": "jedi-chave-local-segura-1234567890",
  "ativo": true
}
```

A API Key deve possuir pelo menos 32 caracteres. Guarde o valor informado, pois ele será utilizado pelo Help Desk para enviar chamados.

Por segurança, o LinkNix armazena somente o hash SHA-256 da API Key. A resposta mostra `********` e não permite recuperar a chave original.

## 16. Enviar e classificar um chamado

Abra `POST /api/chamados`. No campo `X-API-Key`, informe a mesma chave usada no cadastro do cliente.

No corpo da requisição, informe somente os dados pertencentes ao ticket:

```json
{
  "codigoExterno": "TICKET-100",
  "titulo": "Usuário não consegue acessar o sistema",
  "descricao": "Ao informar a senha, o sistema apresenta uma mensagem de erro."
}
```

O usuário não informa o sistema de origem nem a categoria. O LinkNix identifica o cliente pela API Key, obtém `JEDi Educa` automaticamente e carrega `DEV` e `SUPORTE` do PostgreSQL.

A aplicação executa o seguinte fluxo:

```text
recebe o chamado
→ identifica o Help Desk
→ busca o prompt ativo
→ busca as categorias ativas
→ monta o prompt final
→ executa OpenAI, Claude e DeepSeek simulados
→ salva uma ClassificacaoIA por modelo
→ aplica o critério de maioria
→ salva o ResultadoComparativo
→ marca o chamado como CLASSIFICADO
```

A resposta contém o chamado, as três classificações e o resultado final.

## 17. Consultar os resultados

| Endpoint | Finalidade |
|---|---|
| `GET /api/chamados` | Lista os chamados recebidos |
| `GET /api/chamados/{id}` | Consulta um chamado |
| `GET /api/classificacoes/chamado/{chamadoId}` | Lista as respostas dos modelos |
| `GET /api/resultados/chamado/{chamadoId}` | Consulta a classificação final |
| `GET /api/metricas/{id}` | Consulta uma métrica de acerto |

Os endpoints iniciados por `/api/admin` permitem cadastrar e consultar usuários, clientes Help Desk, categorias, prompts, providers, modelos, critérios e execuções de teste. Apenas usuários com perfil `ADMINISTRADOR` podem acessá-los.

## 18. Configuração de segurança

- As senhas dos usuários são armazenadas com BCrypt.
- As API Keys de clientes são armazenadas como hash SHA-256.
- Os endpoints privados utilizam JWT assinado com HS256.
- O token expira em 120 minutos por padrão.
- O projeto não contém chaves reais de modelos de IA.
- Em produção, defina `DB_PASSWORD` e `JWT_SECRET` como variáveis de ambiente e use uma chave JWT aleatória com pelo menos 32 caracteres.

## 19. Execução opcional com Docker

O projeto também contém `Dockerfile` e `docker-compose.yml`. O Docker Compose inicia PostgreSQL e LinkNix juntos. Essa forma é opcional; o procedimento com PostgreSQL, pgAdmin e IntelliJ continua funcionando normalmente.
