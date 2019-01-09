class TransactionInput {
    String transactionOutputId; 
    TransactionOutput UTXO; //Contiene el output de la transaccion "No gastados"
    
    TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}