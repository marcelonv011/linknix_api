package br.com.linknix.exception;

public class IntegracaoException extends RuntimeException {

    public IntegracaoException(String mensagem) {
        super(mensagem);
    }

    public IntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
