import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProductList from './components/ProductList';
import AddProduct from './components/AddProduct';
import Cart from './components/Cart';
import Login from './components/Login';
import Register from './components/Register';
import AdminDashboard from './components/AdminDashboard';
import Profile from './components/Profile';
import ProtectedRoute from './components/ProtectedRoute';
import { CartProvider } from './context/CartContext';
import './index.css';

const App: React.FC = () => {
    return (
        <CartProvider>
            <Router>
                <div className="app-wrapper">
                    <Navbar />
                    <main className="main-content">
                        <Routes>
                            <Route path="/" element={<ProductList />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route element={<ProtectedRoute allowedRoles={['ROLE_USER', 'ROLE_ADMIN']} />}>
                                <Route path="/profile" element={<Profile />} />
                                <Route path="/cart" element={<Cart />} />
                            </Route>
                            <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
                                <Route path="/admin" element={<AddProduct />} />
                                <Route path="/dashboard" element={<AdminDashboard />} />
                            </Route>
                        </Routes>
                    </main>
                </div>
            </Router>
        </CartProvider>
    );
};

export default App;
