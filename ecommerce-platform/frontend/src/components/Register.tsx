import React, { useState } from 'react';
import { authService } from '../services/api';
import { useNavigate, Link } from 'react-router-dom';
import { UserPlus, Loader2, AlertCircle } from 'lucide-react';

const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        role: 'ROLE_USER'
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await authService.register(formData);
            alert("Đăng ký thành công! Vui lòng đăng nhập.");
            navigate('/login');
        } catch (err: any) {
            setError(err.response?.data?.message || "Lỗi khi đăng ký tài khoản.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card" style={{maxWidth: '550px'}}>
                <div className="auth-header">
                    <UserPlus size={42} style={{color: 'var(--primary)', marginBottom: '15px'}} />
                    <h2>Tham gia cùng V-Shop</h2>
                    <p>Bắt đầu mua sắm hoặc bán hàng chuyên nghiệp</p>
                </div>

                {error && (
                    <div className="auth-error" style={{background: '#fef2f2', color: '#b91c1c', padding: '12px', borderRadius: '10px', display: 'flex', gap: '8px', marginBottom: '20px'}}>
                        <AlertCircle size={18} />
                        <span style={{fontSize: '14px', fontWeight: '500'}}>{error}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-grid">
                        <div className="form-group">
                            <label>Họ</label>
                            <input type="text" required value={formData.firstName} onChange={(e) => setFormData({...formData, firstName: e.target.value})} placeholder="Nguyễn" />
                        </div>
                        <div className="form-group">
                            <label>Tên</label>
                            <input type="text" required value={formData.lastName} onChange={(e) => setFormData({...formData, lastName: e.target.value})} placeholder="Văn A" />
                        </div>
                    </div>
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" required value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} placeholder="example@gmail.com" />
                    </div>
                    <div className="form-group">
                        <label>Mật khẩu</label>
                        <input type="password" required value={formData.password} onChange={(e) => setFormData({...formData, password: e.target.value})} placeholder="Tối thiểu 8 ký tự" />
                    </div>
                    <div className="form-group">
                        <label>Bạn tham gia với vai trò?</label>
                        <select className="auth-select" value={formData.role} onChange={(e) => setFormData({...formData, role: e.target.value})}>
                            <option value="ROLE_USER">Người mua hàng (User)</option>
                            <option value="ROLE_SELLER">Người bán hàng (Seller)</option>
                        </select>
                    </div>
                    <button type="submit" className="btn-auth" disabled={loading}>
                        {loading ? <Loader2 className="spin" size={20} /> : "Tạo tài khoản ngay"}
                    </button>
                </form>
                <div className="auth-footer" style={{textAlign: 'center', marginTop: '20px', color: 'var(--text-light)', fontSize: '14px'}}>
                    Đã có tài khoản? <Link to="/login" style={{color: 'var(--primary)', fontWeight: '700', textDecoration: 'none'}}>Đăng nhập</Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
