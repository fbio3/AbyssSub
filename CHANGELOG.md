# Histórico de mudanças do AbyssSub

## v2.2
- Correção: notificações de pagamento sem nome de loja não aparecem mais com a data como nome (ex.: "12/11/2026"); agora mostram "Pagamento (Nubank)", "Pix enviado" etc.
- Correção: notificações de pagamento da fatura do cartão não contam mais como gasto
- Gastos: opção "juntar" para dizer que dois nomes são o mesmo lugar (ex.: "Pagseguro International" é a Steam), com opção de separar depois
- Gastos: dá para dizer que um gasto é de uma assinatura que você já tem, mesmo com nome diferente
- Assinaturas: campo "Outros nomes" para cobranças que aparecem com outro nome no extrato
- Assinaturas: ao renomear uma assinatura, o nome antigo continua valendo para achar as cobranças
- Assinaturas: botão para juntar duas assinaturas que são a mesma (o histórico passa para uma só)
- Gastos: nova categoria Jogos (Steam, Roblox, PlayStation, Xbox, Nintendo, Epic, Riot, Free Fire e outros)
- Gastos: lista de lojas e apps onde mais se gasta (jogos, delivery, transporte, compras, mercado, farmácia, lazer…) com ícone de cada um
- Gastos: foto personalizada para cada lugar, escolhida da galeria
- Assinaturas: mais serviços na lista, como Roblox Premium, Fortnite Crew, GeForce NOW, Twitch, Gemini, Perplexity, NordVPN, Audible e Strava

## v2.1
- Nova aba Investir (entre Renda e Histórico): simulador de investimentos com valor inicial, aporte mensal e prazo em dias, meses ou anos
- Investir: rendimento em % do CDI, prefixado, IPCA + taxa ou poupança, com IR pela tabela regressiva e IOF nos primeiros 30 dias
- Investir: mostra valor final líquido, rendimento bruto e líquido, imposto, taxa equivalente, ganho acima da inflação, quanto rende por mês no fim e em quanto tempo o dinheiro dobra
- Investir: gráfico da evolução mês a mês e comparação com poupança, 100% do CDI, LCI 90%, Tesouro IPCA+ e dinheiro parado
- Investir: taxas atuais (Selic 13,75%, CDI 13,65%, IPCA 4,6%) editáveis e simulações salvas, sincronizadas com a conta
- Renda: navegação por mês, igual ao Início, mostrando quanto sobrou em cada mês
- Renda: a sobra agora considera todos os gastos por padrão, com opção de ver só assinaturas ou só gastos
- Renda: o gráfico mostra assinaturas, gastos e sobra separados
- Início: "Onde você mais gasta" pode alternar entre Tudo, Só gastos e Só assinaturas
- Correção: as porcentagens da referência de gasto (5%, 10%, 15%...) agora ficam centralizadas
- Correção: ao trocar para uma aba à direita, a tela não some mais por meio segundo

## v2.0.1
- Correção: "Pagamento de fatura" no extrato da conta não conta mais como gasto (as compras já vêm do extrato do cartão)
- Correção: dinheiro guardado na Caixinha do Nubank ("Aplicação RDB") não conta mais como gasto
- Correção: poupança, CDB, LCI, LCA, Tesouro Direto e outros investimentos também ficam de fora dos gastos
- Correção: resgates, estornos, reembolsos e cashback não contam como gasto
- Melhoria: o filtro vale também para extratos já importados, sem precisar importar de novo
- Melhoria: o filtro vale para as notificações do banco e para as sugestões de assinatura
- Novo: opção "Não é um gasto (ignorar sempre)" no menu de cada compra (ex.: Pix para a sua própria conta)
- Novo: Configurações mostra o que não conta como gasto e quantos lançamentos foram ignorados
- Novo: botão × para voltar a contar algo que você ignorou
- Melhoria: ao importar, o aviso mostra quantos lançamentos foram ignorados e por quê
- Melhoria: nomes limpos nos lançamentos da conta ("Transferência enviada pelo Pix - MARIA SOUZA - CPF" vira "Maria Souza")
- Melhoria: "Pagamento de boleto efetuado - CONDOMÍNIO" vira "Condomínio"

