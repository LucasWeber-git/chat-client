## Execução
1) Executar o método `main` da classe `Server`
2) Para cada cliente, executar o método `main` da classe `Client`

## Protocolo

- A mensagem é lida linha a linha, sendo que o caractere de quebra é o `\n`
- A primeira linha (header) é composta por: `quantidade de linhas` `|` `método`
  - A quantidade de linhas indica quantas linhas o `corpo` da mensagem contém
  - Os métodos podem ser: `GET_USERS`, `CREATE_USER`, `PUBLIC_MESSAGE` e `PRIVATE_MESSAGE`
- O restante da mensagem é o corpo, sendo cada propriedade composta por chave/valor e o caractere de separação é `|`
- Obs: caracteres em branco no início e no final serão ignorados

**Exemplo:**
```
2 | PRIVATE_MESSAGE
recipient | fulano de tal
content | Olá mundo!
````