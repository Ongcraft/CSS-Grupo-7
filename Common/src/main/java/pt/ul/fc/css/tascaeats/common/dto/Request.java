package pt.ul.fc.css.tascaeats.common.dto;

public record Request<E>(
        String callbackUrl,
        String correlationId,
        E entityDto) {

    public Request() {
        this(null, null, null);
    }

    public Request(String callbackUrl) {
        this(callbackUrl, null, null);
    }
}
