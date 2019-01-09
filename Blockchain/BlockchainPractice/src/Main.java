import java.security.*; 
import java.util.ArrayList;
import java.util.HashMap;
import org.bouncycastle.*;


public class Main {

    static ArrayList<Block> blockchain = new ArrayList<>();
    static HashMap<String,TransactionOutput> UTXOs = new HashMap<>(); //transacciones no realizadas

    private static int difficulty = 3;
    static float minimumTransaction = 0.1f;
    private static Transaction genesisTransaction;

    public static void main(String[] args) {
        //añade los bloques al arrayList de la blockchain
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        //creamos wallets
        Wallet coinbase = new MinerWallet();
        Wallet walletA = new Wallet(coinbase.publicKey);
        Wallet walletB = new Wallet(coinbase.publicKey);

        //crea la transaccion genesis, que envia 100 coins a walletA
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, coinbase.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //firma la transaccion genesis
        genesisTransaction.transactionId = "0"; //establece el id de la transaccion
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.transactionValue, genesisTransaction.transactionId)); //añade el output de la transaccion
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.miner, genesisTransaction.transactionFee, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
        UTXOs.put(genesisTransaction.outputs.get(1).id, genesisTransaction.outputs.get(1));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block();
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testear
        Block block1 = new Block();
        System.out.println("WalletMiner's balance is: " + coinbase.getBalance());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletMiner's balance is: " + coinbase.getBalance());

        Block block2 = new Block();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletMiner's balance is: " + coinbase.getBalance());

        Block block3 = new Block();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletMiner's balance is: " + coinbase.getBalance());

        isChainValid();

    }
    //hace un bucle entre los bloques y compara las hash. Comprueba que
    //la variable hash es igual a la calculada y que el primer bloque hash
    //es igual a la  variable previusHash
    private static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<>(); //lista temporal de transacciones no gastadas en un estado de bloque dado
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //bucle de blockchains para comprobar hashes
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compara el hash registrado y el calculado aqui
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //comprueba si el hash esta resuelto
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.miner) {
                    System.out.println("#Transaction(" + t + ") output Fee is not the miner");
                    return false;
                }
                if( currentTransaction.outputs.get(2).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    private static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    static int lastBlockIndex() {
        return (blockchain.size()-1);
    }
}