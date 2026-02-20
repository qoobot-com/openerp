import { ref } from 'vue';

export function useRequest<T = any>(
  api: () => Promise<T>,
  options: { immediate?: boolean } = {}
) {
  const loading = ref(false);
  const data = ref<T | null>(null);
  const error = ref<any>(null);

  const execute = async () => {
    loading.value = true;
    error.value = null;
    try {
      data.value = await api();
    } catch (err) {
      error.value = err;
    } finally {
      loading.value = false;
    }
  };

  if (options.immediate) {
    execute();
  }

  return {
    loading,
    data,
    error,
    execute,
  };
}
