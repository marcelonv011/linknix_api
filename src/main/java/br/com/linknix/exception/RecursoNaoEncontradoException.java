package br.com.linknix.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Object identificador) {
        super(recurso + " não encontrado(a) para o identificador: " + identificador);
    }
}
