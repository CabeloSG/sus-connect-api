package br.com.susconnect.ml.application.dto;

/**
 * Representa a resposta retornada pelo serviço
 * de Machine Learning após o cálculo do risco
 * de ausência do paciente.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record NoShowPredictionResponse(

        double noShowProbability,

        String riskLevel

) {
}