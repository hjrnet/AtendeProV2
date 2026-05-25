package br.com.atendepro.modules.documento.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface CarregarMarcaDaguaAcademicaPlanoPort {

    Optional<String> carregarMarcaDaguaAcademica(UUID empresaId);
}
