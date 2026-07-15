package br.com.linknix.exception;

import br.com.linknix.dto.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> campos = new LinkedHashMap<>();

        for (FieldError erro : exception.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                "Um ou mais campos estão inválidos.",
                request,
                campos
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponseDTO> tratarViolacaoDeRestricao(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> campos = new LinkedHashMap<>();

        exception.getConstraintViolations().forEach(violacao ->
                campos.putIfAbsent(
                        violacao.getPropertyPath().toString(),
                        violacao.getMessage()
                )
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                "Um ou mais parâmetros estão inválidos.",
                request,
                campos
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> tratarCorpoIlegivel(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                "O corpo da requisição está ausente ou possui formato JSON inválido.",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                request,
                Map.of()
        );
    }

    @ExceptionHandler({ConflitoException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErroResponseDTO> tratarConflito(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        String mensagem = exception instanceof ConflitoException
                ? exception.getMessage()
                : "A operação viola uma restrição de integridade dos dados.";

        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito",
                mensagem,
                request,
                Map.of()
        );
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDTO> tratarRegraNegocio(
            RegraNegocioException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Regra de negócio violada",
                exception.getMessage(),
                request,
                Map.of()
        );
    }

    @ExceptionHandler(IntegracaoException.class)
    public ResponseEntity<ErroResponseDTO> tratarIntegracao(
            IntegracaoException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_GATEWAY,
                "Falha de integração",
                exception.getMessage(),
                request,
                Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> tratarErroInesperado(
            Exception exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro interno inesperado.",
                request,
                Map.of()
        );
    }

    private ResponseEntity<ErroResponseDTO> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            HttpServletRequest request,
            Map<String, String> campos
    ) {
        ErroResponseDTO resposta = ErroResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro(erro)
                .mensagem(mensagem)
                .caminho(request.getRequestURI())
                .campos(campos)
                .build();

        return ResponseEntity.status(status).body(resposta);
    }
}
