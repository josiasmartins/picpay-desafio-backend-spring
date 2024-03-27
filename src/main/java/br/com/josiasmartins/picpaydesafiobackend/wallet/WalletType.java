package br.com.josiasmartins.picpaydesafiobackend.wallet;

public enum WalletType {

    COMUM(1), LOJISTA(2);

    private int value;

    private WalletType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
