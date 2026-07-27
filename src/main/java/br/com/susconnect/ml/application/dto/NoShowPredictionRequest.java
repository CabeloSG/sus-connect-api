package br.com.susconnect.ml.application.dto;

/**
 * Representa os dados enviados pelo SUS Connect
 * ao serviço de Machine Learning para cálculo
 * do risco de ausência de um paciente.
 *
 * Os nomes dos atributos correspondem às features
 * utilizadas durante o treinamento do modelo.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record NoShowPredictionRequest(

        int patientAge,

        int dayOfWeek,

        int hourOfDay,

        int daysInAdvance,

        String appointmentType,

        String medicalSpecialty,

        long historicalAppointments,

        long historicalNoShows,

        long historicalCompleted,

        double historicalNoShowRate,

        String confirmationStatus

) {
}