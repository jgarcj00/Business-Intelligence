import java.security.*;
import java.util.ArrayList;

class Transaction {

    String transactionId; // coincide con el hash de la transaccion
    PublicKey sender; // clave publica del emisor
    PublicKey reciepient; // clave publica del receptor
    PublicKey miner;
    private float value;
    float transactionFee;
    float transactionValue;
    private byte[] signature; // para prevenir que alguien mas realice operaciones en nuestra wallet

    ArrayList<TransactionInput> inputs;
    ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // cuenta de cuantas transacciones se han generado

    // Constructor:
    Transaction(PublicKey from, PublicKey to, PublicKey miner, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.miner = miner;
        this.transactionFee = (value/100);
        this.transactionValue = value;
        this.value = (value + transactionFee);
        this.inputs = inputs;
    }

    // Calcula el hash de la transaccion (se usara como su id)
    private String calulateHash() {
        sequence++; //incrementa para evitar dos transacciones con el mismo hashid
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }

    //firma todos los datos que no queremos que se manipulen
    void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }
    //verifica que los datos firmados no han sido manipulados
    boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        return !StringUtil.verifyECDSASig(sender, data, signature);
    }

    //true si la transaccion se puede hacer
    boolean processTransaction() {

        if(verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        
        for(TransactionInput i : inputs) {
            i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }

        //comprueba que la transaccion es valida
        if(getInputsValue() < Main.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //genera los outputs de la transaccion
        float leftOver = getInputsValue() - value; 
        transactionId = calulateHash();
        outputs.add(new TransactionOutput( this.reciepient, transactionValue,transactionId)); //envia un valor a reciepient
        outputs.add(new TransactionOutput( this.miner, transactionFee, transactionId));
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //envia leftOver de nuevo al emisor

        //añade outputs a la lista Unspent
        for(TransactionOutput o : outputs) {
            Main.UTXOs.put(o.id , o);
        }

        //elimina inputs de la transaccion de la lista UTXO cuando son gastados
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //si la transaccion no puede ser realizada la salta
            Main.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    //devuelve la suma de inputs (UTXIs)
    float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; 
            total += i.UTXO.value;
        }
        return total;
    }

   
    float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}