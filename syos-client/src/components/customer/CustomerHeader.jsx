import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiGetCart } from "../../services/api";
import syosLogo from "../../assets/syos-logo-text.png";

export default function CustomerHeader({ user, onLogout, cartCount: propCartCount }) {
    const [cartCount, setCartCount] = useState(propCartCount || 0);
    const navigate = useNavigate();

    useEffect(() => {
        // Fetch cart count on mount
        const fetchCartCount = async () => {
            try {
                const result = await apiGetCart();
                if (result.success && result.data?.items) {
                    setCartCount(result.data.items.length);
                } else {
                    // If cart fetch fails, just set to 0
                    setCartCount(0);
                }
            } catch (error) {
                // Silently fail for cart count
                setCartCount(0);
            }
        };

        if (user) {
            fetchCartCount();
        }
    }, [user]);

    // Update cart count when props change
    useEffect(() => {
        if (propCartCount !== undefined) {
            setCartCount(propCartCount);
        }
    }, [propCartCount]);

    return (
        <>
            <style>{`
                .customer-header {
                    background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
                    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
                    position: sticky;
                    top: 0;
                    z-index: 100;
                }
                .header-container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 16px 20px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 20px;
                }
                .header-logo {
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    height: 45px;
                }
                .header-logo img {
                    height: 100%;
                    width: auto;
                    filter: brightness(0) invert(1);
                }
                .header-nav {
                    display: flex;
                    gap: 32px;
                    align-items: center;
                }
                .nav-link {
                    color: rgba(255, 255, 255, 0.9);
                    text-decoration: none;
                    font-weight: 600;
                    font-size: 15px;
                    transition: all 0.2s;
                    cursor: pointer;
                    padding: 8px 16px;
                    border-radius: 8px;
                }
                .nav-link:hover {
                    color: white;
                    background: rgba(255, 255, 255, 0.15);
                    transform: translateY(-1px);
                }
                .header-actions {
                    display: flex;
                    gap: 16px;
                    align-items: center;
                }
                .icon-btn {
                    background: rgba(255, 255, 255, 0.15);
                    border: 2px solid rgba(255, 255, 255, 0.3);
                    color: white;
                    font-size: 22px;
                    cursor: pointer;
                    padding: 10px 14px;
                    border-radius: 10px;
                    transition: all 0.2s;
                    backdrop-filter: blur(10px);
                    position: relative;
                }
                .icon-btn:hover {
                    transform: translateY(-2px);
                    background: rgba(255, 255, 255, 0.25);
                    border-color: rgba(255, 255, 255, 0.5);
                    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                }
                .cart-badge {
                    position: absolute;
                    top: -4px;
                    right: -4px;
                    background: #ff4757;
                    color: white;
                    border-radius: 50%;
                    width: 22px;
                    height: 22px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 11px;
                    font-weight: 700;
                    border: 2px solid #22c55e;
                    box-shadow: 0 2px 8px rgba(255, 71, 87, 0.4);
                }
                .user-menu {
                    display: flex;
                    align-items: center;
                    gap: 16px;
                }
                .user-name {
                    font-weight: 600;
                    color: white;
                    font-size: 15px;
                }
                .logout-btn {
                    background: rgba(255, 255, 255, 0.2);
                    color: white;
                    border: 2px solid rgba(255, 255, 255, 0.3);
                    padding: 10px 20px;
                    border-radius: 10px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    backdrop-filter: blur(10px);
                }
                .logout-btn:hover {
                    background: rgba(255, 255, 255, 0.3);
                    border-color: rgba(255, 255, 255, 0.5);
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                }
                @media (max-width: 768px) {
                    .header-nav {
                        display: none;
                    }
                    .user-name {
                        display: none;
                    }
                }
            `}</style>

            <header className="customer-header">
                <div className="header-container">
                    <div className="header-logo" onClick={() => navigate('/customer/products')}>
                        <img src={syosLogo} alt="SYOS" />
                    </div>

                    <nav className="header-nav">
                        <div className="nav-link" onClick={() => navigate('/customer/products')}>
                            Products
                        </div>
                        <div className="nav-link" onClick={() => navigate('/customer/orders')}>
                            My Orders
                        </div>
                        <div className="nav-link" onClick={() => navigate('/customer/wishlist')}>
                            Wishlist
                        </div>
                    </nav>

                    <div className="header-actions">
                        <button 
                            className="icon-btn" 
                            onClick={() => navigate('/customer/cart')}
                            title="Shopping Cart"
                        >
                            🛒
                            {cartCount > 0 && (
                                <span className="cart-badge">{cartCount}</span>
                            )}
                        </button>

                        <div className="user-menu">
                            <span className="user-name">
                                {user?.fullName || user?.full_name || user?.username || 'Customer'}
                            </span>
                            <button className="logout-btn" onClick={onLogout}>
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </header>
        </>
    );
}
