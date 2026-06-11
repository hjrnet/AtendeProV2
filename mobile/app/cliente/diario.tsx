import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { CapaPagina, Cartao, CabecalhoSecao, EstadoVazio, ItemLista, temaAtendePro } from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import {
  consultarSegurancaBeauty,
  consultarListaComprasNutri,
  consultarPlanoPublicadoNutri,
  criarRegistroDiarioNutri,
  listarDiarioAlimentarNutri,
  listarLembretesNutri,
  listarMetasNutri,
  listarProtocolosBeauty,
  resolverPrimeiroClienteBeauty,
  resolverPrimeiroPacienteNutri,
  type ListaComprasNutriApi,
  type PlanoAlimentarNutriApi,
  type ProtocoloBeautyApi,
  type RegistroDiarioNutriApi
} from "@/lib/api/client";

export default function DiarioClienteMobile() {
  const [textoNovoRegistro, setTextoNovoRegistro] = useState("");
  const [registros, setRegistros] = useState<RegistroDiarioNutriApi[]>([]);
  const [plano, setPlano] = useState<PlanoAlimentarNutriApi | null>(null);
  const [listaCompras, setListaCompras] = useState<ListaComprasNutriApi | null>(null);
  const [protocolosBeauty, setProtocolosBeauty] = useState<ProtocoloBeautyApi[]>([]);
  const [alertasBeauty, setAlertasBeauty] = useState(0);
  const [metas, setMetas] = useState(0);
  const [lembretes, setLembretes] = useState(0);
  const [empresaId, setEmpresaId] = useState<string | null>(null);
  const [pacienteId, setPacienteId] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const carregar = async () => {
    setCarregando(true);
    setErro("");

    try {
      const sessao = await carregarSessaoAutenticada();
      const contextoEmpresa = sessao?.usuario.empresaId ?? null;
      setEmpresaId(contextoEmpresa);

      const paciente = await resolverPrimeiroPacienteNutri(contextoEmpresa);
      setPacienteId(paciente?.id ?? null);
      if (!paciente) {
        setRegistros([]);
        setPlano(null);
        setListaCompras(null);
        return;
      }

      const [diarioResposta, metasResposta, lembretesResposta] = await Promise.all([
        listarDiarioAlimentarNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id }),
        listarMetasNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id }),
        listarLembretesNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id })
      ]);

      setRegistros(diarioResposta.itens ?? []);
      setMetas(metasResposta.itens.length);
      setLembretes(lembretesResposta.itens.length);

      try {
        setPlano(await consultarPlanoPublicadoNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id }));
        setListaCompras(await consultarListaComprasNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id }));
      } catch {
        setPlano(null);
        setListaCompras(null);
      }

      const clienteBeauty = await resolverPrimeiroClienteBeauty(contextoEmpresa);
      if (!clienteBeauty) {
        setProtocolosBeauty([]);
        setAlertasBeauty(0);
        return;
      }

      try {
        const [protocolosResposta, segurancaResposta] = await Promise.all([
          listarProtocolosBeauty({ empresaId: contextoEmpresa, clienteId: clienteBeauty.id }),
          consultarSegurancaBeauty({ empresaId: contextoEmpresa, clienteId: clienteBeauty.id })
        ]);
        setProtocolosBeauty(protocolosResposta.itens ?? []);
        setAlertasBeauty(
          segurancaResposta.produtosUtilizados.filter((produto) => produto.alertaEstoqueBaixo || produto.alertaValidade).length +
            segurancaResposta.produtosEstoque.filter((produto) => produto.estoqueBaixo || produto.validadeEmAlerta).length
        );
      } catch {
        setProtocolosBeauty([]);
        setAlertasBeauty(0);
      }
    } catch (falha) {
      setErro(falha instanceof Error ? falha.message : "Não foi possível carregar o acompanhamento Nutri.");
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    void carregar();
  }, []);

  const adicionarRegistro = async () => {
    if (!textoNovoRegistro.trim() || !pacienteId) {
      return;
    }

    try {
      const novo = await criarRegistroDiarioNutri({
        empresaId,
        pacienteId,
        texto: textoNovoRegistro.trim(),
        refeicaoNome: "Registro do paciente"
      });
      setRegistros((estadoAnterior) => [novo, ...estadoAnterior]);
      setTextoNovoRegistro("");
    } catch (falha) {
      setErro(falha instanceof Error ? falha.message : "Não foi possível enviar o registro.");
    }
  };

  return (
    <CapaPagina titulo="Acompanhamento Nutri" subtitulo={`Empresa ${empresaId ?? "sem contexto"} · paciente ${pacienteId ? "selecionado" : "não encontrado"}.`}>
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando acompanhamento...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : null}

      <CabecalhoSecao titulo="Plano publicado" />
      <Cartao titulo={plano?.objetivo ?? "Sem plano publicado"} descricao={plano?.descricao ?? "Quando a nutricionista publicar o plano, ele aparece aqui com refeições e lista de compras."}>
        <Text style={estilos.meta}>Metas: {metas} · Lembretes: {lembretes} · Compras: {listaCompras?.grupos.length ?? 0} grupos</Text>
      </Cartao>

      <CabecalhoSecao titulo="Rotina Beauty" />
      <Cartao
        titulo={protocolosBeauty[0]?.nome ?? "Sem protocolo Beauty ativo"}
        descricao={protocolosBeauty[0]?.objetivo ?? "Quando houver um protocolo estético vinculado, o paciente acompanha sessões, orientações e alertas aqui."}
      >
        <Text style={estilos.meta}>
          Protocolos: {protocolosBeauty.length} · Alertas de produto/segurança: {alertasBeauty}
        </Text>
      </Cartao>

      <CabecalhoSecao titulo="Composição rápida" />
      <Cartao titulo="Adicionar recado" descricao="Anote evolução, dúvidas e observações.">
        <TextInput
          style={estilos.input}
          value={textoNovoRegistro}
          onChangeText={setTextoNovoRegistro}
          placeholder="Digite uma observação"
          placeholderTextColor={temaAtendePro.textoSecundario}
        />
        <Pressable style={estilos.botao} onPress={adicionarRegistro}>
          <Text style={estilos.textoBotao}>Adicionar no diário</Text>
        </Pressable>
      </Cartao>

      <CabecalhoSecao titulo="Últimos registros" />
      <View style={estilos.registros}>
        {registros.length === 0 ? <EstadoVazio texto="Nenhum registro alimentar enviado ainda." /> : null}
        {registros.map((registro) => (
          <ItemLista
            key={registro.id}
            titulo={`${registro.refeicaoNome ?? "Registro alimentar"} · ${registro.statusRevisao}`}
            meta={formatarData(registro.registradoEm)}
            descricao={registro.parecerProfissional ? `${registro.texto}\nRevisão: ${registro.parecerProfissional}` : registro.texto}
          />
        ))}
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  registros: {
    gap: 12
  },
  input: {
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    borderRadius: 12,
    padding: 12,
    backgroundColor: temaAtendePro.superficie,
    color: temaAtendePro.texto
  },
  botao: {
    marginTop: 12,
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: "center",
    backgroundColor: temaAtendePro.destaque
  },
  textoBotao: {
    color: temaAtendePro.superficie,
    fontWeight: "700"
  },
  meta: {
    color: temaAtendePro.textoSecundario,
    marginTop: 8
  },
  estadoCarregamento: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    gap: 10
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  }
});

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}
