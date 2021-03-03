# TestApiRest
Automação de teste de API em Java com RestAssured utilizando o servidor Rest: serverest.dev - O ServeRest é um servidor REST que simula uma loja virtual com intuito de servir de material de estudos de testes de API.

# Cenários de teste API: https://serverest.dev

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
 - listar e verificar se retorna vazio
 - cadastrar
 - concluir
 - cancelar
 
## Cenário 5: Fluxo de venda end to end
 - Faça uma busca por um produto
 - Valide o retorno da busca
 - Escolha um produto na lista
 - Adicione o carrinho
 - Valide o produto no carrinho
