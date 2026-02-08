import React from "react";

export default function ProductCard({ product, onAddToCart, onAddToWishlist, onViewDetails }) {
    const hasDiscount = product.discountPercentage > 0;
    const displayPrice = hasDiscount ? product.discountedPrice : product.originalPrice;
    const isInStock = product.inStock === true && product.availableQuantity > 0;

    return (
        <>
            <style>{`
                .product-card {
                    background: white;
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                    transition: all 0.3s ease;
                    cursor: pointer;
                    height: 100%;
                    display: flex;
                    flex-direction: column;
                    border: 1px solid rgba(102, 126, 234, 0.1);
                }
                .product-card:hover {
                    transform: translateY(-8px);
                    box-shadow: 0 12px 24px rgba(102, 126, 234, 0.25);
                    border-color: rgba(102, 126, 234, 0.3);
                }
                .product-image-container {
                    position: relative;
                    width: 100%;
                    padding-top: 100%;
                    background: #f5f5f5;
                    overflow: hidden;
                }
                .product-image {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }
                .product-badge {
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
                .out-of-stock-badge {
                    background: #6b7280;
                }
                .wishlist-btn {
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    background: white;
                    border: none;
                    width: 36px;
                    height: 36px;
                    border-radius: 50%;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 18px;
                    transition: transform 0.2s;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                }
                .wishlist-btn:hover {
                    transform: scale(1.1);
                }
                .product-details {
                    padding: 16px;
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                }
                .product-category {
                    color: #22c55e;
                    font-size: 12px;
                    font-weight: 600;
                    text-transform: uppercase;
                    margin-bottom: 4px;
                }
                .product-name {
                    font-size: 16px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                    min-height: 40px;
                }
                .product-price-container {
                    margin-bottom: 12px;
                }
                .product-price {
                    font-size: 20px;
                    font-weight: 700;
                    color: #1f2937;
                }
                .product-original-price {
                    font-size: 14px;
                    color: #9ca3af;
                    text-decoration: line-through;
                    margin-left: 8px;
                }
                .product-discount {
                    display: inline-block;
                    background: #fef3c7;
                    color: #f59e0b;
                    padding: 2px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                    margin-left: 8px;
                }
                .product-stock {
                    font-size: 13px;
                    color: #10b981;
                    margin-bottom: 12px;
                }
                .out-of-stock-text {
                    color: #ef4444;
                }
                .product-actions {
                    display: flex;
                    gap: 8px;
                    margin-top: auto;
                }
                .btn-add-cart {
                    flex: 1;
                    background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
                    color: white;
                    border: none;
                    padding: 12px 16px;
                    border-radius: 10px;
                    font-weight: 700;
                    cursor: pointer;
                    transition: all 0.2s;
                    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
                }
                .btn-add-cart:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(34, 197, 94, 0.4);
                }
                .btn-add-cart:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                    box-shadow: none;
                    transform: none;
                }
                .btn-view-details {
                    background: #f3f4f6;
                    color: #22c55e;
                    border: none;
                    padding: 10px 16px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .btn-view-details:hover {
                    background: #e5e7eb;
                }
            `}</style>

            <div className="product-card">
                <div className="product-image-container" onClick={onViewDetails}>
                    <img 
                        src={product.imageUrl || "/placeholder-product.jpg"} 
                        alt={product.name}
                        className="product-image"
                        onError={(e) => e.target.src = "/placeholder-product.jpg"}
                    />
                    {hasDiscount && isInStock && (
                        <div className="product-badge">
                            -{product.discountPercentage}% OFF
                        </div>
                    )}
                    {!isInStock && (
                        <div className="product-badge out-of-stock-badge">
                            OUT OF STOCK
                        </div>
                    )}
                    <button 
                        className="wishlist-btn" 
                        onClick={(e) => {
                            e.stopPropagation();
                            onAddToWishlist?.(product);
                        }}
                        title="Add to Wishlist"
                    >
                        ❤️
                    </button>
                </div>

                <div className="product-details">
                    {product.categoryName && (
                        <div className="product-category">{product.categoryName}</div>
                    )}
                    
                    <h3 className="product-name">{product.name}</h3>
                    
                    <div className="product-price-container">
                        <span className="product-price">
                            LKR {displayPrice?.toFixed(2)}
                        </span>
                        {hasDiscount && (
                            <>
                                <span className="product-original-price">
                                    LKR {product.originalPrice?.toFixed(2)}
                                </span>
                                <span className="product-discount">
                                    SAVE {product.discountPercentage}%
                                </span>
                            </>
                        )}
                    </div>

                    <div className={`product-stock ${!isInStock ? 'out-of-stock-text' : ''}`}>
                        {isInStock ? `${product.availableQuantity} in stock` : 'Out of Stock'}
                    </div>

                    <div className="product-actions">
                        <button 
                            className="btn-add-cart"
                            onClick={(e) => {
                                e.stopPropagation();
                                onAddToCart?.(product);
                            }}
                            disabled={!isInStock}
                        >
                            {isInStock ? '🛒 Add to Cart' : 'Unavailable'}
                        </button>
                        <button 
                            className="btn-view-details"
                            onClick={onViewDetails}
                        >
                            View
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}
