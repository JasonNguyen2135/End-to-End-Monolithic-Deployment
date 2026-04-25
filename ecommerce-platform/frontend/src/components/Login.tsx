import React, { useState } from 'react';
import { authService } from '../services/api';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, Loader2, AlertCircle } from 'lucide-react';

const Login: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const response = await authService.login({ email, password });
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
            navigate('/');
            window.location.reload();
        } catch (err: any) {
            setError(err.response?.data?.message || "Email hoặc mật khẩu không đúng.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <LogIn size={42} className="auth-icon" style={{color: 'var(--primary)', marginBottom: '15px'}} />
                    <h2>Đăng nhập</h2>
                    <p>Chào mừng bạn quay trở lại với V-Shop</p>
                </div>

                {error && (
                    <div className="auth-error" style={{background: '#fef2f2', color: '#b91c1c', padding: '12px', borderRadius: '10px', display: 'flex', gap: '8px', marginBottom: '20px'}}>
                        <AlertCircle size={18} />
                        <span style={{fontSize: '14px', fontWeight: '500'}}>{error}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} placeholder="name@company.com" />
                    </div>
                    <div className="form-group">
                        <label>Mật khẩu</label>
                        <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" />
                    </div>
                    <button type="submit" className="btn-auth" disabled={loading}>
                        {loading ? <Loader2 className="spin" size={20} /> : "Đăng nhập ngay"}
                    </button>
                </form>
                <div className="auth-footer" style={{textAlign: 'center', marginTop: '20px', color: 'var(--text-light)', fontSize: '14px'}}>
                    Chưa có tài khoản? <Link to="/register" style={{color: 'var(--primary)', fontWeight: '700', textDecoration: 'none'}}>Tham gia ngay</Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
