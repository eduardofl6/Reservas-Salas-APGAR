Idioma: Português | [English](README.en.md)
---

# API de Reservas de Sala

Uma API REST desenvolvida em Java com Spring Boot para gerenciar reservas de salas de reunião e calcular estatísticas de ocupação diária, com armazenamento de dados 100% em memória.

## Tecnologias e Versões

- **Java:** `21`
    
- **Framework:** Spring Boot
    
- **Gerenciador de Dependências:** Maven
    

## Como executar a aplicação

1. Clone este repositório para a sua máquina local.
    
2. Abra o terminal na pasta raiz do projeto.
    
3. Execute o comando abaixo utilizando o Maven Wrapper já incluso no projeto:
    
    **Linux/macOS:**
    
    Bash
    
    ```
    ./mvnw spring-boot:run
    ```
    
    **Windows:**
    
    DOS
    
    ```
    mvnw.cmd spring-boot:run
    ```
    
4. A aplicação estará disponível em `http://localhost:8080`.
    

## Como executar os testes automatizados

Para rodar a suíte de testes unitários e verificar as validações de regra de negócio, execute o seguinte comando no terminal (na raiz do projeto):

**Linux/macOS:**

Bash

```
./mvnw test
```

**Windows:**

DOS

```
mvnw.cmd test
```

## Documentação API

A documentação dos endpoints é montada pela dependência Swagger, dependência que faz leitura automática dos endpoints, para acessa-lo garanta que a aplicação está rodando, então acesse: 
```
http://localhost:8080/swagger-ui/index.html
```

## Observações Relevantes sobre a Solução (Decisões de Arquitetura)

Decisões arquiteturais e Considerações/Trade-Offs

1. **Armazenamento Seguro e Otimizado para Leitura:** Para lidar com a concorrência no acesso aos dados, foi implementado um ```ConcurrentHashMap<LocalDate, List<Reserva>>``` para guardar os dias através de LocalDate, e agenda através de ```List<Reserva> x = CopyOnWriteArrayList<>()```, essa estrutura ConcurrentHashMap foi escolhida pois através do seu método compute() é possível dar write-lock em um bucket (dia), proibindo duplo registro, CopyOnWriteArrayList foi escolhido para as agendas pois é read-heavy, sendo ágil para leitura de dados, ação que após a consulta é a mais frequente em um sistema de reservas. Essa partição por data torna a consulta de estatísticas O(1) para localizar o dia e O(n) para iterar as reservas daquele dia, evitando varredura de toda a coleção.  
    
2. **Blindagem de Fuso Horário:** O OffsetDateTime foi escolhido no lugar de LocalDateTime para preservar o offset enviado pelo cliente. A anotação @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE) impede que o Jackson converta automaticamente o horário para o fuso do servidor, garantindo que 14:00-03:00 seja armazenado e retornado exatamente como foi enviado.
    
3. **Pragmatismo na Modelagem:** Como não há uso de banco de dados, foi optado usar classes como ```Reserva``` e ```Estatistica``` como Model e DTO, visto que suas funções são simples e são usadas tanto pelo controller como pelo service e repository.
    
4. **Tratamento Global de Exceções:** Para atender o requisito de mensagens de erro úteis e consistentes, foi optado por usar um ErrorHandler geral no sistema, o TratadorDeErros, ele capta exceptions mandadas pelo sistema antes de serem enviadas, altera e clarifica o erro antes de enviar uma descrição, e baseia-se na origem para gera-la. Ele capta tanto exceptions lançadas pelas próprios Objetos do sistema como pelo Jackson na entrada. 

5. **Contrato Estrito de API:** Para atender o contrato estrito de APIs foram usadas notações como ```@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)``` e ```@PostMapping(value ="/reservas", consumes = MediaType.APPLICATION_JSON_VALUE)``` para garantir que qualquer body que fosse entrar ou sair do sistema fosse JSON

**Autor:** Eduardo Fortes Luiz
