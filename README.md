### What is this repository for? ###

* SDK for Solana on-chain and Proteus API interaction.

### How to import in your project (Groovy)###

1. Add the following to `settings.gradle` file:
   ``` 
   ...
   repositories {
         ...
        maven { url 'https://jitpack.io' }
    }
   
2. Add dependency to `build.gradle`:
   ```
   dependencies {
    ...
       implementation 'fan.vault.sdk:android-sdk:versionNumber'
   }

### How to use ###

### Claiming NFTs from social wallet to app wallet

1. Request OneTimePassword for a user (method ProteusAPIWorker.requestOneTimePassword). ProteusAPI
   swagger: https://v0uusuz5j4.execute-api.us-east-2.amazonaws.com/v0/docs/#/mint/Otp
2. Get social wallet address for user email address (method ProteusAPIWorker.getSocialWalletAddress)
   . Proteus API
   swagger: https://v0uusuz5j4.execute-api.us-east-2.amazonaws.com/v0/docs/#/profiles/GetSocialWallet
3. List NFTs owned by social wallet (method SolanaWorker.listNFTsWithMetadata)
4. For each NFT claim call ClaimNFTWorker.claim
    1. It requests transfer transaction from the ProteusAPI
    2. Call Solana.signAndSendTransaction