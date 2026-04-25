import React, { useEffect, useState } from 'react';
import { cartService, orderService } from '../services/api';
import { Loader2, CheckCircle, Trash2, ShoppingBag, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';

const Cart: React.FC = () => {
    const { refreshCartCount } = useCart();
    const [cart, setCart] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userEmail = user.email || "admin@vshop.com";

    useEffect(() => { fetchCart(); }, []);

    const fetchCart = async () => {
        try {
            const response = await cartService.getCart(userEmail);
            setCart(response.data);
        } catch (e) { console.error(e); } finally { setLoading(false); }
    };

    const handleCheckout = async () => {
        if (!cart || cart.items.length === 0) return;
        setProcessing(true);
        try {
            await orderService.placeOrder({ shippingAddress: "Manual Payment Address" });
            alert("Thanh toán thành công!");
            refreshCartCount();
            navigate('/payment-result?status=success');
        } catch (e: any) {
            alert(e.response?.data?.message || "Lỗi thanh toán");
        } finally { setProcessing(false); }
    };

    if (loading) return <div className="loader"><Loader2 className="spin" /></div>;

    return (
        <div className="cart-container container">
            <h2 className="section-title">Giỏ Hàng Của Bạn</h2>
            {(!cart || cart.items.length === 0) ? (
                <div className="empty-cart">
                    <ShoppingBag size={60} color="#cbd5e1" />
                    <p>Giỏ hàng đang trống.</p>
                    <button className="btn-buy" onClick={() => navigate('/')}>Tiếp tục mua sắm</button>
                </div>
            ) : (
                <div className="cart-content">
                    <div className="cart-items">
                        {cart.items.map((item: any, index: number) => (
                            <div key={index} className="cart-item">
                                <div className="item-main">
                                    <h4>{item.name}</h4>
                                    <p className="text-muted">${item.price.toFixed(2)} x {item.quantity}</p>
                                </div>
                                <div className="item-total">
                                    <strong>${(item.price * item.quantity).toFixed(2)}</strong>
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="cart-summary">
                        <div className="summary-row">
                            <span>Tổng tiền:</span>
                            <span className="price">${cart.totalPrice.toFixed(2)}</span>
                        </div>
                        <button className="btn-auth" style={{width:'100%', marginTop: '20px'}} onClick={handleCheckout} disabled={processing}>
                            {processing ? <Loader2 className="spin" /> : <CheckCircle size={20} />} Xác Nhận Thanh Toán
                        </button>
                        <button onClick={async () => { await cartService.clearCart(userEmail); refreshCartCount(); fetchCart(); }} style={{width:'100%', marginTop: '15px', background:'none', border:'none', color:'var(--text-light)', cursor:'pointer', fontWeight:'600', display:'flex', alignItems:'center', justifyContent:'center', gap:'8px'}}>
                             <Trash2 size={16}/> Xóa tất cả
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Cart;
