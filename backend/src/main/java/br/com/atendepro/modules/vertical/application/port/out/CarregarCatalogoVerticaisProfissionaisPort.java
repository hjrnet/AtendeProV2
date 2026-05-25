package br.com.atendepro.modules.vertical.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;
import br.com.atendepro.modules.vertical.domain.model.VerticalProfissional;

public interface CarregarCatalogoVerticaisProfissionaisPort {

    List<VerticalProfissional> listarVerticais();

    Optional<VerticalProfissional> carregarVertical(CodigoVerticalProfissional codigo);
}
