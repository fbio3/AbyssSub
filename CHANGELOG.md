# Histórico de mudanças do AbyssSub

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
