import { ref } from 'vue';

export function useTable() {
  const loading = ref(false);
  const dataSource = ref([]);
  const pagination = ref({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  const fetch = async (api: any) => {
    loading.value = true;
    try {
      const res = await api();
      dataSource.value = res.list || [];
      pagination.value.total = res.total || 0;
    } catch (error) {
      console.error(error);
    } finally {
      loading.value = false;
    }
  };

  return {
    loading,
    dataSource,
    pagination,
    fetch,
  };
}
