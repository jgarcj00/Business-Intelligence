import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciepient; //el nuevo dueño de las monedas
    public float value; //valor de las monedas
    public String parentTransactionId; 

    //Constructor
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        // receptor=clave publica
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        // id = hash
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    //comprueba si las monedas te pertenecen
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }

}