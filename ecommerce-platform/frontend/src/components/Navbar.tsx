import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingBag, User, Search, LogOut, Settings, LayoutDashboard } from 'lucide-react';
import { useCart } from '../context/CartContext';

const Navbar: React.FC = () => {
    const { cartCount } = useCart();
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/login');
        window.location.reload();
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">
                    <ShoppingBag className="logo-icon" size={28} color="var(--primary)" />
                    <span className="logo-text">V-Shop</span>
                </Link>
            </div>
            
            <div className="search-bar">
                <Search size={18} className="search-icon" />
                <input 
                    type="text" 
                    placeholder="Tìm kiếm sản phẩm, thương hiệu..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
            </div>

            <ul className="navbar-links">
                <li><Link to="/">Cửa hàng</Link></li>
                
                {token ? (
                    <>
                        <li className="cart-link">
                            <Link to="/cart">
                                <div className="cart-icon-wrapper">
                                    <ShoppingBag size={22} />
                                    {cartCount > 0 && <span className="cart-count">{cartCount}</span>}
                                </div>
                            </Link>
                        </li>

                        {user.role === 'ROLE_ADMIN' && (
                            <li>
                                <Link to="/dashboard" title="Admin Dashboard">
                                    <LayoutDashboard size={20} />
                                </Link>
                            </li>
                        )}

                        {(user.role === 'ROLE_SELLER' || user.role === 'ROLE_ADMIN') && (
                            <li>
                                <Link to="/admin" title="Quản lý sản phẩm">
                                    <Settings size={20} />
                                </Link>
                            </li>
                        )}

                        <li>
                            <Link to="/profile" className="user-profile-link" style={{display: 'flex', alignItems: 'center', gap: '8px'}}>
                                <div style={{width: '32px', height: '32px', borderRadius: '50%', background: '#eef2ff', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--primary)', fontWeight: 'bold', fontSize: '12px'}}>
                                    {user.firstName?.charAt(0)}
                                </div>
                                <span style={{fontWeight: '600', color: 'var(--text)'}}>Hi, {user.firstName}</span>
                            </Link>
                        </li>

                        <li>
                            <button onClick={handleLogout} className="btn-logout" style={{background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '5px'}}>
                                <LogOut size={20} />
                            </button>
                        </li>
                    </>
                ) : (
                    <>
                        <li><Link to="/login" style={{color: 'var(--text)'}}>Đăng nhập</Link></li>
                        <li><Link to="/register" style={{background: 'var(--primary)', color: 'white', padding: '10px 20px', borderRadius: '12px'}}>Tham gia</Link></li>
                    </>
                )}
            </ul>
        </nav>
    );
};

export default Navbar;
