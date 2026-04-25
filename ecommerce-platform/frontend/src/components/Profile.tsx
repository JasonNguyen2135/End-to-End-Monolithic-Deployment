import React, { useState, useEffect } from 'react';
import { authService, orderService } from '../services/api';
import { User, Package, Calendar, Clock, MapPin, Mail } from 'lucide-react';

const Profile: React.FC = () => {
    const [userData, setUser] = useState<any>({});
    const [orders, setOrders] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchInfo = async () => {
            try {
                const userRes = await authService.getProfile();
                setUser(userRes.data);
                const orderRes = await orderService.getMyOrders();
                setOrders(orderRes.data);
            } catch (e) {
                console.error(e);
            } finally {
                setLoading(false);
            }
        };
        fetchInfo();
    }, []);

    if (loading) return <div className="loader">Đang tải hồ sơ...</div>;

    const DEFAULT_AVATAR = `https://ui-avatars.com/api/?name=${userData.firstName}+${userData.lastName}&background=4f46e5&color=fff`;

    return (
        <div className="profile-page container">
            <div className="profile-card">
                <div className="profile-header-banner"></div>
                <div className="profile-main-info">
                    <div className="avatar-wrapper">
                        <img 
                            src={userData.profileImageUrl || DEFAULT_AVATAR} 
                            alt="Avatar" 
                            onError={(e) => { (e.target as HTMLImageElement).src = DEFAULT_AVATAR }}
                            className="profile-avatar-img" 
                        />
                    </div>
                    <div className="info-text">
                        <h2>{userData.firstName} {userData.lastName}</h2>
                        <span className={`role-tag ${userData.role}`}>{userData.role}</span>
                    </div>
                </div>

                <div className="profile-details-grid">
                    <div className="detail-item">
                        <Mail size={18} />
                        <div>
                            <label>Email liên hệ</label>
                            <p>{userData.email}</p>
                        </div>
                    </div>
                    <div className="detail-item">
                        <MapPin size={18} />
                        <div>
                            <label>Địa chỉ mặc định</label>
                            <p>Việt Nam</p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="order-history-section">
                <div className="section-title-wrapper">
                    <Package size={24} />
                    <h3>Lịch sử đơn hàng của bạn</h3>
                </div>
                
                <div className="orders-list">
                    {orders.length === 0 ? (
                        <div className="empty-state">
                            <p>Bạn chưa thực hiện giao dịch nào.</p>
                        </div>
                    ) : (
                        orders.map(o => (
                            <div key={o.id} className="order-card-item">
                                <div className="order-main">
                                    <div className="order-status-badge">
                                        <Clock size={14} /> {o.status}
                                    </div>
                                    <div className="order-id">Mã đơn: #{o.id.toString().slice(-6).toUpperCase()}</div>
                                    <div className="order-date"><Calendar size={14} /> {new Date(o.orderDate).toLocaleDateString('vi-VN')}</div>
                                </div>
                                <div className="order-price">
                                    Total: <strong>${o.totalPrice.toFixed(2)}</strong>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profile;
