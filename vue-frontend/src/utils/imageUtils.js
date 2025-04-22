const BASE_URL = "http://localhost:8080/photos/";
const PLACEHOLDER = "/placeholder.png"; // Assuming placeholder is in public folder

/**
 * Extracts the first valid image filename from a potentially semicolon-separated string
 * and constructs the full URL.
 * @param {string | null | undefined} imageString - The image string from the backend.
 * @returns {string} The full URL to the first image or a placeholder.
 */
export function getFirstImageUrl(imageString) {
  if (!imageString || typeof imageString !== "string") {
    return PLACEHOLDER;
  }

  // Split the string by semicolon
  const images = imageString.split(";");
  // Get the first part and trim whitespace
  const firstImage = images[0]?.trim();

  if (firstImage) {
    // Basic check if it looks like a filename (e.g., contains a dot)
    // You might want a more robust check depending on expected filenames
    if (firstImage.includes(".")) {
      // Check if it's already a full URL (less likely from backend but good practice)
      if (
        firstImage.startsWith("http://") ||
        firstImage.startsWith("https://")
      ) {
        return firstImage;
      }
      // Construct the full URL using the base path
      return BASE_URL + firstImage;
    }
  }

  // Fallback if the first part is empty or doesn't look like a valid filename
  return PLACEHOLDER;
}
