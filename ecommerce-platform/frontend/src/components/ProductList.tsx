import React, { useEffect, useState } from 'react';
import { productService, cartService } from '../services/api';
import { Product } from '../types';
import { Loader2, ShoppingCart, Plus, Minus } from 'lucide-react';
import { useCart } from '../context/CartContext';

const ProductList: React.FC = () => {
    const { refreshCartCount } = useCart();
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [quantities, setQuantities] = useState<{ [key: string]: number }>({});

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await productService.getAllProducts();
                setProducts(response.data);
            } catch (error) { console.error(error); } finally { setLoading(false); }
        };
        fetchProducts();
    }, []);

    const updateQuantity = (id: string, delta: number) => {
        setQuantities(prev => ({ ...prev, [id]: Math.max(1, (prev[id] || 1) + delta) }));
    };

    const addToCart = async (product: Product) => {
        try {
            const qty = quantities[product.id!] || 1;
            await cartService.addToCart("user123", { productId: product.id, quantity: qty });
            alert(`Đã thêm ${qty} ${product.name} vào giỏ hàng!`);
            refreshCartCount();
        } catch (error) { alert("Lỗi khi thêm vào giỏ hàng."); }
    };

    if (loading) return <div className="loader"><Loader2 className="spin" /> Đang tải sản phẩm...</div>;

    return (
        <div className="product-list-container container">
            <h2 className="section-title">Sản Phẩm Cửa Hàng</h2>
            <div className="product-grid">
                {products.map((p) => (
                    <div key={p.id} className="product-card">
                        <div className="product-image">
                            <img src={p.imageUrl || 'https://via.placeholder.com/260'} alt={p.name} />
                            {p.stock === 0 && <span className="out-of-stock-badge">Hết hàng</span>}
                        </div>
                        <div className="product-info">
                            <span className="category-tag">{p.categoryName || 'Chung'}</span>
                            <h3>{p.name}</h3>
                            <p className="description">{p.description}</p>
                            <div className="price-row">
                                <span className="price">${p.price.toFixed(2)}</span>
                                <span className="stock-info">Kho: {p.stock || 0}</span>
                            </div>
                            <div className="product-actions">
                                <div className="quantity-control">
                                    <button onClick={() => updateQuantity(p.id!, -1)}>-</button>
                                    <span>{quantities[p.id!] || 1}</span>
                                    <button onClick={() => updateQuantity(p.id!, 1)}>+</button>
                                </div>
                                <button className="btn-add-cart" onClick={() => addToCart(p)} disabled={p.stock === 0}>
                                    <ShoppingCart size={18} /> Thêm
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProductList;
