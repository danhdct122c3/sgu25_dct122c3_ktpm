/**
 * Get full image URL by prepending backend base URL if needed
 * @param {string} imageUrl - Image URL from backend (can be relative or absolute)
 * @returns {string} Full image URL
 */
export const getImageUrl = (imageUrl) => {
  if (!imageUrl) {
    console.log('⚠️ getImageUrl: empty URL');
    return '';
  }
  
  // If already absolute URL (http/https), return as is
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    console.log('✅ getImageUrl: absolute URL', imageUrl);
    return imageUrl;
  }
  
  // If relative URL starting with /uploads/, prepend backend base URL with /api/v1
  if (imageUrl.startsWith('/uploads/')) {
    const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
    // Ensure backend URL ends with /api/v1
    const baseUrl = backendUrl.endsWith('/api/v1') ? backendUrl : `${backendUrl}/api/v1`;
    const fullUrl = `${baseUrl}${imageUrl}`;
    console.log('🔗 getImageUrl: converted', imageUrl, '→', fullUrl);
    return fullUrl;
  }
  
  // Otherwise return as is
  console.log('➡️ getImageUrl: passthrough', imageUrl);
  return imageUrl;
};