## v2.0
- Novo: nova aba Início com todos os seus gastos do mês, não só as assinaturas
- Mudança: a antiga aba Início agora se chama Assinaturas
- Novo: barra de baixo com 4 abas: Início, Assinaturas, Renda e Histórico
- Novo: as compras entram sozinhas a partir dos extratos importados e das notificações do banco
- Novo: botão + na aba Início para adicionar uma compra à mão
- Novo: categoria automática para cada compra (mercado, restaurantes, transporte, saúde, compras, contas, Pix e mais)
- Novo: nomes limpos para lojas conhecidas (ex.: "UBER *TRIP" vira "Uber")
- Novo: navegar entre os meses no topo da aba Início
- Novo: comparação com o mês passado e média de gasto por dia
- Novo: gráfico de gastos por categoria e dos últimos 6 meses
- Novo: destaques do mês: maior gasto, onde mais gastou, dia da semana que mais gasta, categoria principal e dias sem gastar
- Novo: ranking "Onde você mais gasta"
- Novo: lista de gastos por dia, com busca e filtro por categoria
- Novo: editar nome e categoria de qualquer compra, ou remover com opção de desfazer
- Novo: "É uma assinatura: cadastrar" transforma uma compra em assinatura
- Novo: Histórico agora mostra o total gasto com tudo, separando assinaturas e compras
- Novo: cobranças de assinaturas que aparecem no extrato antes de você usar o app passam a contar no Histórico
- Novo: estatísticas novas no Histórico: total em assinaturas, total em compras, maior compra e cobranças achadas no extrato
- Novo: filtro do extrato do Histórico por Tudo, Assinaturas ou Compras
- Novo: calendário do Histórico mostra as compras também
- Novo: tocar num lançamento do Histórico abre opções: marcar como pago/não pago, editar ou remover do histórico
- Novo: remover um pagamento de assinatura do histórico, com opção de desfazer
- Novo: na aba Renda, a referência de gasto com assinaturas pode ser alterada (5%, 10%, 15%, 20%, 30% ou outro valor)
- Novo: na aba Renda, "Compras este mês" e "Sobra real do mês" (renda menos assinaturas menos compras)
- Melhoria: compras adicionadas à mão, edições e a referência da renda também são salvas na conta
- Melhoria: a mesma cobrança vista no extrato e na notificação do banco conta uma vez só
- Melhoria: um pagamento de assinatura e a mesma cobrança no extrato contam uma vez só
- Correção: depois de importar um extrato, as telas não se atualizavam até trocar de aba
- Correção: um pagamento removido ou marcado como não pago não volta mais pelo extrato
- Correção: tocar num pagamento do histórico não troca mais o status sem querer (agora abre um menu)
- Correção: os totais de cada mês no extrato do Histórico agora somam tudo o que foi pago
- Melhoria: limpeza de código antigo que não era mais usado

## v1.9
- Novo: importar vários extratos de uma vez (por exemplo, um arquivo para cada mês)
- Novo: Configurações mostra os meses de extrato já salvos, com os meses faltando em tracejado
- Melhoria: extratos enviados em dias diferentes ficam guardados e são analisados juntos
- Melhoria: cobranças repetidas são encontradas mesmo com um mês faltando no meio
- Melhoria: aviso explicando para enviar os outros meses quando só um foi importado
- Novo: ícones oficiais dos serviços do catálogo, carregados do site de cada serviço
- Novo: foto em cada assinatura, que aparece na lista, no histórico e nas sugestões
- Novo: "Mudar foto" no cadastro com três opções: galeria, ícone do serviço ou letra colorida
- Novo: foto da galeria recortada e reduzida automaticamente para não pesar no app
- Melhoria: ao digitar o nome de um serviço conhecido, a categoria e o tipo de cobrança são preenchidos sozinhos
- Melhoria: sem internet, os ícones voltam para a letra colorida

