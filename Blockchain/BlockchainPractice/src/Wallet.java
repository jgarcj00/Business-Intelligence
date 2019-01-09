import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Wallet {
    PrivateKey privateKey; //dos tipos de claves la publica y la privada
    PublicKey publicKey;
    private PublicKey minerKey;

    private HashMap<String,TransactionOutput> UTXOs = new HashMap<>(); //transacciones no realizadas

    Wallet(PublicKey miner){
        this.minerKey = miner;
        generateKeyPair();
    }

    Wallet() {
    }

    void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);   //256b
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //si me pertenece el resultado
                UTXOs.put(UTXO.id,UTXO); //lo añade a la lista de transacciones no realizadas
                total += UTXO.value ;
            }
        }
        return total;
    }
    //genera y devuelve una nueva transaccion
    Transaction sendFunds(PublicKey _recipient, float value) {
        if(getBalance() < (value+(value/100))) { 
            System.out.println("#Not Enough funds to pay Transaction Fee. Transaction Discarded.");
            return null;
        }
        if(getBalance() < value) { 
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, minerKey , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}

