import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { apiGetWishlist, apiRemoveFromWishlist, apiAddToCart } from "../../services/api";
import syosLogo from "../../assets/syos-logo.png";

export default function WishlistPage({ user, onLogout }) {
    const [wishlist, setWishlist] = useState([]);
    const [loading, setLoading] = useState(true);
    const [notification, setNotification] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadWishlist();
    }, []);

    const loadWishlist = async () => {
        setLoading(true);
        const result = await apiGetWishlist();
        if (result.success) {
            setWishlist(result.data || []);
        }
        setLoading(false);
    };

    const handleRemove = async (productCode, productName) => {
        const result = await apiRemoveFromWishlist(productCode);
        if (result.success) {
            showNotification(`${productName} removed from wishlist`, "success");
            loadWishlist();
        } else {
            showNotification(result.message || "Failed to remove from wishlist", "error");
        }
    };

    const handleAddToCart = async (product) => {
        const result = await apiAddToCart(product.productCode, 1);
        if (result.success) {
            showNotification(`${product.name || product.productName} added to cart!`, "success");
        } else {
            showNotification(result.message || "Failed to add to cart", "error");
        }
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    return (
        <>
            <style>{`
                .wishlist-page {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                }
                .wishlist-container {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .page-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 24px 0;
                }
                .wishlist-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                    gap: 24px;
                }
                .wishlist-card {
                    background: white;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    transition: transform 0.2s, box-shadow 0.2s;
                }
                .wishlist-card:hover {
                    transform: translateY(-4px);
                    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
                }
                .product-image-container {
                    position: relative;
                    width: 100%;
                    padding-top: 100%;
                    background: #f5f5f5;
                    overflow: hidden;
                    cursor: pointer;
                }
                .product-image {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }
                .discount-badge {
                    position: absolute;
                    top: 10px;
                    left: 10px;
                    background: #ef4444;
                    color: white;
                    padding: 4px 12px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                }
                .out-of-stock-overlay {
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0,0,0,0.6);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    font-weight: 700;
                    font-size: 18px;
                }
                .card-content {
                    padding: 16px;
                }
                .product-name {
                    font-size: 16px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                    min-height: 40px;
                }
                .product-price {
                    font-size: 20px;
                    font-weight: 700;
                    color: #22c55e;
                    margin-bottom: 4px;
                }
                .original-price {
                    font-size: 14px;
                    color: #9ca3af;
                    text-decoration: line-through;
                    margin-left: 8px;
                }
                .added-date {
                    font-size: 13px;
                    color: #6b7280;
                    margin-bottom: 12px;
                }
                .card-actions {
                    display: flex;
                    gap: 8px;
                }
                .btn {
                    flex: 1;
                    padding: 10px 16px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                }
                .btn-cart {
                    background: #22c55e;
                    color: white;
                }
                .btn-cart:hover {
                    background: #5568d3;
                }
                .btn-cart:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                }
                .btn-remove {
                    background: #fee2e2;
                    color: #ef4444;
                }
                .btn-remove:hover {
                    background: #fecaca;
                }
                .loading-spinner {
                    text-align: center;
                    padding: 60px 20px;
                    font-size: 18px;
                    color: #6b7280;
                }
                .empty-wishlist {
                    text-align: center;
                    padding: 60px 20px;
                    background: white;
                    border-radius: 12px;
                }
                .empty-icon {
                    width: 180px;
                    height: auto;
                    margin: 0 auto 24px;
                    display: block;
                    filter: drop-shadow(0 4px 12px rgba(34, 197, 94, 0.2));
                }
                .empty-title {
                    font-size: 24px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .empty-text {
                    color: #6b7280;
                    margin-bottom: 24px;
                }
                .browse-btn {
                    background: #22c55e;
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .browse-btn:hover {
                    background: #5568d3;
                }
                .notification {
                    position: fixed;
                    top: 90px;
                    right: 20px;
                    padding: 16px 24px;
                    border-radius: 12px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.2);
                    font-weight: 600;
                    z-index: 1000;
                    animation: slideIn 0.3s ease;
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255,255,255,0.3);
                }
                .notification.success { 
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white; 
                }
                .notification.error { 
                    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                    color: white; 
                }
                @keyframes slideIn {
                    from { transform: translateX(400px); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
            `}</style>

            <div className="wishlist-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="wishlist-container">
                    <h1 className="page-title">❤️ My Wishlist</h1>

                    {loading ? (
                        <div className="loading-spinner">
                            ⏳ Loading wishlist...
                        </div>
                    ) : wishlist.length === 0 ? (
                        <div className="empty-wishlist">
                            <img src={syosLogo} alt="SYOS" className="empty-icon" />
                            <h2 className="empty-title">Your wishlist is empty</h2>
                            <p className="empty-text">
                                Save your favorite items here for later!
                            </p>
                            <button 
                                className="browse-btn"
                                onClick={() => navigate('/customer/products')}
                            >
                                Browse Products
                            </button>
                        </div>
                    ) : (
                        <div className="wishlist-grid">
                            {wishlist.map(item => (
                                <div key={item.wishlistId} className="wishlist-card">
                                    <div 
                                        className="product-image-container"
                                        onClick={() => navigate(`/customer/products/${item.productCode}`)}
                                    >
                                        <img 
                                            src={item.imageUrl || "/placeholder-product.jpg"}
                                            alt={item.name || item.productName}
                                            className="product-image"
                                            onError={(e) => e.target.src = "/placeholder-product.jpg"}
                                        />
                                        {item.discountPercentage > 0 && item.inStock && (
                                            <div className="discount-badge">
                                                {item.discountPercentage}% OFF
                                            </div>
                                        )}
                                        {!item.inStock && (
                                            <div className="out-of-stock-overlay">
                                                OUT OF STOCK
                                            </div>
                                        )}
                                    </div>

                                    <div className="card-content">
                                        <h3 className="product-name">{item.name || item.productName}</h3>
                                        
                                        <div className="product-price">
                                            LKR {(item.discountPercentage > 0 ? item.discountedPrice : item.originalPrice)?.toFixed(2)}
                                            {item.discountPercentage > 0 && (
                                                <span className="original-price">
                                                    LKR {item.originalPrice?.toFixed(2)}
                                                </span>
                                            )}
                                        </div>

                                        <div className="added-date">
                                            {item.addedDate ? (
                                                <>Added {new Date(item.addedDate).toLocaleDateString('en-US', {
                                                    month: 'short',
                                                    day: 'numeric',
                                                    year: 'numeric'
                                                })}</>
                                            ) : (
                                                <>In Wishlist</>
                                            )}
                                        </div>

                                        <div className="card-actions">
                                            <button 
                                                className="btn btn-cart"
                                                onClick={() => handleAddToCart(item)}
                                                disabled={!item.inStock}
                                            >
                                                {item.inStock ? '🛒 Add to Cart' : 'Out of Stock'}
                                            </button>
                                            <button 
                                                className="btn btn-remove"
                                                onClick={() => handleRemove(item.productCode, item.name || item.productName)}
                                            >
                                                🗑️
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                {notification && (
                    <div className={`notification ${notification.type}`}>
                        {notification.message}
                    </div>
                )}
            </div>
        </>
    );
}