## v1.8
- Novo: catálogo com mais de 50 serviços populares (streaming, música, apps, jogos, academias, celular e mais)
- Novo: botão "Escolher da lista de serviços" no cadastro preenche nome, categoria e tipo de cobrança
- Novo: busca no catálogo por nome ou categoria
- Novo: importar extrato do banco em OFX ou CSV para encontrar as assinaturas que você já paga
- Novo: o extrato é lido sozinho, descobrindo as colunas de data, valor e descrição
- Novo: reconhece serviços conhecidos mesmo com nomes de extrato como "PG *NETFLIX.COM" ou "GOOGLE YOUTUBEPREMIUM"
- Novo: encontra cobranças que se repetem todo mês com valor parecido, mesmo de lugares que não estão no catálogo
- Novo: leitura das notificações de compra dos apps de banco (opcional, com sua permissão)
- Novo: aviso "assinaturas encontradas" no Início com o botão "Revisar"
- Novo: cada sugestão mostra valor, dia da cobrança e onde foi vista (extrato ou notificação do banco)
- Novo: "Adicionar" abre o cadastro já preenchido para você conferir antes de salvar
- Novo: "Ignorar" esconde a sugestão, com opção de desfazer
- Novo: seção "Detectar assinaturas" em Configurações
- Novo: ajuda para liberar a "configuração restrita" do Android 13 ou mais novo
- Novo: botão para apagar todos os dados usados na detecção
- Privacidade: extrato e notificações são analisados só no celular e nunca são enviados
- Melhoria: Pix recebido, estornos e compras avulsas (padaria, iFood, Uber) não viram sugestão

## v1.7
- Novo: o app verifica sozinho se existe uma versão mais nova publicada no GitHub
- Novo: aviso de atualização com a versão atual, a nova, o que mudou e o tamanho do download
- Novo: botão "Atualizar agora" baixa o APK dentro do app e abre o instalador do Android
- Novo: barra de progresso durante o download da atualização
- Novo: pede a permissão "Instalar apps desconhecidos" só na primeira vez e continua sozinho ao voltar
- Novo: "Agora não" esconde o aviso daquela versão, que só volta quando sair uma mais nova
- Novo: em Configurações, status "Você está na versão mais recente" ou "Nova versão disponível"
- Novo: botões "Atualizar" e "Verificar agora" em Configurações
- Melhoria: a versão mostrada em Configurações agora vem direto do app instalado
- Melhoria: a verificação acontece no máximo a cada 6 horas para não gastar internet

## v1.6.1
- Melhoria: botões "Google" e "E-mail" lado a lado, do mesmo tamanho, quando você está sem conta
- Correção: o APK agora é gerado de verdade no GitHub Actions
- Melhoria: o APK publicado no GitHub passou a se chamar AbyssSub.apk
- Melhoria: o build do GitHub fica vermelho se o APK não for gerado (antes ficava verde mesmo sem APK)
- Melhoria: ao publicar uma Release pelo site, o APK é anexado nela automaticamente

## v1.6
- Novo: login com e-mail e senha
- Novo: criar conta com e-mail, com confirmação da senha
- Novo: "Esqueci minha senha" envia um link para criar uma nova senha
- Novo: mensagens de erro de login em português (senha errada, e-mail já usado, senha fraca etc.)
- Novo: lembretes de cobrança por notificação
- Novo: escolha de quando avisar: no dia, 1, 2, 3 ou 7 dias antes (padrão: 1 dia)
- Novo: escolha do horário do aviso (padrão: 9:00)
- Novo: Configurações mostra qual será o próximo aviso
- Novo: os lembretes funcionam com o app fechado e voltam depois de reiniciar o celular
- Novo: bloqueio do app com senha própria (PIN de 4 a 6 números)
- Novo: bloqueio do app com o bloqueio do celular (digital, rosto, PIN ou padrão)
- Novo: bloqueio do app só com a digital
- Novo: o bloqueio aparece ao abrir e ao voltar depois de 30 segundos fora do app
- Novo: a tela fica coberta na lista de apps recentes quando o bloqueio está ligado
- Novo: espera de 30 segundos depois de 5 senhas erradas
- Novo: "Esqueci a senha" remove o PIN confirmando com o bloqueio do celular

## v1.5
- Mudança: novo nome interno do app (com.abysssub.app), sem nomes pessoais no código
- Novo: "Copiar backup" em Configurações copia todos os dados como texto
- Novo: "Restaurar" cola um backup, com opção de juntar ou substituir os dados
- Novo: o backup é conferido antes de restaurar e mostra quantas assinaturas, rendas e pagamentos tem
- Melhoria: os dados salvos passaram para um novo nome interno, com migração automática

