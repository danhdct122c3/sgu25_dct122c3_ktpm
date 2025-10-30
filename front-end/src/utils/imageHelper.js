/**
 * Get full image URL by prepending backend base URL if needed
 * @param {string} imageUrl - Image URL from backend (can be relative or absolute)
 * @returns {string} Full image URL
 */
/**
 * Normalize and return a safe image URL.
 * - returns null for empty input (caller should decide fallback)
 * - returns absolute URLs unchanged
 * - for /uploads/ paths, prepends VITE_API_BASE_URL or http://localhost:8080
 */
export const getImageUrl = (imageUrl) => {
  if (!imageUrl) return null;

  // absolute URL -> passthrough
  if (/^https?:\/\//i.test(imageUrl)) return imageUrl;

  // normalize leading slash
  const normalized = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`;

  // common case: files served under /uploads/
  if (normalized.startsWith('/uploads/')) {
    const backendBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    return `${backendBase.replace(/\/$/, '')}${normalized}`;
  }

  // otherwise return normalized relative path — caller may treat this as usable
  return normalized;
};
