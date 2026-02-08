import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { 
    apiGetCustomerProduct, 
    apiAddToCart, 
    apiAddToWishlist,
    apiGetProductReviews,
    apiAddProductReview
} from "../../services/api";

export default function ProductDetailsPage({ user, onLogout }) {
    const { productCode } = useParams();
    const [product, setProduct] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [reviewStats, setReviewStats] = useState({ averageRating: 0, totalReviews: 0 });
    const [loading, setLoading] = useState(true);
    const [quantity, setQuantity] = useState(1);
    const [notification, setNotification] = useState(null);
    const [showReviewForm, setShowReviewForm] = useState(false);
    const [reviewForm, setReviewForm] = useState({ rating: 5, comment: "" });
    const navigate = useNavigate();

    useEffect(() => {
        loadProductDetails();
        loadReviews();
    }, [productCode]);

    const loadProductDetails = async () => {
        setLoading(true);
        const result = await apiGetCustomerProduct(productCode);
        if (result.success) {
            setProduct(result.data);
        } else {
            showNotification("Product not found", "error");
        }
        setLoading(false);
    };

    const loadReviews = async () => {
        const result = await apiGetProductReviews(productCode);
        if (result.success) {
            setReviews(result.data || []);
            setReviewStats({
                averageRating: result.averageRating || 0,
                totalReviews: result.totalReviews || 0
            });
        }
    };

    const handleAddToCart = async () => {
        const result = await apiAddToCart(productCode, quantity);
        if (result.success) {
            showNotification(`${product.name} added to cart!`, "success");
        } else {
            showNotification(result.message || "Failed to add to cart", "error");
        }
    };

    const handleAddToWishlist = async () => {
        const result = await apiAddToWishlist(productCode);
        if (result.success) {
            showNotification(`${product.name} added to wishlist!`, "success");
        } else {
            showNotification(result.message || "Failed to add to wishlist", "error");
        }
    };

    const handleSubmitReview = async () => {
        if (!reviewForm.comment.trim()) {
            showNotification("Please write a review", "error");
            return;
        }

        const result = await apiAddProductReview({
            productCode,
            rating: reviewForm.rating,
            comment: reviewForm.comment
        });

        if (result.success) {
            showNotification("Review submitted successfully!", "success");
            setShowReviewForm(false);
            setReviewForm({ rating: 5, comment: "" });
            loadReviews();
        } else {
            showNotification(result.message || "Failed to submit review", "error");
        }
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    const renderStars = (rating) => {
        return "⭐".repeat(Math.round(rating)) + "☆".repeat(5 - Math.round(rating));
    };

    if (loading) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ textAlign: 'center', padding: '60px', fontSize: '18px', color: '#6b7280' }}>
                    ⏳ Loading product...
                </div>
            </div>
        );
    }

    if (!product) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ maxWidth: '800px', margin: '0 auto', padding: '60px 20px', textAlign: 'center' }}>
                    <div style={{ fontSize: '64px', marginBottom: '16px' }}>❌</div>
                    <h2 style={{ fontSize: '24px', marginBottom: '16px' }}>Product not found</h2>
                    <button 
                        onClick={() => navigate('/customer/products')}
                        style={{
                            background: '#22c55e',
                            color: 'white',
                            border: 'none',
                            padding: '12px 24px',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: 'pointer'
                        }}
                    >
                        Browse Products
                    </button>
                </div>
            </div>
        );
    }

    const isInStock = product.inStock === true && product.availableQuantity > 0;
    const hasDiscount = product.discountPercentage > 0;
    const displayPrice = hasDiscount ? product.discountedPrice : product.originalPrice;

    return (
        <>
            <style>{`
                .product-details-page {
                    min-height: 100vh;
                    background: #f9fafb;
                }
                .product-container {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .back-btn {
                    background: #f3f4f6;
                    color: #4b5563;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                    margin-bottom: 24px;
                }
                .back-btn:hover {
                    background: #e5e7eb;
                }
                .product-main {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 40px;
                    background: white;
                    border-radius: 12px;
                    padding: 40px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    margin-bottom: 32px;
                }
                .product-image-section {
                    position: relative;
                }
                .main-image {
                    width: 100%;
                    aspect-ratio: 1;
                    object-fit: cover;
                    border-radius: 12px;
                    background: #f5f5f5;
                }
                .discount-badge {
                    position: absolute;
                    top: 20px;
                    left: 20px;
                    background: #ef4444;
                    color: white;
                    padding: 8px 16px;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 700;
                }
                .product-info-section {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                }
                .product-category {
                    color: #22c55e;
                    font-size: 14px;
                    font-weight: 600;
                    text-transform: uppercase;
                }
                .product-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0;
                }
                .product-rating {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 18px;
                }
                .rating-number {
                    font-weight: 600;
                    color: #1f2937;
                }
                .review-count {
                    color: #6b7280;
                    font-size: 14px;
                }
                .product-price-section {
                    padding: 20px;
                    background: #f9fafb;
                    border-radius: 8px;
                }
                .price {
                    font-size: 36px;
                    font-weight: 700;
                    color: #22c55e;
                }
                .original-price {
                    font-size: 20px;
                    color: #9ca3af;
                    text-decoration: line-through;
                    margin-left: 12px;
                }
                .savings {
                    display: inline-block;
                    background: #fef3c7;
                    color: #f59e0b;
                    padding: 4px 12px;
                    border-radius: 6px;
                    font-size: 14px;
                    font-weight: 600;
                    margin-left: 12px;
                }
                .product-stock {
                    font-size: 16px;
                    font-weight: 600;
                    color: #10b981;
                }
                .out-of-stock {
                    color: #ef4444;
                }
                .product-description {
                    color: #4b5563;
                    line-height: 1.6;
                }
                .quantity-selector {
                    display: flex;
                    align-items: center;
                    gap: 16px;
                }
                .quantity-label {
                    font-weight: 600;
                    color: #374151;
                }
                .quantity-controls {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    border: 2px solid #e5e7eb;
                    border-radius: 8px;
                    overflow: hidden;
                }
                .qty-btn {
                    background: #f3f4f6;
                    border: none;
                    width: 44px;
                    height: 44px;
                    cursor: pointer;
                    font-weight: 700;
                    font-size: 18px;
                    transition: background 0.2s;
                }
                .qty-btn:hover {
                    background: #e5e7eb;
                }
                .qty-input {
                    width: 60px;
                    text-align: center;
                    border: none;
                    font-weight: 600;
                    font-size: 18px;
                }
                .action-buttons {
                    display: flex;
                    gap: 12px;
                }
                .btn {
                    padding: 14px 32px;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 700;
                    cursor: pointer;
                    transition: all 0.2s;
                }
                .btn-primary {
                    flex: 1;
                    background: #10b981;
                    color: white;
                }
                .btn-primary:hover {
                    background: #059669;
                }
                .btn-primary:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                }
                .btn-secondary {
                    background: #fee2e2;
                    color: #ef4444;
                }
                .btn-secondary:hover {
                    background: #fecaca;
                }
                .reviews-section {
                    background: white;
                    border-radius: 12px;
                    padding: 32px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                .section-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 24px 0;
                }
                .write-review-btn {
                    background: #22c55e;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                    margin-bottom: 24px;
                }
                .write-review-btn:hover {
                    background: #5568d3;
                }
                .review-form {
                    background: #f9fafb;
                    padding: 20px;
                    border-radius: 8px;
                    margin-bottom: 24px;
                }
                .form-group {
                    margin-bottom: 16px;
                }
                .form-label {
                    display: block;
                    font-weight: 600;
                    color: #374151;
                    margin-bottom: 8px;
                }
                .star-selector {
                    display: flex;
                    gap: 8px;
                    font-size: 32px;
                }
                .star {
                    cursor: pointer;
                    transition: transform 0.2s;
                }
                .star:hover {
                    transform: scale(1.2);
                }
                .review-textarea {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e5e7eb;
                    border-radius: 8px;
                    font-size: 15px;
                    resize: vertical;
                    min-height: 100px;
                }
                .review-textarea:focus {
                    outline: none;
                    border-color: #22c55e;
                }
                .form-actions {
                    display: flex;
                    gap: 8px;
                }
                .reviews-list {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                }
                .review-card {
                    padding: 20px;
                    background: #f9fafb;
                    border-radius: 8px;
                }
                .review-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: start;
                    margin-bottom: 12px;
                }
                .reviewer-name {
                    font-weight: 600;
                    color: #1f2937;
                }
                .review-date {
                    font-size: 13px;
                    color: #6b7280;
                }
                .review-rating {
                    font-size: 16px;
                }
                .review-comment {
                    color: #4b5563;
                    line-height: 1.6;
                }
                .no-reviews {
                    text-align: center;
                    padding: 40px;
                    color: #6b7280;
                }
                .notification {
                    position: fixed;
                    top: 80px;
                    right: 20px;
                    padding: 16px 24px;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                    font-weight: 600;
                    z-index: 1000;
                    animation: slideIn 0.3s ease;
                }
                .notification.success { background: #10b981; color: white; }
                .notification.error { background: #ef4444; color: white; }
                @keyframes slideIn {
                    from { transform: translateX(400px); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @media (max-width: 768px) {
                    .product-main {
                        grid-template-columns: 1fr;
                    }
                }
            `}</style>

            <div className="product-details-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="product-container">
                    <button className="back-btn" onClick={() => navigate('/customer/products')}>
                        ← Back to Products
                    </button>

                    <div className="product-main">
                        <div className="product-image-section">
                            <img 
                                src={product.imageUrl || "/placeholder-product.jpg"}
                                alt={product.name}
                                className="main-image"
                                onError={(e) => e.target.src = "/placeholder-product.jpg"}
                            />
                            {hasDiscount && (
                                <div className="discount-badge">
                                    {product.discountPercentage}% OFF
                                </div>
                            )}
                        </div>

                        <div className="product-info-section">
                            <div className="product-category">{product.categoryName}</div>
                            
                            <h1 className="product-title">{product.name}</h1>

                            {reviewStats.totalReviews > 0 && (
                                <div className="product-rating">
                                    <span>{renderStars(reviewStats.averageRating)}</span>
                                    <span className="rating-number">{reviewStats.averageRating.toFixed(1)}</span>
                                    <span className="review-count">({reviewStats.totalReviews} reviews)</span>
                                </div>
                            )}

                            <div className="product-price-section">
                                <div>
                                    <span className="price">LKR {displayPrice?.toFixed(2)}</span>
                                    {hasDiscount && (
                                        <>
                                            <span className="original-price">
                                                LKR {product.originalPrice?.toFixed(2)}
                                            </span>
                                            <span className="savings">
                                                SAVE {product.discountPercentage}%
                                            </span>
                                        </>
                                    )}
                                </div>
                            </div>

                            <div className={`product-stock ${!isInStock ? 'out-of-stock' : ''}`}>
                                {isInStock ? `✅ ${product.availableQuantity} in stock` : '❌ Out of Stock'}
                            </div>

                            {product.description && (
                                <div className="product-description">
                                    {product.description}
                                </div>
                            )}

                            <div className="quantity-selector">
                                <div className="quantity-label">Quantity:</div>
                                <div className="quantity-controls">
                                    <button 
                                        className="qty-btn"
                                        onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                    >
                                        −
                                    </button>
                                    <input 
                                        type="number"
                                        className="qty-input"
                                        value={quantity}
                                        onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                                        min="1"
                                        max={product.availableQuantity}
                                    />
                                    <button 
                                        className="qty-btn"
                                        onClick={() => setQuantity(Math.min(product.availableQuantity, quantity + 1))}
                                    >
                                        +
                                    </button>
                                </div>
                            </div>

                            <div className="action-buttons">
                                <button 
                                    className="btn btn-primary"
                                    onClick={handleAddToCart}
                                    disabled={!isInStock}
                                >
                                    {isInStock ? '🛒 Add to Cart' : 'Out of Stock'}
                                </button>
                                <button 
                                    className="btn btn-secondary"
                                    onClick={handleAddToWishlist}
                                >
                                    ❤️ Wishlist
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="reviews-section">
                        <h2 className="section-title">
                            ⭐ Customer Reviews ({reviewStats.totalReviews})
                        </h2>

                        {!showReviewForm ? (
                            <button 
                                className="write-review-btn"
                                onClick={() => setShowReviewForm(true)}
                            >
                                ✍️ Write a Review
                            </button>
                        ) : (
                            <div className="review-form">
                                <div className="form-group">
                                    <label className="form-label">Your Rating</label>
                                    <div className="star-selector">
                                        {[1, 2, 3, 4, 5].map(star => (
                                            <span 
                                                key={star}
                                                className="star"
                                                onClick={() => setReviewForm(prev => ({ ...prev, rating: star }))}
                                            >
                                                {star <= reviewForm.rating ? '⭐' : '☆'}
                                            </span>
                                        ))}
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Your Review</label>
                                    <textarea 
                                        className="review-textarea"
                                        placeholder="Share your experience with this product..."
                                        value={reviewForm.comment}
                                        onChange={(e) => setReviewForm(prev => ({ ...prev, comment: e.target.value }))}
                                    />
                                </div>

                                <div className="form-actions">
                                    <button 
                                        className="btn btn-primary"
                                        onClick={handleSubmitReview}
                                    >
                                        Submit Review
                                    </button>
                                    <button 
                                        className="btn btn-secondary"
                                        onClick={() => {
                                            setShowReviewForm(false);
                                            setReviewForm({ rating: 5, comment: "" });
                                        }}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        )}

                        {reviews.length === 0 ? (
                            <div className="no-reviews">
                                No reviews yet. Be the first to review this product!
                            </div>
                        ) : (
                            <div className="reviews-list">
                                {reviews.map(review => (
                                    <div key={review.reviewId} className="review-card">
                                        <div className="review-header">
                                            <div>
                                                <div className="reviewer-name">{review.customerName}</div>
                                                <div className="review-date">
                                                    {new Date(review.reviewDate).toLocaleDateString('en-US', {
                                                        year: 'numeric',
                                                        month: 'long',
                                                        day: 'numeric'
                                                    })}
                                                </div>
                                            </div>
                                            <div className="review-rating">
                                                {renderStars(review.rating)}
                                            </div>
                                        </div>
                                        <div className="review-comment">{review.comment}</div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
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