## v1.4
- Novo: login com Google
- Novo: dados salvos na nuvem (Firebase) para quem entra com uma conta
- Novo: os dados voltam sozinhos ao entrar em outro celular ou reinstalar o app
- Novo: mudanças feitas em um aparelho aparecem nos outros em segundos
- Novo: tela de boas-vindas na primeira abertura com "Entrar com Google" ou "Continuar sem conta"
- Novo: seção Conta em Configurações com foto, e-mail, status da sincronização e botão "Sair"
- Novo: botão "Sincronizar agora"
- Novo: ao entrar numa conta que já tem dados, escolha entre juntar ou usar só os da conta
- Novo: funciona sem internet e envia tudo quando a conexão voltar
- Segurança: cada pessoa só consegue ler e gravar os próprios dados

## v1.3
- Novo: aba Renda entre Início e Histórico
- Novo: cadastro de várias fontes de renda (salário, freela etc.)
- Novo: renda por mês, quinzena, semana ou ano, tudo convertido para o valor mensal
- Novo: "Sobra por mês" (renda menos assinaturas) no topo da aba
- Novo: gráfico de quanto da renda vai para assinaturas, com etiqueta Saudável, Atenção ou Alto
- Novo: sobra por dia e sobra em 12 meses, já contando o fim das promoções
- Novo: horas de trabalho por mês e dias de trabalho no ano gastos com assinaturas
- Novo: quanto já foi pago no mês e quanto sobraria cancelando a assinatura mais cara
- Novo: gráfico da sobra nos próximos 12 meses, com balão ao tocar
- Novo: quanto cada assinatura custa em tempo de trabalho e em % da renda
- Novo: horas trabalhadas por mês ajustáveis em Configurações (padrão: 220 h)
- Novo: o botão + da aba Renda adiciona uma renda

## v1.2
- Novo: aba Histórico com tudo o que já foi pago desde que você começou a usar o app
- Novo: barra de abas na parte de baixo da tela
- Novo: escolha de período: desde o início, este mês, 3 meses, 6 meses, este ano ou personalizado
- Novo: calendário com pagos, previstos, a confirmar e não pagos
- Novo: toque num dia do calendário para ver os pagamentos dele
- Novo: resumo com maior gasto, mês mais caro, economia com promoções e pagamentos não feitos
- Novo: gráfico do que foi pago por mês
- Novo: ranking do quanto foi pago em cada assinatura
- Novo: extrato com todos os pagamentos, separados por mês
- Novo: registro de pagamentos automático ou manual (com botões "Paguei" e "Não")
- Novo: toque num pagamento do extrato para marcar ou desmarcar, com opção de desfazer
- Novo: balão com o valor e as maiores assinaturas ao tocar num mês do gráfico
- Mudança: próximas cobranças mostram o valor por mês (ou por semana nas semanais)
- Novo: planos trimestrais, semestrais e anuais mostram a parte do ciclo (ex.: 2/12)

## v1.1
- Mudança: o app passou a se chamar AbyssSub, com ícone novo
- Novo: modo escuro e claro automático, seguindo o tema do celular
- Novo: visual novo em azul-marinho e dourado, combinando com o ícone
- Novo: Configurações com a escolha de tema: Automático, Claro ou Escuro
- Melhoria: selo de promoção em dourado, mais bonito no modo escuro
- Correção: o topo azul não fica mais atrás das "Próximas cobranças"
- Novo: animações de entrada, números que sobem contando e gráficos que se desenham
- Novo: painéis que deslizam, botões que reagem ao toque e transição suave entre temas
- Novo: assinaturas novas aparecem com brilho e as excluídas deslizam para fora

## v1.0
- Novo: cadastro de assinaturas com nome, valor, categoria e data da próxima cobrança
- Novo: total gasto por mês, por ano e nos próximos 30 dias
- Novo: cobranças semanais, mensais, trimestrais, semestrais e anuais
- Novo: promoções que duram uma quantidade de meses e depois voltam ao preço normal
- Novo: gráfico por categoria
- Novo: lista das próximas cobranças
- Novo: projeção dos próximos 12 meses mostrando quando cada promoção acaba
- Novo: filtro da lista por categoria
- Novo: editar e excluir assinaturas
- Novo: dados salvos no próprio celular
