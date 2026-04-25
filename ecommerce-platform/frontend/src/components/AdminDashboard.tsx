import React, { useState, useEffect } from 'react';
import { sellerService, categoryService, adminService } from '../services/api';
import { ShieldCheck, Tags, Users, CheckCircle, XCircle, Trash2, Plus, LayoutDashboard } from 'lucide-react';

const AdminDashboard: React.FC = () => {
    const [tab, setTab] = useState('sellers');
    const [sellerRequests, setSellers] = useState<any[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [users, setUsers] = useState<any[]>([]);
    const [newCat, setNewCat] = useState('');

    useEffect(() => {
        if (tab === 'sellers') fetchSellers();
        if (tab === 'categories') fetchCategories();
        if (tab === 'users') fetchUsers();
    }, [tab]);

    const fetchSellers = async () => {
        try {
            const res = await sellerService.getPendingRequests();
            setSellers(res.data);
        } catch (e) { console.error(e); }
    };

    const fetchCategories = async () => {
        try {
            const res = await categoryService.getAll();
            setCategories(res.data);
        } catch (e) { console.error(e); }
    };

    const fetchUsers = async () => {
        try {
            const res = await adminService.getAllUsers();
            setUsers(res.data);
        } catch (e) { console.error(e); }
    };

    const handleApprove = async (id: number) => {
        await sellerService.approveRequest(id);
        alert("Đã duyệt người bán!");
        fetchSellers();
    };

    return (
        <div className="admin-page container">
            <div className="admin-card">
                <div className="admin-header">
                    <div className="header-title">
                        <LayoutDashboard size={24} color="var(--primary)" />
                        <h2>Hệ Thống Quản Trị</h2>
                    </div>
                </div>

                <div className="admin-dashboard">
                    <aside className="admin-sidebar">
                        <button onClick={() => setTab('sellers')} className={tab === 'sellers' ? 'active' : ''}>
                            <ShieldCheck size={20} /> Duyệt Người Bán
                        </button>
                        <button onClick={() => setTab('categories')} className={tab === 'categories' ? 'active' : ''}>
                            <Tags size={20} /> Danh Mục
                        </button>
                        <button onClick={() => setTab('users')} className={tab === 'users' ? 'active' : ''}>
                            <Users size={20} /> Người Dùng
                        </button>
                    </aside>

                    <main className="admin-main-content">
                        <div className="data-section">
                            {tab === 'sellers' && (
                                <>
                                    <h3 className="section-title-sm">Yêu cầu đăng ký Seller</h3>
                                    {sellerRequests.map(r => (
                                        <div key={r.id} className="card-item">
                                            <span>{r.userEmail} - <strong>{r.storeName}</strong></span>
                                            <div className="actions">
                                                <button onClick={() => handleApprove(r.id)} className="btn-success-icon"><CheckCircle size={22} color="var(--success)" /></button>
                                            </div>
                                        </div>
                                    ))}
                                </>
                            )}

                            {tab === 'categories' && (
                                <>
                                    <h3 className="section-title-sm">Quản lý danh mục</h3>
                                    <div className="add-action-bar">
                                        <input value={newCat} onChange={(e) => setNewCat(e.target.value)} placeholder="Tên danh mục..." className="modern-input" />
                                        <button onClick={async () => { await categoryService.create(newCat); setNewCat(''); fetchCategories(); }} className="btn-primary-sm">Thêm</button>
                                    </div>
                                    {categories.map(c => (
                                        <div key={c.id} className="table-row">
                                            <span>{c.name}</span>
                                            <Trash2 size={18} color="var(--danger)" style={{cursor:'pointer'}} />
                                        </div>
                                    ))}
                                </>
                            )}

                            {tab === 'users' && (
                                <>
                                    <h3 className="section-title-sm">Danh sách người dùng</h3>
                                    {users.map(u => (
                                        <div key={u.id} className="table-row">
                                            <span>{u.email}</span>
                                            <span className="role-tag">{u.role}</span>
                                        </div>
                                    ))}
                                </>
                            )}
                        </div>
                    </main>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
