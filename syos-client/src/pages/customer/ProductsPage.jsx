import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import ProductCard from "../../components/customer/ProductCard";
import { apiGetCustomerProducts, apiGetCategories, apiAddToCart, apiAddToWishlist } from "../../services/api";

export default function ProductsPage({ user, onLogout }) {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("ALL");
    const [categoryId, setCategoryId] = useState(null);
    const [categories, setCategories] = useState([]);
    const [notification, setNotification] = useState(null);
    const navigate = useNavigate();

    // Load products on mount and when category changes
    useEffect(() => {
        loadProducts();
    }, [categoryId]);

    // Load categories on mount
    useEffect(() => {
        loadCategories();
    }, []);

    // Debounce search - wait 500ms after user stops typing
    useEffect(() => {
        const timeoutId = setTimeout(() => {
            loadProducts();
        }, 500);
        
        return () => clearTimeout(timeoutId);
    }, [searchQuery]);

    const loadCategories = async () => {
        try {
            console.log("📂 Fetching categories with names...");
            const result = await apiGetCategories();
            console.log("📦 Categories API response:", result);
            
            if (result.success && result.data) {
                const categoryList = result.data.categories || result.data || [];
                const formattedCategories = categoryList.map(cat => ({
                    id: cat.categoryId || cat.id,
                    name: cat.categoryName || cat.name
                }));
                
                console.log(`✅ Loaded ${formattedCategories.length} categories:`, formattedCategories);
                setCategories(formattedCategories);
            } else {
                console.warn("⚠️ Categories API failed, falling back to product extraction");
                // Fallback: extract from products
                const allResult = await apiGetCustomerProducts();
                const allProducts = allResult?.data?.products || allResult?.data || [];
                const categoryIds = new Set();
                allProducts.forEach(p => {
                    if (p.categoryId) categoryIds.add(p.categoryId);
                });
                const uniqueCategories = Array.from(categoryIds)
                    .sort((a, b) => a - b)
                    .map(id => ({ id, name: `Category ${id}` }));
                setCategories(uniqueCategories);
            }
        } catch (error) {
            console.error("❌ Error loading categories:", error);
        }
    };

    const loadProducts = async () => {
        setLoading(true);
        try {
            // Build API params for server-side filtering
            const params = {};
            if (categoryId) params.category = categoryId;
            if (searchQuery) params.search = searchQuery;
            
            console.log("🔍 Loading products with params:", params);
            const result = await apiGetCustomerProducts(params);
            console.log("📦 Products API Response:", result);
            
            // Backend returns: { success: true, data: { products: [...], pagination: {...} } }
            const productList = result?.data?.products || result?.data || [];
            
            // Log apple001 specifically to debug
            const appleProduct = productList.find(p => p.productCode === 'apple001');
            if (appleProduct) {
                console.log('🍎 Apple001 product details:', {
                    availableQuantity: appleProduct.availableQuantity,
                    inStock: appleProduct.inStock,
                    name: appleProduct.name
                });
            } else {
                console.log('🍎 Apple001 not found in product list');
            }
            
            if (result.success && Array.isArray(productList)) {
                setProducts(productList);
                setFilteredProducts(productList);
                console.log(`✅ Loaded ${productList.length} products`);
            } else {
                console.error("Invalid product data:", result);
                setProducts([]);
                setFilteredProducts([]);
                showNotification("Failed to load products", "error");
            }
        } catch (error) {
            console.error("Error loading products:", error);
            setProducts([]);
            setFilteredProducts([]);
            showNotification("Error loading products", "error");
        }
        setLoading(false);
    };

    const handleCategoryChange = (category, catId) => {
        console.log(`🏷️ Category changed to: ${category} (ID: ${catId})`);
        setSelectedCategory(category);
        setCategoryId(catId);
    };

    const handleAddToCart = async (product) => {
        const result = await apiAddToCart(product.productCode, 1);
        if (result.success) {
            showNotification(`${product.name} added to cart!`, "success");
        } else {
            showNotification(result.message || "Failed to add to cart", "error");
        }
    };

    const handleAddToWishlist = async (product) => {
        const result = await apiAddToWishlist(product.productCode);
        if (result.success) {
            showNotification(`${product.name} added to wishlist!`, "success");
        } else {
            showNotification(result.message || "Failed to add to wishlist", "error");
        }
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    return (
        <>
            <style>{`
                .products-page {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                }
                .products-container {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 32px 20px;
                }
                .page-header {
                    margin-bottom: 32px;
                    text-align: center;
                }
                .page-title {
                    font-size: 42px;
                    font-weight: 800;
                    background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    background-clip: text;
                    margin: 0 0 8px 0;
                }
                .page-subtitle {
                    font-size: 18px;
                    color: #6b7280;
                    font-weight: 500;
                }
                .filters-section {
                    background: white;
                    padding: 24px;
                    border-radius: 16px;
                    box-shadow: 0 4px 20px rgba(34, 197, 94, 0.15);
                    margin-bottom: 32px;
                    display: flex;
                    gap: 16px;
                    flex-wrap: wrap;
                    align-items: center;
                    border: 1px solid rgba(34, 197, 94, 0.1);
                }
                .search-box {
                    flex: 1;
                    min-width: 250px;
                }
                .search-input {
                    width: 100%;
                    padding: 14px 20px 14px 44px;
                    border: 2px solid #e5e7eb;
                    border-radius: 12px;
                    font-size: 15px;
                    transition: all 0.2s;
                    background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="%2322c55e" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>') no-repeat 16px center;
                }
                .search-input:focus {
                    outline: none;
                    border-color: #22c55e;
                    box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
                }
                .category-filters {
                    display: flex;
                    gap: 8px;
                    flex-wrap: wrap;
                }
                .category-btn {
                    padding: 10px 20px;
                    border: 2px solid #e5e7eb;
                    background: white;
                    border-radius: 10px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    font-size: 14px;
                }
                .category-btn:hover {
                    border-color: #22c55e;
                    color: #22c55e;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.2);
                }
                .category-btn.active {
                    background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
                    color: white;
                    border-color: transparent;
                    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
                }
                .products-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                    gap: 24px;
                }
                .loading-spinner {
                    text-align: center;
                    padding: 60px 20px;
                    font-size: 18px;
                    color: #6b7280;
                }
                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                }
                .empty-state-icon {
                    font-size: 64px;
                    margin-bottom: 16px;
                }
                .empty-state-title {
                    font-size: 24px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .empty-state-text {
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
                .notification.success {
                    background: #10b981;
                    color: white;
                }
                .notification.error {
                    background: #ef4444;
                    color: white;
                }
                @keyframes slideIn {
                    from {
                        transform: translateX(400px);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            `}</style>

            <div className="products-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="products-container">
                    <div className="page-header">
                        <h1 className="page-title">Discover Amazing Products</h1>
                        <p className="page-subtitle">Shop from our curated collection with exclusive deals</p>
                    </div>

                    <div className="filters-section">
                        <div className="search-box">
                            <input 
                                type="text"
                                className="search-input"
                                placeholder="Search for products..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                        </div>

                        <div className="category-filters">
                            <button 
                                className={`category-btn ${selectedCategory === "ALL" ? "active" : ""}`}
                                onClick={() => handleCategoryChange("ALL", null)}
                            >
                                All Products
                            </button>
                            {categories.length === 0 && !loading && (
                                <span style={{ color: '#9ca3af', fontSize: '14px', padding: '10px' }}>
                                    Loading categories...
                                </span>
                            )}
                            {categories.map(category => (
                                <button 
                                    key={category.id}
                                    className={`category-btn ${selectedCategory === category.name ? "active" : ""}`}
                                    onClick={() => handleCategoryChange(category.name, category.id)}
                                >
                                    {category.name}
                                </button>
                            ))}
                        </div>
                    </div>

                    {loading ? (
                        <div className="loading-spinner">
                            ⏳ Loading products...
                        </div>
                    ) : filteredProducts.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">📦</div>
                            <h2 className="empty-state-title">No products found</h2>
                            <p className="empty-state-text">
                                {searchQuery || selectedCategory !== "ALL" 
                                    ? "Try adjusting your filters" 
                                    : "Products will appear here once they're added"}
                            </p>
                        </div>
                    ) : (
                        <div className="products-grid">
                            {filteredProducts.map(product => (
                                <ProductCard 
                                    key={product.productCode}
                                    product={product}
                                    onAddToCart={handleAddToCart}
                                    onAddToWishlist={handleAddToWishlist}
                                    onViewDetails={() => navigate(`/customer/products/${product.productCode}`)}
                                />
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
