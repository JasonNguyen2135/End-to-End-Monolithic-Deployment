import axios from 'axios';

// Dùng đường dẫn tương đối, Nginx sẽ lo việc proxy tới Service Backend
const API_BASE_URL = '/api/v1';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authService = {
    login: (creds: any) => api.post('/auth/login', creds),
    register: (data: any) => api.post('/auth/register', data),
    getProfile: () => api.get('/auth/profile'),
};

export const productService = {
    getAllProducts: () => api.get('/products'),
    getProductById: (id: string) => api.get(`/products/${id}`),
    createProduct: (data: any) => api.post('/products', data),
    updateProduct: (id: string, data: any) => api.put(`/products/${id}`, data),
    deleteProduct: (id: string) => api.delete(`/products/${id}`),
};

export const categoryService = {
    getAll: () => api.get('/categories'),
    create: (name: string) => api.post('/categories', { name }),
};

export const cartService = {
    getCart: (userId: string) => api.get(`/cart/${userId}`),
    addToCart: (userId: string, item: any) => api.post(`/cart/${userId}/add`, item),
    clearCart: (userId: string) => api.delete(`/cart/${userId}`),
};

export const orderService = {
    placeOrder: (data: any) => api.post('/orders', data),
    getMyOrders: () => api.get('/orders/my-orders'),
};

export const adminService = {
    getAllUsers: () => api.get('/auth/admin/users'),
};

export default api;
