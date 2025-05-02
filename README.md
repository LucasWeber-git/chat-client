# Execução
1) Executar o método `main` da classe `Server`
2) Para cada cliente, executar o método `main` da classe `Client`

# Protocolo

- A mensagem é lida linha a linha, sendo que o caractere de quebra é o `\n`
- A primeira linha (header) é composta por: `quantidade de linhas` `|` `método`
  - A quantidade de linhas indica quantas linhas o `corpo` da mensagem contém
  - Os métodos podem ser: `GET_USERS`, `CREATE_USER`, `SEND_PUBLIC_MESSAGE` e `SEND_PRIVATE_MESSAGE`
  - Ao criar um novo usuário, o servidor notifica todos os usuários com `USER_CREATED`, para que suas listas sejam atualizadas
- O restante da mensagem é o corpo, sendo cada propriedade composta por chave/valor e o caractere de separação é `|`
- Obs: caracteres em branco no início e no final serão ignorados
- Obs2: a ordem das propriedades no corpo da mensagem não importa

### GET_USERS

#### Request
```
0 | GET_USERS
```

#### Response
```
1 | GET_USERS
usernames | usuario1, usuario2, usuario3, usuario4
```

### CREATE_USER
#### Request
```
1 | CREATE_USER
username | usuario1
```

#### Response sucesso
```
0 | CREATE_USER
```

#### Response erro
```
1 | CREATE_USER
error | DUPLICATED_USER
```

### USER_CREATED
```
0 | USER_CREATED
```

### SEND_PRIVATE_MESSAGE

#### Request
```
2 | SEND_PRIVATE_MESSAGE
recipient | usuario2
content | Olá, tudo bem?
```

#### Response
```
0 | SEND_PRIVATE_MESSAGE
```

#### Mensagem recebida no destinatário
```
2 | SEND_PRIVATE_MESSAGE
sender | usuario1
content | Olá, tudo bem?
```

### SEND_PUBLIC_MESSAGE

#### Request
```
1 | SEND_PUBLIC_MESSAGE
content | Olá, tudo bem?
```

#### Response
```
0 | SEND_PUBLIC_MESSAGE
```

#### Mensagem recebida no destinatário
```
2 | SEND_PUBLIC_MESSAGE
sender | usuario1
content | Olá, tudo bem?
```