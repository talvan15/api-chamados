package br.edu.ifrn.chamados.api;

import org.springframework.data.domain.*;

public final class PageSupport {
    private PageSupport() {
    }

    public static Pageable cap(Pageable p) {
        return PageRequest.of(Math.max(0, p.getPageNumber()), Math.min(Math.max(1, p.getPageSize()), 50), p.getSort());
    }
}
