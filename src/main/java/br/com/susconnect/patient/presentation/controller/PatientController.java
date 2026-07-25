package br.com.susconnect.patient.presentation.controller;

import br.com.susconnect.common.response.SuccessResponse;
import br.com.susconnect.patient.application.dto.CreatePatientRequest;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.dto.UpdatePatientRequest;
import br.com.susconnect.patient.application.usecase.CreatePatientUseCase;
import br.com.susconnect.patient.application.usecase.DeletePatientUseCase;
import br.com.susconnect.patient.application.usecase.FindAllPatientsUseCase;
import br.com.susconnect.patient.application.usecase.FindPatientByIdUseCase;
import br.com.susconnect.patient.application.usecase.UpdatePatientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gerenciamento de pacientes")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final FindPatientByIdUseCase findPatientByIdUseCase;
    private final FindAllPatientsUseCase findAllPatientsUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final DeletePatientUseCase deletePatientUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar paciente")
    public ResponseEntity<SuccessResponse<PatientResponse>> create(
            @Valid @RequestBody CreatePatientRequest request) {

        PatientResponse response = createPatientUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(
                        "Paciente cadastrado com sucesso.",
                        response));
    }

    @GetMapping
    @Operation(summary = "Listar pacientes")
    public ResponseEntity<SuccessResponse<List<PatientResponse>>> findAll() {

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Pacientes encontrados.",
                        findAllPatientsUseCase.execute()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public ResponseEntity<SuccessResponse<PatientResponse>> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Paciente encontrado.",
                        findPatientByIdUseCase.execute(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar paciente")
    public ResponseEntity<SuccessResponse<PatientResponse>> update(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdatePatientRequest request) {

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Paciente atualizado com sucesso.",
                        updatePatientUseCase.execute(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir paciente")
    public ResponseEntity<SuccessResponse<Void>> delete(
            @PathVariable UUID id) {

        deletePatientUseCase.execute(id);

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Paciente excluído com sucesso.",
                        null));
    }
}