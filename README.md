# Condominium - Sistema de Gestão Condominial

---

## 📌 Visão Geral

O *Sistema de Gestão Condominial* é uma aplicação Java orientada a objetos que visa resolver problemas administrativos comuns em condomínios, como inadimplência, conflitos entre moradores e manutenção deficiente das áreas comuns. O objetivo é fornecer uma plataforma digital eficaz para gerenciar, automatizar e otimizar processos internos condominiais.

---

## 🚀 Objetivos Principais

- Melhorar a eficiência administrativa e financeira dos condomínios.
- Garantir transparência e acessibilidade nas informações para moradores e funcionários.
- Facilitar a comunicação e reduzir conflitos internos através de canais claros e eficazes.
- Automatizar a gestão de espaços e manutenção preventiva.

---

## 👥 Participantes

- Arthur Barros
- Dácio Augusto
- Leonardo Granja  
- João Vitor Sacramento 
- Thiago Pinto  
- Tomás Brandão  
 

---

## 📋 Funcionalidades Previstas

- Cadastro e gestão de moradores.
- Controle e registro de pagamentos e inadimplências.
- Gerenciamento de reservas das áreas comuns (salão, churrasqueira, etc.).
- Emissão e gerenciamento de comunicados e avisos.
- Gestão de permissões e acessos de usuários ao sistema.
- Registro e monitoramento de ocorrências e conflitos internos.
- Agendamento e gerenciamento de assembleias e reuniões.
- Consulta ao histórico detalhado de manutenções e pagamentos.

---

## 🛠 Tecnologias Utilizadas

- *Java 21* (Programação Orientada a Objetos)
- *Spring Boot 3.2.5* (Framework para desenvolvimento de aplicações)
- *Spring Data JPA* (Persistência de dados)
- *Hibernate* (Implementação JPA)
- *H2 Database* (Banco de Dados em arquivo para desenvolvimento)
- *Maven* (Gerenciador de dependências e build)
- *JUnit 5* (Framework de testes)
- Arquitetura em Camadas (Apresentação CLI, Negócio e Persistência)

---

## 🚀 Configuração do Ambiente e Execução do Projeto

Esta seção guiará você na configuração do ambiente necessário para compilar e executar o projeto.

### ✅ Pré-requisitos

Antes de começar, certifique-se de ter instalado em seu sistema:

- **Java Development Kit (JDK)**: Versão 21 ou superior  
  [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.org/)
