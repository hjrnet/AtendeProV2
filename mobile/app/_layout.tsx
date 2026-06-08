import { Stack } from "expo-router";
import { temaAtendePro } from "@/components/ui-shell";

export default function RootLayout() {
  return (
    <Stack
      screenOptions={{
        headerTitleAlign: "center",
        headerTintColor: temaAtendePro.superficie,
        headerStyle: {
          backgroundColor: temaAtendePro.destaque
        },
        headerShadowVisible: false,
        headerBackTitle: "Voltar",
        headerTitleStyle: {
          fontWeight: "600",
          color: temaAtendePro.superficie
        }
      }}
    >
      <Stack.Screen
        name="index"
        options={{
          headerTitle: "AtendePro"
        }}
      />
      <Stack.Screen
        name="auth/index"
        options={{
          headerTitle: "Entrar"
        }}
      />
      <Stack.Screen
        name="cliente"
        options={{
          headerTitle: "Paciente"
        }}
      />
      <Stack.Screen
        name="cliente/agenda"
        options={{
          headerTitle: "Agenda"
        }}
      />
      <Stack.Screen
        name="cliente/documentos"
        options={{
          headerTitle: "Documentos"
        }}
      />
      <Stack.Screen
        name="cliente/diario"
        options={{
          headerTitle: "Diário"
        }}
      />
      <Stack.Screen
        name="cliente/mensagens"
        options={{
          headerTitle: "Mensagens"
        }}
      />
      <Stack.Screen
        name="profissional"
        options={{
          headerTitle: "Profissional"
        }}
      />
      <Stack.Screen
        name="profissional/agenda"
        options={{
          headerTitle: "Agenda do dia"
        }}
      />
      <Stack.Screen
        name="profissional/mensagens"
        options={{
          headerTitle: "Mensagens"
        }}
      />
      <Stack.Screen
        name="profissional/evolucao"
        options={{
          headerTitle: "Acompanhamento"
        }}
      />
      <Stack.Screen
        name="notificacoes"
        options={{
          headerTitle: "Notificações"
        }}
      />
    </Stack>
  );
}
