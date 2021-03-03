# TestApiRest
Automação de teste de API em Java com RestAssured utilizando o servidor Rest: serverest.dev - O ServeRest é um servidor REST que simula uma loja virtual com intuito de servir de material de estudos de testes de API.

# Cenários de teste API: https://serverest.dev

## Configurações:
 - Dependências: rest-assured 4.0.0, junit 4.12
 - A estrutura do projeto comtém uma interface chamada "Constant.java" onde as contantes do projeto estão armazenadas, na "BaseTest.java" a configuração do projeto está definida na classe "setup()" e os métodos de teste estão no package= "tests". Para rodar o projeto é necessário executar cada classe de teste de forma indivudual.
 
## Melhorias:
 - Criar um report para os tests
 - Criar uma classe tipo Runner para que os start dos tests seja feito uma única vez
 - Criar uma estrutura mais robusta
 - Criar uma esteira de test com ferramentas de Integração continua como por exemplo: "Jenkins"

## Observações:
 - Alguns métodos ficaram extensos devido a necessidade de se limpar a base após a execução para que não haja poluição da mesma, por se tratar de uma API aberta.

## Cenário 1: Autenticação Login:
 - credencial incorreta
 - credencial correta
 - Tentar acesso direto na API

## Cenário 2: Autenticação Conta Usuario:
 - cadastrar usuario e verificar se ele foi inserido
 - verificar se a lista de usuarios retorna vazia
 - validar o bloqueio de cadastro de usuario com mesmo email
 - editar conta usuario
 - validar o bloqueio de edição de usuario com mesmo email
 - excluir conta usuario

## Cenário 3: Manipulando produtos:
 - cadastrar produto e verificar se foi inserido
 - verificar se a lista retorna vazia
 - validar o bloqueio de cadastro de produto com mesmo nome
 - validar o bloqueio de cadastro de produto sem token de autenticação
 - validar o bloqueio de cadastro de produto com usuario sem permissão de admin
 - editar produto
 - validar o bloqueio da edição de um produto com mesmo nome
 - validar o bloqueio de edição de um produto sem token de autenticação
 - excluir produto
 - validar o bloqueio de exclusão de um produto sem token de autenticação

## Cenário 4: Manipulando Carrinho
- listar carrinho e verificar se retorna vazio
- cadastrar carrinho
- validar que não é permitido ter mais de um carrinho
- concluir
- validar mensagem de retorno quando não ha carrinho para o usuário
- validar o bloqueio de conclusão da venda de um carrinho sem token de autenticação
- cancelar
- validar mensagem de retorno quando não ha carrinho para ser cancelado
- Validar o bloqueio do cancelamento da venda de um carrinho sem um token de autenticação
 
## Cenário 5: Fluxo de venda end to end
 - Faça uma busca por um produto
 - Valide o retorno da busca
 - Escolha um produto na lista
 - Adicione o carrinho
 - Valide o produto no carrinho
 - Concuir a compra
