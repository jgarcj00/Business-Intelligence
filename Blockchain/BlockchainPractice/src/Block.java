import java.util.ArrayList;
import java.util.Date;

class Block {

    String hash; //este string guarda la firma digital	
    String previousHash;
    private String merkleRoot;
    ArrayList<Transaction> transactions = new ArrayList<>(); //los datos van a ser un mensaje
    private long timeStamp; 
    private int nonce;

   
    Block() {
        if (Main.lastBlockIndex() == -1) {
            this.previousHash = "0";
        } else {
            this.previousHash = Main.blockchain.get(Main.lastBlockIndex()).hash;
        }
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); 
    }

    //Calcula el muevo string hash basado en contenido de las partes del bloque que no queremos que sean manipuladas
    String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    //incrementa el valor de nonce hasta que llegamos al valor de hash
    void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

 
    void addTransaction(Transaction transaction) {
        if(transaction == null) return;
        if((!previousHash.equals("0"))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
    }
}