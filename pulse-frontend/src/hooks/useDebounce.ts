import { useState, useEffect } from 'react';

/**
 * Returns a debounced version of the given value.
 * Only updates after the specified delay (ms) has passed without changes.
 */
export function useDebounce<T>(value: T, delay: number = 400): T {
  const [debounced, setDebounced] = useState<T>(value);

  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);

  return debounced;
}
