# Configurar o login com Google (Firebase)

Leva uns 15 minutos e só precisa ser feito uma vez. Enquanto não estiver pronto,
o app funciona normalmente no modo "sem conta".

## 1. Criar o projeto

1. Acesse **https://console.firebase.google.com** com a sua conta Google.
2. Clique em **Criar um projeto** (ou *Create a Firebase project*).
3. Nome: `AbyssSub` → continuar.
4. Google Analytics: pode **desativar** (o app não usa) → **Criar projeto**.

## 2. Ligar o login com Google

1. No menu da esquerda: **Criação > Authentication** (*Build > Authentication*) → **Vamos começar**.
2. Aba **Método de login** (*Sign-in method*) → **Google** → ative a chave **Ativar**.
3. Em **E-mail de suporte do projeto**, escolha o seu e-mail → **Salvar**.

## 3. Criar o banco de dados

1. Menu da esquerda: **Criação > Firestore Database** → **Criar banco de dados**.
2. Local: **southamerica-east1 (São Paulo)** → próximo.
3. Escolha **Iniciar no modo de produção** → **Criar**.
4. Abra a aba **Regras** (*Rules*), apague tudo e cole o conteúdo do arquivo
   `firestore.rules` (está na pasta do projeto) → **Publicar**.

   Essas regras garantem que cada pessoa só consegue ler e gravar os próprios dados.

## 4. Registrar o app Android

1. Dê dois cliques em **`firebase-sha1.bat`** (na pasta do projeto) e copie o código
   que aparece depois de `SHA1:` (algo como `AB:CD:12:...`).

   **Se o script não mostrar o SHA1**, pegue pelo Android Studio: abra o painel **Gradle**
   (ícone de elefante na barra da direita) → clique no ícone **Execute Gradle Task**
   (um elefante com seta, no topo do painel) → digite `signingReport` → Enter.
   Na aba **Run** embaixo aparece a variante `debug` com a linha `SHA1:`.
2. No Firebase, menu da esquerda: **Configurações > Geral** → role até a seção
   **Seus apps** → clique no ícone do **Android** (ou em *Adicionar app* > Android).
   Também dá pelo botão **+ Adicionar app** no topo da *Visão geral do projeto*.
3. Preencha:
   - **Nome do pacote:** `com.abysssub.app`
   - **Apelido:** `AbyssSub`
   - **Certificado de assinatura SHA-1:** cole o código do passo 1
4. **Registrar app** → **Baixar google-services.json**.
5. Coloque o arquivo dentro da pasta **`app`** do projeto (ao lado do `build.gradle` de lá):
   `AbyssSub\app\google-services.json` (a pasta do projeto)
6. Os próximos passos do site (adicionar SDK etc.) podem ser pulados — o projeto já está pronto.

## 5. Gerar e testar

1. Android Studio → **File > Sync Project with Gradle Files**.
2. **Build > Generate App Bundles or APKs > Generate APKs**.
3. Instale o `APK\AbyssSub-debug.apk` por cima do app atual (os dados continuam).
4. Abra o app → **Entrar com Google** (ou em Configurações > Conta).

## 6. GitHub (para o APK de lá também ter login)

1. Envie para o repositório os arquivos novos, **incluindo `app/google-services.json`**
   (ele identifica o projeto, não é uma senha — a segurança vem das regras do passo 3).
2. Dê dois cliques em **`chave-github.bat`**: ele copia a chave de assinatura deste PC.
3. No GitHub: **Settings > Secrets and variables > Actions > New repository secret**
   - Nome: `ABYSS_DEBUG_KEYSTORE`
   - Valor: cole (Ctrl+V) → **Add secret**

Assim o APK gerado pelo GitHub tem a mesma assinatura do seu PC: o login funciona nele e
dá para instalar um por cima do outro sem perder os dados.

## Se algo der errado

| O que aparece | O que fazer |
|---|---|
| "Não foi possível entrar com o Google" | Confira o SHA-1 no passo 4. Se adicionou o SHA-1 ou ligou o Google **depois** de baixar o `google-services.json`, baixe o arquivo de novo e substitua. |
| Botão diz "Login com Google ainda não configurado" | O `google-services.json` não está em `app\` ou o Gradle não foi sincronizado. |
| Configurações > Conta mostra "Sem permissão no banco" | As regras do passo 3.4 não foram publicadas. |
| "Salvando… (envia quando tiver internet)" | Normal sem internet: o app guarda tudo e envia quando a conexão voltar. |

## Bom saber

- O plano gratuito do Firebase (Spark) é mais do que suficiente para uso pessoal.
- Se dois celulares forem editados **ao mesmo tempo sem internet**, ao reconectar vale a
  última versão enviada. Com internet, as mudanças chegam nos outros aparelhos em segundos.