- **Apache Maven**: Versão 3.6.x ou superior  
  [Download Maven](https://maven.apache.org/download.cgi)
- **Git**: Para clonar o repositório  
  [Download Git](https://git-scm.com/downloads)
- **IDE (opcional, mas recomendado)**:
  - IntelliJ IDEA (Community ou Ultimate)
  - Visual Studio Code com extensões de Java e Spring Boot
  - Eclipse IDE for Java Developers

---

### ⚙️ Passos para Configuração e Execução

#### 1. Clonar o Repositório

```bash
git clone https://github.com/guttinue/condominium
cd condominium
```

#### 2. Compilar o Projeto com Maven

```bash
mvn clean install
```

> Este comando também executa os testes automatizados.  
> Para compilar rapidamente sem rodar os testes:

```bash
mvn clean install -DskipTests
```

#### 3. Executar a Aplicação

Após o build, execute o JAR gerado:

```bash
java -jar target/condominium-0.0.1-SNAPSHOT.jar
```

Ou, durante o desenvolvimento, execute via Maven:

```bash
mvn spring-boot:run
```

---

### 🧪 Acessando o Banco de Dados H2 (primeira execução)

Com o H2 configurado para modo arquivo, os dados persistirão entre as reinicializações da aplicação. Para acessar o console H2 e inspecionar os dados:

#### 1. Certifique-se de que as seguintes propriedades estão ativas no application.properties:

```properties
spring.datasource.url=jdbc:h2:file:./data/condominiumdb
spring.jpa.hibernate.ddl-auto=create 
spring.sql.init.mode=always 
spring.jpa.defer-datasource-initialization=true
```


### 🗃️ Dados Iniciais

Após a primeira execução, altere as propriedades em application.proprieties  

```properties
spring.datasource.url=jdbc:h2:file:./data/condominiumdb
spring.jpa.hibernate.ddl-auto=update # ou validate
spring.sql.init.mode=never # ou embedded, ou remova/renomeie data.sql se não quiser que rode mais
spring.jpa.defer-datasource-initialization=tru
```

Depois disso, clique em maven e escolha a opçãao "Reload All Maven Projects"

Ele popula o sistema com exemplos de:
- Condomínios
- Áreas comuns
- Usuários (moradores, síndico, funcionário)

---

### 📂 Estrutura do Projeto (Visão Simplificada)

```
condominium/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/condo/
│   │   │       ├── CondominiumCliApplication.java
│   │   │       ├── domain/
│   │   │       ├── menu/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
│       └── java/
│           └── com/condo/
├── pom.xml
└── README.md
```

---
## 🗂 Entrega 01

<details>
<summary><b>📌 Histórias do Usuário</b></summary>

<br>

| Nº | Perfil                    | História                                     |
|----|---------------------------|----------------------------------------------|
| 1  | Morador                   | Cadastro/Login                               |
| 2  | Morador                   | Reserva de áreas comuns                      |
| 3  | Morador                   | Registro de problemas de manutenção          |
| 4  | Funcionário Administrativo| Cadastro de novos moradores                  |
| 5  | Funcionário Administrativo| Registrar pagamentos das taxas               |
| 6  | Síndico                   | Envio de avisos e comunicados oficiais       |
| 7  | Síndico                   | Agendamento e registro de assembleias        |
| 8  | Morador                   | Consulta histórico financeiro                |

🔗 [Clique aqui para acessar as histórias e seus critérios de aceitação](https://docs.google.com/document/d/1cEao7RFi_IAkyKRBPETrI6R5NwgSLFkRdxgUmrukpas/edit?usp=sharing)

</details>

<details>
<summary><b>🖥 Protótipo Lo-Fi</b></summary>

<br>

📁 *Protótipos iniciais Lo-Fi das principais telas*  
🔗 [Clique aqui para visualizar os protótipos](https://drive.google.com/file/d/1izdiABJx6wsdaE88PcFmbcOxtpXIwX8a/view?usp=sharing)

*Telas prototipadas:*
- Tela de Login  
- Tela Inicial (Dashboard do Morador)  
- Cadastro de Morador  
- Registro de Pagamentos  
- Tela de Reservas das Áreas Comuns  
- Tela de Registro de Manutenção  
- Avisos e Comunicados (Síndico/Admin)  

</details>

<details>
<summary><b>🎥 Screencast - Entrega 01</b></summary>

<br>

▶ *Demonstração das funcionalidades entregues nesta etapa*  
🔗 [Clique aqui para assistir o Screencast](https://youtu.be/sXgaN3N0s4o)

*Conteúdo abordado no Screencast:*
- Apresentação das Histórias do Usuário  
- Explicação rápida dos protótipos Lo-Fi  
- Apresentação dos próximos passos do projeto  

</details>

## 🗂 Entrega 2

<details>
<summary><b>📖 Histórias Implementadas:</b></summary>

- *Cadastro de Moradores e Dependentes* (com persistência em memória)
- *Reserva de Áreas Comuns* (com persistência em memória)
</details>

<details>
<summary><b>🛠 Ambiente de Versionamento</b></summary>

- Ambiente versionado no GitHub com commits frequentes (mínimo semanal).

🔗 [Acesse o histórico de commits aqui](https://github.com/guttinue/condominium/commits/main)
</details>


<details>
<summary><b>📊 Diagrama de Classes Completo</b></summary>

- [Clique aqui para visualizar o Diagrama de Classes em Alta Resolução](./entregas/diagrama_de_classes.png)
</details>

<details>
<summary><b>🐞 Issue/Bug Tracker</b></summary>

⚒ Utilizamos o ClickUp como ferramenta de gerenciamento de Bugs e Tarefas

- [Visualizar o Issue Tracker atualizado aqui](./entregas/issue_tracker.png)
</details>

<details>
<summary><b>🎥 Screencast de Demonstração</b></summary>

▶ Assista o Screencast no YouTube mostrando o uso do sistema implementado nesta entrega:

🔗 [Clique aqui para assistir](https://youtu.be/sGfDrOjskvE)
</details>

---

## 🗂 Entrega 3

<details>
<summary><b>📖 Histórias Implementadas:</b></summary>

- *Reportar problemas de manutenção* 
- *Agendar assembleias e reuniões* 
</details>

<details>
<summary><b>🛠 Ambiente de Versionamento</b></summary>

- Ambiente versionado no GitHub com commits frequentes (mínimo semanal).

🔗 [Acesse o histórico de commits aqui](https://github.com/guttinue/condominium/commits/main)
</details>


<details>
<summary><b>📊 Diagrama de Classes Completo</b></summary>
 
🔗 [Clique aqui para visualizar o Diagrama de Classes em Alta Resolução](./entregas/diagrama_de_classes_new.drawio.png)
</details>

<details>
<summary><b>🐞 Issue/Bug Tracker</b></summary>

⚒ [Clique aqui para visualizar o Issue Tracker atualizado aqui](https://github.com/guttinue/condominium/issues)
</details>

<details>
<summary><b>▶ Screencast</b></summary>

▶ Assista o Screencast no YouTube mostrando o uso do sistema implementado nesta entrega:

🔗 [Clique aqui para assistir](https://www.youtube.com/watch?v=xYrWPnL8QkA)
</details>

<details>
<summary><b>📝 Testes Automatizados</b></summary>


📝 Assista ao vídeo de testes automzatizados no nosso sistema utilizando o Maven

🔗 [Clique aqui para assistir](https://www.youtube.com/watch?v=mtcnFiv77g8)
</details>

---

## 🗂 Entrega 4

<details>
<summary><b>📖 Histórias Implementadas:</b></summary>

- *Registro de visistantes no condomínio* 
- *Cadastro de veículos de moradores*
- *Registro de pagamentos das taxas condominiais*

🔗 [Clique aqui para vizualizar as historias no formato BDD](https://docs.google.com/document/d/1atD3BnABAXsUcRFY7HiJjJL5QWME2WjqwFCLNFSzlHs/edit?usp=sharing)
</details>

<details>
<summary><b>🛠 Ambiente de Versionamento</b></summary>

- Ambiente versionado no GitHub com commits frequentes (mínimo semanal).

🔗 [Acesse o histórico de commits aqui](https://github.com/guttinue/condominium/commits/main)
</details>


<details>
<summary><b>📊 Diagrama de Classes Completo</b></summary>

- [Clique aqui para visualizar o Diagrama de Classes em Alta Resolução](./entregas/diagramaAtualizado.png)
</details>

<details>
<summary><b>🐞 Issue/Bug Tracker</b></summary>
 
⚒ [Clique aqui para visualizar o Issue Tracker atualizado aqui](https://github.com/guttinue/condominium/issues)

</details>

<details>
<summary><b>▶ Screencast</b></summary>

▶ Assista o Screencast no YouTube mostrando o uso do sistema implementado nesta entrega:

🔗 [Clique aqui para assistir](https://youtu.be/6Z-DswcfUAg)
🔗 [Clique aqui para assistir](https://youtu.be/jvuRR2rQ7Bk)
🔗 [Clique aqui para assistir](https://youtu.be/Urv2Q1oAKY0)
</details>

<details>
<summary><b>📝 Testes Automatizados</b></summary>




📝 Assista ao vídeo de testes automzatizados no nosso sistema utilizando o Maven

🔗 [Clique aqui para assistir]()
</details>
