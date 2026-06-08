import { ReactNode } from "react";
import {
  Pressable,
  StyleSheet,
  Text,
  View
} from "react-native";

export const temaAtendePro = {
  fundo: "#F7FAF9",
  superficie: "#FFFFFF",
  superficieSecundaria: "#EEF7F4",
  destaque: "#0F766E",
  destaqueProfundo: "#064E3B",
  acento: "#10B981",
  texto: "#0F172A",
  textoSecundario: "#64748B",
  borda: "#D6E4E0",
  sucesso: "#059669",
  aviso: "#D97706",
  erro: "#DC2626"
} as const;

export const espacamento = {
  xxs: 6,
  xs: 10,
  sm: 12,
  md: 16,
  lg: 20,
  xl: 24
} as const;

type VariacaoBadge = "info" | "sucesso" | "aviso" | "alerta" | "neutro";

const variantesBadge: Record<VariacaoBadge, string> = {
  info: temaAtendePro.destaque,
  sucesso: temaAtendePro.sucesso,
  aviso: temaAtendePro.aviso,
  alerta: temaAtendePro.erro,
  neutro: temaAtendePro.textoSecundario
};

export function CapaPagina({
  titulo,
  subtitulo,
  children
}: {
  titulo: string;
  subtitulo?: string;
  children: ReactNode;
}) {
  return (
    <View style={estilos.pagina}>
      <View style={estilos.cabecalho}>
        <Text style={estilos.titulo}>{titulo}</Text>
        {subtitulo ? <Text style={estilos.subtitulo}>{subtitulo}</Text> : null}
      </View>
      <View style={estilos.conteudo}>{children}</View>
    </View>
  );
}

export function Cartao({ titulo, descricao, destaque, children }: {
  titulo: string;
  descricao?: string;
  destaque?: boolean;
  children?: ReactNode;
}) {
  return (
    <View style={[estilos.cartao, destaque ? estilos.cartaoDestaque : null]}>
      <Text style={estilos.cartaoTitulo}>{titulo}</Text>
      {descricao ? <Text style={estilos.cartaoDescricao}>{descricao}</Text> : null}
      {children}
    </View>
  );
}

export function CabecalhoSecao({
  titulo,
  acao,
  onPressAcao
}: {
  titulo: string;
  acao?: string;
  onPressAcao?: () => void;
}) {
  return (
    <View style={estilos.topoSecao}>
      <Text style={estilos.tituloSecao}>{titulo}</Text>
      {acao && onPressAcao ? (
        <Pressable onPress={onPressAcao}>
          <Text style={estilos.acaoSecao}>{acao}</Text>
        </Pressable>
      ) : null}
    </View>
  );
}

export function ItemLista({
  titulo,
  meta,
  descricao,
  status,
  onPress
}: {
  titulo: string;
  meta?: string;
  descricao?: string;
  status?: string;
  onPress?: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [estilos.item, pressed ? estilos.itemPressionado : null]}
    >
      <View style={estilos.itemTopo}>
        <Text style={estilos.itemTitulo}>{titulo}</Text>
        {meta ? <Text style={estilos.itemMeta}>{meta}</Text> : null}
      </View>
      {descricao ? <Text style={estilos.itemDescricao}>{descricao}</Text> : null}
      {status ? <Badge texto={status} variante="info" /> : null}
    </Pressable>
  );
}

export function Badge({
  texto,
  variante = "neutro"
}: {
  texto: string;
  variante?: VariacaoBadge;
}) {
  const cor = variantesBadge[variante];
  return (
    <View style={[estilos.badge, { backgroundColor: cor }]}>
      <Text style={estilos.badgeTexto}>{texto}</Text>
    </View>
  );
}

export function GridCards({ children }: { children: ReactNode }) {
  return <View style={estilos.grade}>{children}</View>;
}

export function EstadoVazio({ texto }: { texto: string }) {
  return (
    <View style={estilos.estadoVazio}>
      <Text style={estilos.estadoVazioTexto}>{texto}</Text>
    </View>
  );
}

const estilos = StyleSheet.create({
  grade: {
    gap: espacamento.sm
  },
  pagina: {
    flex: 1,
    backgroundColor: temaAtendePro.fundo,
    padding: espacamento.xl
  },
  conteudo: {
    gap: espacamento.lg
  },
  cabecalho: {
    marginBottom: espacamento.lg,
    gap: espacamento.xs
  },
  titulo: {
    fontSize: 28,
    color: temaAtendePro.texto,
    fontWeight: "700"
  },
  subtitulo: {
    color: temaAtendePro.textoSecundario,
    lineHeight: 22
  },
  cartao: {
    backgroundColor: temaAtendePro.superficie,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 14,
    padding: espacamento.md,
    gap: espacamento.xs
  },
  cartaoDestaque: {
    borderColor: temaAtendePro.acento,
    borderWidth: 1.4,
    backgroundColor: temaAtendePro.superficieSecundaria
  },
  cartaoTitulo: {
    color: temaAtendePro.texto,
    fontWeight: "700",
    marginBottom: 2
  },
  cartaoDescricao: {
    color: temaAtendePro.textoSecundario,
    lineHeight: 20
  },
  topoSecao: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  },
  tituloSecao: {
    color: temaAtendePro.texto,
    fontSize: 18,
    fontWeight: "700"
  },
  acaoSecao: {
    color: temaAtendePro.destaque,
    fontWeight: "600"
  },
  item: {
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    backgroundColor: temaAtendePro.superficie,
    borderRadius: 12,
    padding: espacamento.md,
    gap: 4
  },
  itemPressionado: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.acento
  },
  itemTopo: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: espacamento.xs
  },
  itemTitulo: {
    flex: 1,
    color: temaAtendePro.texto,
    fontWeight: "600"
  },
  itemMeta: {
    color: temaAtendePro.textoSecundario,
    fontSize: 12
  },
  itemDescricao: {
    color: temaAtendePro.textoSecundario
  },
  badge: {
    alignSelf: "flex-start",
    marginTop: espacamento.xs,
    borderRadius: 999,
    paddingVertical: 4,
    paddingHorizontal: 10
  },
  badgeTexto: {
    color: temaAtendePro.superficie,
    fontSize: 12,
    fontWeight: "600"
  },
  estadoVazio: {
    borderRadius: 12,
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderStyle: "dashed",
    padding: espacamento.md
  },
  estadoVazioTexto: {
    color: temaAtendePro.textoSecundario
  }
});
