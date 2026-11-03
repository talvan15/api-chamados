package br.edu.ifrn.chamados.api;

import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Function;

public record PageResponse<T>(List<T> itens, int pagina, int tamanho, long totalElementos, int totalPaginas) {
    public static <S, T> PageResponse<T> from(Page<S> page, Function<S, T> mapper) {
        return new PageResponse<>(page.getContent().stream().map(mapper).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
