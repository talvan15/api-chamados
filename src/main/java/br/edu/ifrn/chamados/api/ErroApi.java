package br.edu.ifrn.chamados.api;

import java.time.Instant;

public record ErroApi(Instant timestamp, int status, String mensagem, String caminho) {
}
