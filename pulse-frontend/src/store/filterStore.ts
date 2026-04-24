import { create } from 'zustand';

interface FilterState {
  domain: string;
  search: string;
  page: number;
  setDomain: (domain: string) => void;
  setSearch: (q: string) => void;
  setPage: (page: number) => void;
  reset: () => void;
}

export const useFilterStore = create<FilterState>((set) => ({
  domain: '',
  search: '',
  page: 0,
  setDomain: (domain) => set({ domain, page: 0 }),
  setSearch: (search) => set({ search, page: 0 }),
  setPage: (page) => set({ page }),
  reset: () => set({ domain: '', search: '', page: 0 }),
}));
