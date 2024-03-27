package br.com.josiasmartins.picpaydesafiobackend.authorization;

public record Authorization(String message) {

    public boolean isAuthorization() {
        return message.equals("Authorizado");
    }

}
