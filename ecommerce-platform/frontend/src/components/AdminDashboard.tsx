import React, { useState, useEffect } from 'react';
import { categoryService, adminService } from '../services/api';
import { Tags, Users, Trash2, LayoutDashboard } from 'lucide-react';

const AdminDashboard: React.FC = () => {
    const [tab, setTab] = useState('categories');
    const [categories, setCategories] = useState<any[]>([]);
    const [users, setUsers] = useState<any[]>([]);
    const [newCat, setNewCat] = useState('');

    useEffect(() => {
        if (tab === 'categories') fetchCategories();
        if (tab === 'users') fetchUsers();
    }, [tab]);

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
                        <button onClick={() => setTab('categories')} className={tab === 'categories' ? 'active' : ''}>
                            <Tags size={20} /> Danh Mục
                        </button>
                        <button onClick={() => setTab('users')} className={tab === 'users' ? 'active' : ''}>
                            <Users size={20} /> Người Dùng
                        </button>
                    </aside>

                    <main className="admin-main-content">
                        <div className="data-section">
                            {tab === 'categories' && (
                                <>
                                    <h3 className="section-title-sm">Quản lý danh mục</h3>
                                    <div className="add-action-bar">
                                        <input value={newCat} onChange={(e) => setNewCat(e.target.value)} placeholder="Tên danh mục..." className="modern-input" />
                                        <button onClick={async () => { if(newCat) { await categoryService.create(newCat); setNewCat(''); fetchCategories(); } }} className="btn-primary-sm">Thêm</button>
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
