package br.com.atendepro.modules.adminsaas.adapter.out.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.adminsaas.application.port.out.RepopularDadosDemoPort;
import br.com.atendepro.modules.demo.adapter.in.bootstrap.DadosDemoLocalRunner;

@Component
@Profile("local")
public class DadosDemoLocalResetAdapter implements RepopularDadosDemoPort {

    private final DadosDemoLocalRunner dadosDemoLocalRunner;

    public DadosDemoLocalResetAdapter(DadosDemoLocalRunner dadosDemoLocalRunner) {
        this.dadosDemoLocalRunner = dadosDemoLocalRunner;
    }

    @Override
    public void repopularDadosDemo() {
        dadosDemoLocalRunner.popularDadosDemo();
    }
}
