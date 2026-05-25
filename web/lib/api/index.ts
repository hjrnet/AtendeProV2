export { ApiError, type ApiErrorPayload } from "@/lib/api/api-error";
export {
  apiClient,
  criarApiClient,
  type ApiClientConfig,
  type ApiRequestInterceptor,
  type ApiRequestOptions,
  type ApiResponseInterceptor
} from "@/lib/api/http-client";
