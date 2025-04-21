/**
 * Formats a number into Vietnamese Dong (VND) currency format.
 * @param {number | null | undefined} number - The number to format.
 * @returns {string} - The formatted currency string or '...' if input is invalid.
 */
export function formatPrice(number) {
  if (typeof number !== "number" || isNaN(number)) {
    // Handle cases where price might be null, undefined, or NaN
    console.warn("formatPrice received invalid input:", number);
    return "..."; // Return a placeholder or handle as appropriate
  }
  // Format the number with Vietnamese locale for separators
  const formattedNumber = number.toLocaleString("vi-VN");
  // Append " VND"
  return `${formattedNumber} VND`;
}

// You can add other formatter functions here if needed
// export function formatDate(date) { ... }
