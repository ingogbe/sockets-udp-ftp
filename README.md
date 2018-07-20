# README #

Trabalho de Redes usando DatagramSocket em Java

FTP (Servidor/Cliente)

### Projetos inclusos no repositório

```sh
1 client
2 framework
3 server
```

### Alterações antes do uso

É necessário fazer alteração na constante `SERVER_STORAGE` no arquivo `ServerMessageThread.java` no projeto `server`, para o local onde deseja que o servidor utilize par armazenar os arquivos.

```sh
/server/src/server/ServerMessageThread.java
```

```java
public static final String SERVER_STORAGE = "C:/Users/SEU_PC/Desktop/serverStorage/";
```

### Configuração antes do uso (Eclipse IDE)

- Crie todos os projetos
    * Vá em **File**
    * Em seguida em **New**
    * E clique na opção **Java Project**
    * Deselecione a opção **Use default location**
    * Clique em **Browse**
    * Navega para a pasta onde deu clone ou salvou o repositorio
    * Selecione o projeto que deseja criar (Projetos 1, 2 e 3 citados anteriormente)
    * Clique em **Finish**
    * Faça isso para os três projetos
    

- Faça a ligação do projeto `framework` com os projetos `client` e `server`
    * Clique com o botão direito sobre o projeto **`client`**
    * Vá até a opção **Build path**
    * Clique na opção **Configure Build Path...**
    * Na janela que se abriu. Clique na aba **Projects**
    * Clique no botão **Add**
    * Selecione o projeto **`framework`**
    * Clique em **Ok** e em seguida em **Apply and Close**
    * Repita o procedimento com o projeto **`server`**

### Uso

* Inicie primeiro o Server (`Main.java`)
* Inicie quantos Clientes desejar (`Main.java`)

#### Comandos Server

| Comando 		| Descrição 													| Exemplo												|
| ------------- | ------------------------------------------------------------- | ----------------------------------------------------- |
| enter 		| Iniciar o servidor (por padrão inicia com o IP Localhost) 	| `enter`													|
| open 			| Iniciar o servidor (por padrão inicia com o IP Localhost) 	| `open`													|
| connect 		| Iniciar o servidor (por padrão inicia com o IP Localhost) 	| `connect`												|
| start 		| Iniciar o servidor (por padrão inicia com o IP Localhost) 	| `start`													|
| exit 			| Parar o servidor 												| `exit`													|
| close 		| Parar o servidor 												| `close` 												|
| disconnect 	| Parar o servidor 												| `disconnect` 											|
| stop 			| Parar o servidor 												| `stop`													|
| ping 			| Ping para cliente especifico 									| `ping <ip>:<port>`										|
| show 			| Mostra lista do parametro passado								| `show users` <br/> `show files` <br/> `show ping requests`	|
| refresh 		| Atualiza lista do parametro passado							| `refresh userlist`										|


#### Comandos Client

| Comando 		| Descrição 														| Exemplo												|
| ------------- | ----------------------------------------------------------------- | ----------------------------------------------------- |
| enter 		| Iniciar o client 													| `enter <name> <server_ip>`								|
| open 			| Iniciar o client 													| `open <name> <server_ip>`								|
| connect 		| Iniciar o client 													| `connect <name> <server_ip>`							|
| start 		| Iniciar o client 										 			| `start <name> <server_ip>`								|
| exit 			| Parar o client 													| `exit`													|
| close 		| Parar o client 													| `close` 												|
| disconnect 	| Parar o client 													| `disconnect` 											|
| stop 			| Parar o client 													| `stop`													|
| ping 			| Ping para client/server especifico ou server passado ao iniciar 	| `ping <ip>:<port>` <br/> `ping server`					|
| list 			| Atualiza lista do parametro passado							 	| `list users` <br/> `list files` 							|
| get			| Download de arquivo especificado (id do arquivo no 'list files' e salva através de FileChooser)	| `get <file_id>`											|
| download 		| Download de arquivo especificado (id do arquivo no 'list files' e salva através de FileChooser)	| `download <file_id>`									|
| put			| Upload de arquivo especificado (FileChooser)						| `get <file_id>`											|
| upload 		| Upload de arquivo especificado (FileChooser)						| `download <file_id>`									|


