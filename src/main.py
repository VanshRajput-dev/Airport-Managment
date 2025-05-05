import cv2
import numpy as np

def homomorphic_filter(image, cutoff=30, order=2, gamma_low=0.5, gamma_high=2.0):
    image = image.astype(np.float32) / 255
    rows, cols = image.shape
    log_image = np.log1p(image)

    fft_image = np.fft.fft2(log_image)
    fft_shift = np.fft.fftshift(fft_image)

    u = np.arange(rows)
    v = np.arange(cols)
    u = u - rows // 2
    v = v - cols // 2
    V, U = np.meshgrid(v, u)
    D = np.sqrt(U**2 + V**2)
    H = 1 / (1 + (cutoff / (D + 1e-5))**(2 * order))

    filtered_fft = (gamma_high - gamma_low) * H * fft_shift + gamma_low
    filtered_image = np.fft.ifft2(np.fft.ifftshift(filtered_fft))
    filtered_image = np.exp(np.real(filtered_image)) - 1

    filtered_image = np.clip(filtered_image, 0, None)
    filtered_image = cv2.normalize(filtered_image, None, 0, 255, cv2.NORM_MINMAX)
    return filtered_image.astype(np.uint8)

# Load image (in grayscale mode)
img = cv2.imread('seven.png', 0)  # Make sure the filename is correct

# Apply filter
result = homomorphic_filter(img)

# Show result
cv2.imshow('Original', img)
cv2.imshow('Homomorphic Filtered', result)
cv2.waitKey(0)
cv2.destroyAllWindows()
