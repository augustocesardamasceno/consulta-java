package br.com.accenture.consulta.domain.exception;

public class DuplicatedTupleException extends RuntimeException {
    public DuplicatedTupleException(String message) {
        super(message);
    }

}
