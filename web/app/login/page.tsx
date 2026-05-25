import { LoginView } from "@/features/auth/components/login-view";
import { RotaPublica } from "@/features/auth/components/rota-protegida";

type LoginPageProps = {
  searchParams?: {
    redirectTo?: string | string[];
  };
};

export default function LoginPage({ searchParams }: LoginPageProps) {
  return (
    <RotaPublica>
      <LoginView redirectTo={searchParams?.redirectTo} />
    </RotaPublica>
  );
}
