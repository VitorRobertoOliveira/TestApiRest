# TestApiRest
Automação de teste de API em Java com RestAssured utilizando o servidor Rest: serverest.dev - O ServeRest é um servidor REST que simula uma loja virtual com intuito de servir de material de estudos de testes de API.

# Cenários de teste API: https://serverest.dev

# Cenário 1: Autenticação Login:
 - credencial incorreta
 - credencial correta
 - Tentar acesso direto a API

# Cenário 2: Autenticação Conta Usuario:
 - cadastrar usuario e verificar se ele foi inserido
 - verificar se a lista de usuarios retorna vazia
 - validar o bloqueio de cadastro de usuario com mesmo email
 - editar conta usuario
 - excluir conta usuario

# Cenário 3: Manipulando produtos:
 - cadastrar produto e verificar se foi inserido
 - verificar se a lista retorna vazia
 - editar produto
 - excluir produto

# Cenário 4: Manipulando Carrinho
 - listar e verificar se retorna vazio
 - cadastrar
 - concluir
 - cancelar
 
# Cenário 5: Fluxo de venda end to end
 - Faça uma busca por um produto
 - Valide o retorno da busca
 - Escolha um produto na lista
 - Adicione o carrinho
 - Valide o produto no carrinho
