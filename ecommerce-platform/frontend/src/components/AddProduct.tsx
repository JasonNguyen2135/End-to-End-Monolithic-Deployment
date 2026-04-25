import React, { useState, useEffect } from 'react';
import { productService } from '../services/api';
import { Product } from '../types';
import { Save, Loader2, ArrowLeft, Trash2, PlusCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const AddProduct: React.FC = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [products, setProducts] = useState<Product[]>([]);
    const [product, setProduct] = useState({
        name: '',
        description: '',
        price: 0,
        imageUrl: '',
        stock: 100,
        categoryId: 1
    });

    useEffect(() => { fetchProducts(); }, []);

    const fetchProducts = async () => {
        try {
            const response = await productService.getAllProducts();
            setProducts(response.data);
        } catch (error) { console.error(error); }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            await productService.createProduct(product);
            alert("Sản phẩm đã được tạo!");
            setProduct({ name: '', description: '', price: 0, imageUrl: '', stock: 100, categoryId: 1 });
            fetchProducts();
        } catch (error: any) {
            alert(error.response?.data?.message || "Lỗi hệ thống");
        } finally { setLoading(false); }
    };

    return (
        <div className="admin-page container">
            <div className="admin-card">
                {/* 1. FIX HEADER CHỖ NÀY */}
                <div className="admin-header">
                    <button className="btn-back" onClick={() => navigate('/')}>
                        <ArrowLeft size={18} /> Quay lại cửa hàng
                    </button>
                    <h2>Quản Lý Cửa Hàng (Monolith)</h2>
                </div>
                
                <div className="admin-content">
                    <div className="form-section">
                        <h3 className="section-title-sm"><PlusCircle size={20}/> Thêm Sản Phẩm Mới</h3>
                        <form onSubmit={handleSubmit} className="admin-form">
                            <div className="form-group">
                                <label>Tên Sản Phẩm</label>
                                <input type="text" required value={product.name} onChange={(e) => setProduct({...product, name: e.target.value})} placeholder="Ví dụ: iPhone 15 Pro" />
                            </div>
                            
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Giá Bán ($)</label>
                                    <input type="number" required min="0" step="0.01" value={product.price || ''} onChange={(e) => setProduct({...product, price: parseFloat(e.target.value) || 0})} placeholder="0.00" />
                                </div>
                                <div className="form-group">
                                    <label>Kho (Stock)</label>
                                    <input type="number" required min="0" value={product.stock || ''} onChange={(e) => setProduct({...product, stock: parseInt(e.target.value) || 0})} placeholder="100" />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Link Hình Ảnh</label>
                                <input type="text" value={product.imageUrl} onChange={(e) => setProduct({...product, imageUrl: e.target.value})} placeholder="Dán link ảnh từ Google..." />
                            </div>

                            <div className="form-group">
                                <label>Mô tả</label>
                                <textarea required rows={4} value={product.description} onChange={(e) => setProduct({...product, description: e.target.value})} placeholder="Nhập mô tả chi tiết..." />
                            </div>

                            <button type="submit" className="btn-save" disabled={loading}>
                                {loading ? <Loader2 className="spin" size={20} /> : <Save size={20} />}
                                Lưu Sản Phẩm
                            </button>
                        </form>
                    </div>

                    {/* 2. FIX DANH SÁCH SẢN PHẨM CHỖ NÀY */}
                    <div className="list-section">
                        <h3 className="section-title-sm">Danh Sách Hiện Có ({products.length})</h3>
                        <div className="admin-product-list">
                            {products.map((p) => (
                                <div key={p.id} className="admin-product-item card-item">
                                    <img src={p.imageUrl || 'https://via.placeholder.com/50'} alt={p.name} />
                                    <div className="item-details">
                                        <p className="name">{p.name}</p>
                                        <span className="category-tag" style={{fontSize: '9px'}}>{p.categoryName || 'General'}</span>
                                        <p className="price">${p.price.toFixed(2)} - <small>Kho: {p.stock}</small></p>
                                    </div>
                                    <button className="btn-delete" onClick={async () => { if(window.confirm("Xóa?")) { await productService.deleteProduct(p.id!); fetchProducts(); }}}>
                                        <Trash2 size={18} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AddProduct;
