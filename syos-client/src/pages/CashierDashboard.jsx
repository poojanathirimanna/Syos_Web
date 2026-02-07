import { useState, useEffect } from "react";
import Sidebar from "../components/common/Sidebar";
import Header from "../components/common/Header";
import { apiGetProducts, apiCreateBill, apiGetCashierBills, apiGetCategories, apiGetBillDetails, apiGetCashierPromotions } from "../services/api";
import syosLogo from "../assets/syos-logo-text.png";

export default function CashierDashboard({ user, onLogout }) {
    const [activeMenu, setActiveMenu] = useState("pos");
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [cart, setCart] = useState([]);
    const [showCheckoutModal, setShowCheckoutModal] = useState(false);
    const [bills, setBills] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("all");
    const [categories, setCategories] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [selectedBill, setSelectedBill] = useState(null);
    const [showBillModal, setShowBillModal] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);
    const [promotions, setPromotions] = useState([]);

    const showNotification = (message, type = 'info') => {
        const id = Date.now();
        setNotifications(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setNotifications(prev => prev.filter(n => n.id !== id));
        }, 3000);
    };

    const menuItems = [
        { id: "pos", icon: "🛒", label: "Point of Sale" },
        { id: "transactions", icon: "💰", label: "Transactions" },
        { id: "promotions", icon: "🏷️", label: "Promotions" },
        { id: "reports", icon: "📊", label: "Reports" },
    ];

    useEffect(() => {
        loadProducts();
        loadCategories();
        loadPromotions();
    }, []);

    useEffect(() => {
        if (activeMenu === "transactions") {
            loadBills();
        } else if (activeMenu === "pos") {
            // Reload promotions when returning to POS to get latest discounts
            loadPromotions();
        }
    }, [activeMenu]);

    const loadCategories = async () => {
        try {
            const response = await apiGetCategories();
            if (response.success) {
                setCategories(response.data || []);
            }
        } catch (err) {
            console.error("Error loading categories:", err);
        }
    };

    const loadPromotions = async () => {
        try {
            const response = await apiGetCashierPromotions();
            if (response.success) {
                setPromotions(response.data || []);
                console.log('🎁 Loaded promotions:', response.data);
            }
        } catch (err) {
            console.error("Error loading promotions:", err);
        }
    };

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await apiGetProducts();
            console.log('🛒 POS: API Response:', response);
            if (response.success) {
                // Products are nested inside data.products
                const productsData = response.data?.products || response.data || [];
                console.log('🛒 POS: Products data:', productsData);
                const productsArray = Array.isArray(productsData) ? productsData : [];
                console.log('🛒 POS: Setting products array:', productsArray);
                setProducts(productsArray);
            } else {
                setError(response.message || "Failed to load products");
                setProducts([]);
            }
        } catch (err) {
            setError("Error loading products: " + err.message);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const loadBills = async () => {
        try {
            const response = await apiGetCashierBills();
            if (response.success) {
                // Ensure data is an array
                const billsData = Array.isArray(response.data) ? response.data : [];
                setBills(billsData);
            } else {
                setBills([]);
            }
        } catch (err) {
            console.error("Error loading bills:", err);
            setBills([]);
        }
    };

    const viewBillDetails = async (billNumber) => {
        try {
            const response = await apiGetBillDetails(billNumber);
            console.log('📄 Bill Details Response:', response);
            if (response.success) {
                console.log('📄 Bill Data:', response.data);
                setSelectedBill(response.data);
                setShowBillModal(true);
            } else {
                showNotification('Failed to load bill details', 'error');
            }
        } catch (err) {
            console.error('Error loading bill:', err);
            showNotification('Error loading bill details', 'error');
        }
    };

    const downloadBillReceipt = () => {
        if (!selectedBill) return;
        
        const bill = selectedBill;
        const receiptContent = `
========================================
           SYOS SUPERMARKET
          Receipt / Invoice
========================================
Bill Number: ${bill.billNumber}
Date: ${bill.billDate || 'N/A'}
Cashier: ${bill.cashierName || 'N/A'}
========================================

ITEMS:
${bill.items?.map(item => 
    `${item.productName || 'Product'}\n  Qty: ${item.quantity} x Rs. ${Number(item.unitPrice || 0).toFixed(2)} = Rs. ${Number(item.totalPrice || 0).toFixed(2)}`
).join('\n\n') || 'No items'}

========================================
Subtotal: Rs. ${Number(bill.subtotal || 0).toFixed(2)}
Total Amount: Rs. ${Number(bill.totalAmount || 0).toFixed(2)}
Payment Method: ${(bill.paymentMethod || 'Cash').toUpperCase()}
Amount Paid: Rs. ${Number(bill.amountPaid || 0).toFixed(2)}
Change: Rs. ${Number(bill.changeAmount || 0).toFixed(2)}
========================================
Status: ${bill.status || 'Completed'}

     Thank you for shopping with us!
========================================
        `;

        const blob = new Blob([receiptContent], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Receipt_${bill.billNumber}.txt`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        showNotification('Receipt downloaded successfully', 'success');
    };

    const addToCart = (product) => {
        // Check if product has stock on shelf
        if (!product.shelfQuantity || product.shelfQuantity <= 0) {
            showNotification(`${product.name} is out of stock on the shelf!`, 'error');
            return;
        }

        console.log('🛒 Adding to cart:', product.name, 'Product Code:', product.productCode);
        console.log('📦 Available promotions:', promotions.length);
        console.log('🔍 All promotions:', promotions);

        // Find active promotion for this product from promotions list
        const activePromotion = promotions.find(promo => 
            promo.productCode === product.productCode &&
            isDiscountActive(promo.discountStartDate, promo.discountEndDate)
        );

        console.log('🎯 Active promotion found:', activePromotion);

        // Check if product has discount in its own data (fallback)
        const productHasDiscount = product.discountPercentage > 0 && 
            isDiscountActive(product.discountStartDate, product.discountEndDate);

        console.log('💰 Product has discount:', productHasDiscount, 'Discount %:', product.discountPercentage);

        // Use promotion discount or product discount
        const discountPercentage = activePromotion?.discountPercentage || 
            (productHasDiscount ? product.discountPercentage : 0);
        
        console.log('✅ Final discount percentage:', discountPercentage);
        
        const hasActiveDiscount = discountPercentage > 0;

        // Calculate the final price (with discount if applicable)
        const finalPrice = hasActiveDiscount 
            ? product.unitPrice - (product.unitPrice * discountPercentage / 100)
            : product.unitPrice;

        const existingItem = cart.find(item => item.productCode === product.productCode);
        if (existingItem) {
            // Check if adding more would exceed shelf stock
            if (existingItem.quantity >= product.shelfQuantity) {
                showNotification(`Only ${product.shelfQuantity} units available on shelf!`, 'warning');
                return;
            }
            setCart(cart.map(item =>
                item.productCode === product.productCode
                    ? { ...item, quantity: item.quantity + 1 }
                    : item
            ));
            // Decrease shelf quantity in products state
            setProducts(products.map(p =>
                p.productCode === product.productCode
                    ? { ...p, shelfQuantity: p.shelfQuantity - 1 }
                    : p
            ));
            
            // Show discount notification if applicable
            if (hasActiveDiscount) {
                showNotification(`${discountPercentage}% discount applied!`, 'success');
            }
        } else {
            const cartItem = {
                ...product,
                quantity: 1,
                originalPrice: product.unitPrice,
                finalPrice: finalPrice,
                discountApplied: hasActiveDiscount ? discountPercentage : 0,
                promotionType: activePromotion?.type || null
            };
            console.log('🎁 Created cart item:', cartItem);
            console.log('   Original Price:', product.unitPrice);
            console.log('   Final Price:', finalPrice);
            console.log('   Discount Applied:', hasActiveDiscount ? discountPercentage : 0);
            
            setCart([...cart, cartItem]);
            // Decrease shelf quantity in products state
            setProducts(products.map(p =>
                p.productCode === product.productCode
                    ? { ...p, shelfQuantity: p.shelfQuantity - 1 }
                    : p
            ));
            
            // Show discount notification if applicable
            if (hasActiveDiscount) {
                const promoType = activePromotion?.type === 'BATCH' ? 'Clearance Sale' : 'Promotion';
                showNotification(`${product.name} added with ${discountPercentage}% ${promoType} discount!`, 'success');
            }
        }
    };

    const isDiscountActive = (startDate, endDate) => {
        // If dates are null or undefined, consider discount as active
        // (backend should only return active discounts)
        if (!startDate || !endDate) {
            return true;
        }
        const now = new Date();
        const start = new Date(startDate);
        const end = new Date(endDate);
        return now >= start && now <= end;
    };

    const updateQuantity = (productCode, newQuantity) => {
        const cartItem = cart.find(item => item.productCode === productCode);
        if (!cartItem) return;

        const quantityDifference = newQuantity - cartItem.quantity;
        const product = products.find(p => p.productCode === productCode);

        if (newQuantity <= 0) {
            // Restore the quantity back to products
            setProducts(products.map(p =>
                p.productCode === productCode
                    ? { ...p, shelfQuantity: p.shelfQuantity + cartItem.quantity }
                    : p
            ));
            setCart(cart.filter(item => item.productCode !== productCode));
        } else {
            // Check if new quantity exceeds available stock (product shelf + current cart quantity)
            const availableStock = product.shelfQuantity + cartItem.quantity;
            if (newQuantity > availableStock) {
                showNotification(`Only ${availableStock} units available on shelf!`, 'warning');
                return;
            }
            // Update cart and products
            setCart(cart.map(item =>
                item.productCode === productCode
                    ? { ...item, quantity: newQuantity }
                    : item
            ));
            setProducts(products.map(p =>
                p.productCode === productCode
                    ? { ...p, shelfQuantity: p.shelfQuantity - quantityDifference }
                    : p
            ));
        }
    };

    const removeFromCart = (productCode) => {
        const cartItem = cart.find(item => item.productCode === productCode);
        if (cartItem) {
            // Restore the quantity back to products
            setProducts(products.map(p =>
                p.productCode === productCode
                    ? { ...p, shelfQuantity: p.shelfQuantity + cartItem.quantity }
                    : p
            ));
        }
        setCart(cart.filter(item => item.productCode !== productCode));
    };

    const getCartTotal = () => {
        return cart.reduce((total, item) => {
            const price = item.finalPrice || item.unitPrice;
            return total + (price * item.quantity);
        }, 0);
    };

    const getOriginalTotal = () => {
        return cart.reduce((total, item) => {
            const price = item.originalPrice || item.unitPrice;
            return total + (price * item.quantity);
        }, 0);
    };

    const getTotalSavings = () => {
        return getOriginalTotal() - getCartTotal();
    };

    const handleCheckout = () => {
        if (cart.length === 0) return;
        setShowCheckoutModal(true);
    };

    const clearCart = () => {
        // Restore all quantities back to products
        const updatedProducts = [...products];
        cart.forEach(cartItem => {
            const productIndex = updatedProducts.findIndex(p => p.productCode === cartItem.productCode);
            if (productIndex !== -1) {
                updatedProducts[productIndex] = {
                    ...updatedProducts[productIndex],
                    shelfQuantity: updatedProducts[productIndex].shelfQuantity + cartItem.quantity
                };
            }
        });
        setProducts(updatedProducts);
        setCart([]);
    };

    const filteredProducts = Array.isArray(products) 
        ? products.filter(product => {
            const matchesSearch = product.name?.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesCategory = selectedCategory === "all" || product.categoryId === selectedCategory;
            return matchesSearch && matchesCategory;
        })
        : [];

    console.log('🛒 POS: products state:', products);
    console.log('🛒 POS: filteredProducts:', filteredProducts);
    console.log('🛒 POS: searchQuery:', searchQuery);

    return (
        <>
            <style>{`
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                .dashboard-container {
                    display: flex;
                    min-height: 100vh;
                    background: #f5f5f5;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                }
                
                .main-content {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                }
                
                .content-area {
                    flex: 1;
                    padding: 32px;
                    overflow-y: auto;
                }
                
                .section-title {
                    font-size: 24px;
                    color: #333;
                    font-weight: 700;
                    margin-bottom: 24px;
                }
                
                .quick-stats {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                    gap: 20px;
                    margin-bottom: 32px;
                }
                
                .stat-card {
                    background: white;
                    padding: 24px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    border-left: 4px solid #66bb6a;
                }
                
                .stat-label {
                    color: #888;
                    font-size: 13px;
                    margin-bottom: 8px;
                    text-transform: uppercase;
                    font-weight: 600;
                }
                
                .stat-value {
                    font-size: 32px;
                    font-weight: 700;
                    color: #333;
                }
                
                .pos-section {
                    background: white;
                    padding: 32px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }
                
                .pos-grid {
                    display: grid;
                    grid-template-columns: 2fr 1fr;
                    gap: 24px;
                }
                
                .products-section {
                    background: #f9f9f9;
                    padding: 24px;
                    border-radius: 8px;
                }
                
                .cart-section {
                    background: #f9f9f9;
                    padding: 24px;
                    border-radius: 8px;
                }
                
                .section-subtitle {
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 16px;
                }
                
                .product-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
                    gap: 12px;
                }
                
                .product-card {
                    background: white;
                    padding: 16px;
                    border-radius: 8px;
                    text-align: center;
                    cursor: pointer;
                    transition: all 0.2s ease;
                    border: 2px solid #e0e0e0;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                    display: flex;
                    flex-direction: column;
                    position: relative;
                }
                
                .product-card:hover {
                    border-color: #52B788;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 8px rgba(82, 183, 136, 0.2);
                }

                .product-card.out-of-stock {
                    cursor: not-allowed;
                }

                .product-card.out-of-stock:hover {
                    transform: none;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                    border-color: #e0e0e0;
                }

                .product-card.low-stock {
                    border-color: #ff9800;
                    background: linear-gradient(135deg, #fff3e0 0%, #ffffff 100%);
                }

                .product-discount-badge {
                    position: absolute;
                    top: 8px;
                    right: 8px;
                    background: linear-gradient(135deg, #FF6B6B 0%, #E74C3C 100%);
                    color: white;
                    font-size: 11px;
                    font-weight: 700;
                    padding: 6px 12px;
                    border-radius: 20px;
                    text-align: center;
                    z-index: 3;
                    box-shadow: 0 3px 10px rgba(255, 107, 107, 0.4);
                }

                .low-stock-badge {
                    position: absolute;
                    top: 8px;
                    left: 8px;
                    right: 8px;
                    background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
                    color: white;
                    font-size: 10px;
                    font-weight: 700;
                    padding: 4px 8px;
                    border-radius: 6px;
                    text-align: center;
                    z-index: 2;
                    box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
                }

                .out-of-stock-badge {
                    position: absolute;
                    top: 8px;
                    left: 8px;
                    right: 8px;
                    background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
                    color: white;
                    font-size: 10px;
                    font-weight: 700;
                    padding: 4px 8px;
                    border-radius: 6px;
                    text-align: center;
                    z-index: 2;
                    box-shadow: 0 2px 8px rgba(244, 67, 54, 0.3);
                }
                
                .product-image {
                    width: 100%;
                    height: 120px;
                    object-fit: cover;
                    border-radius: 8px;
                    margin-bottom: 8px;
                    background: #f5f5f5;
                }

                .product-icon {
                    font-size: 48px;
                    margin-bottom: 8px;
                    height: 120px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: #f5f5f5;
                    border-radius: 8px;
                }
                
                .product-name {
                    font-size: 14px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 4px;
                    min-height: 40px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                
                .product-price {
                    font-size: 16px;
                    color: #52B788;
                    font-weight: 700;
                }

                .product-stock {
                    font-size: 11px;
                    color: #888;
                    margin-top: 4px;
                }

                .category-filters {
                    display: flex;
                    gap: 8px;
                    margin-bottom: 16px;
                    flex-wrap: wrap;
                }

                .category-btn {
                    padding: 8px 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 20px;
                    cursor: pointer;
                    font-size: 13px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .category-btn:hover {
                    border-color: #52B788;
                    color: #52B788;
                }

                .category-btn.active {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border-color: #52B788;
                }

                .search-box-pos {
                    margin-bottom: 20px;
                }

                .search-input-pos {
                    width: 100%;
                    max-width: 400px;
                    padding: 12px 16px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                }

                .search-input-pos:focus {
                    outline: none;
                    border-color: #52B788;
                }

                .cart-items {
                    max-height: 400px;
                    overflow-y: auto;
                    margin-bottom: 20px;
                }

                .cart-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 12px;
                    background: white;
                    border-radius: 8px;
                    margin-bottom: 8px;
                }

                .cart-item-info {
                    flex: 1;
                }

                .cart-item-name {
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 4px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    flex-wrap: wrap;
                }

                .cart-discount-badge {
                    background: #FF6B6B;
                    color: white;
                    font-size: 10px;
                    padding: 2px 6px;
                    border-radius: 4px;
                    font-weight: 700;
                }

                .cart-item-price {
                    color: #52B788;
                    font-size: 14px;
                }

                .cart-savings {
                    padding: 12px 16px;
                    background: linear-gradient(135deg, #FFEAA7 0%, #FFD700 100%);
                    border-radius: 8px;
                    margin-bottom: 8px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    font-weight: 600;
                    color: #333;
                    box-shadow: 0 2px 8px rgba(255, 215, 0, 0.3);
                }

                .savings-amount {
                    font-size: 18px;
                    font-weight: 700;
                    color: #E67E22;
                }

                .cart-item-controls {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .qty-btn {
                    width: 28px;
                    height: 28px;
                    border: 2px solid #52B788;
                    background: white;
                    color: #52B788;
                    border-radius: 4px;
                    cursor: pointer;
                    font-weight: 700;
                    transition: all 0.2s;
                }

                .qty-btn:hover {
                    background: #52B788;
                    color: white;
                }

                .qty-display {
                    min-width: 30px;
                    text-align: center;
                    font-weight: 600;
                }

                .remove-btn {
                    background: #f44336;
                    color: white;
                    border: none;
                    padding: 6px 10px;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 12px;
                }

                .cart-total {
                    padding: 16px;
                    background: white;
                    border-radius: 8px;
                    margin-bottom: 12px;
                }

                .cart-total-label {
                    font-size: 14px;
                    color: #666;
                    margin-bottom: 8px;
                }

                .cart-total-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #52B788;
                }
                
                .cart-empty {
                    text-align: center;
                    padding: 40px;
                    color: #999;
                }
                
                .checkout-button {
                    width: 100%;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 16px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 16px;
                    cursor: pointer;
                    transition: all 0.3s;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }
                
                .checkout-button:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .checkout-button:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .clear-cart-btn {
                    width: 100%;
                    background: white;
                    color: #f44336;
                    border: 2px solid #f44336;
                    padding: 12px;
                    border-radius: 8px;
                    font-weight: 600;
                    font-size: 14px;
                    cursor: pointer;
                    margin-top: 8px;
                    transition: all 0.2s;
                }

                .clear-cart-btn:hover {
                    background: #f44336;
                    color: white;
                }
                
                .logout-button {
                    background: #f44336;
                    color: white;
                    border: none;
                    padding: 8px 16px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-weight: 600;
                    font-size: 14px;
                    transition: background 0.2s ease;
                }
                
                .logout-button:hover {
                    background: #d32f2f;
                }
                
                .transactions-table {
                    background: white;
                    border-radius: 8px;
                    overflow: hidden;
                }
                
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                
                th {
                    background: #f5f5f5;
                    padding: 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #666;
                    font-size: 13px;
                    text-transform: uppercase;
                }
                
                td {
                    padding: 16px;
                    border-top: 1px solid #e0e0e0;
                    color: #333;
                }
                
                .badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: 600;
                }
                
                .badge.success {
                    background: #e8f5e9;
                    color: #4caf50;
                }
                
                .badge.pending {
                    background: #fff3e0;
                    color: #ff9800;
                }

                .view-bill-btn {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 8px 16px;
                    border-radius: 6px;
                    font-size: 13px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .view-bill-btn:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 4px 8px rgba(82, 183, 136, 0.3);
                }

                .pagination {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    gap: 8px;
                    margin-top: 24px;
                    padding: 16px;
                }

                .pagination-btn {
                    padding: 8px 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    color: #333;
                    border-radius: 6px;
                    cursor: pointer;
                    font-weight: 600;
                    font-size: 14px;
                    transition: all 0.2s;
                }

                .pagination-btn:hover:not(:disabled) {
                    border-color: #52B788;
                    color: #52B788;
                }

                .pagination-btn:disabled {
                    opacity: 0.4;
                    cursor: not-allowed;
                }

                .pagination-btn.active {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border-color: #52B788;
                }

                .pagination-info {
                    color: #666;
                    font-size: 14px;
                    font-weight: 500;
                }

                .toast-container {
                    position: fixed;
                    top: 80px;
                    right: 20px;
                    z-index: 10000;
                    display: flex;
                    flex-direction: column;
                    gap: 12px;
                    pointer-events: none;
                }

                .toast {
                    background: white;
                    padding: 16px 20px;
                    border-radius: 12px;
                    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    min-width: 300px;
                    max-width: 400px;
                    pointer-events: auto;
                    animation: slideIn 0.3s ease-out;
                    border-left: 4px solid;
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

                .toast.success {
                    border-left-color: #4caf50;
                }

                .toast.error {
                    border-left-color: #f44336;
                }

                .toast.warning {
                    border-left-color: #ff9800;
                }

                .toast.info {
                    border-left-color: #2196f3;
                }

                .toast-icon {
                    font-size: 24px;
                    flex-shrink: 0;
                }

                .toast-message {
                    flex: 1;
                    color: #333;
                    font-size: 14px;
                    font-weight: 500;
                    line-height: 1.4;
                }
            `}</style>

            <div className="dashboard-container">
                {/* Toast Notifications */}
                <div className="toast-container">
                    {notifications.map(notif => (
                        <div key={notif.id} className={`toast ${notif.type}`}>
                            <span className="toast-icon">
                                {notif.type === 'success' && '✅'}
                                {notif.type === 'error' && '❌'}
                                {notif.type === 'warning' && '⚠️'}
                                {notif.type === 'info' && 'ℹ️'}
                            </span>
                            <span className="toast-message">{notif.message}</span>
                        </div>
                    ))}
                </div>

                {/* Sidebar */}
                <Sidebar
                    logo={syosLogo}
                    menuItems={menuItems}
                    activeMenu={activeMenu}
                    onMenuClick={setActiveMenu}
                    isOpen={sidebarOpen}
                    accentColor="#66bb6a"
                />

                {/* Main Content */}
                <main className="main-content">
                    <Header
                        user={user}
                        onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
                        onLogout={onLogout}
                        showNotifications={false}
                    />

                    {/* Content Area */}
                    <div className="content-area">
                        {activeMenu === "pos" && (
                            <>
                                <h1 className="section-title">Point of Sale</h1>

                                {error && (
                                    <div style={{
                                        padding: '16px',
                                        marginBottom: '20px',
                                        background: 'linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)',
                                        color: '#991b1b',
                                        borderRadius: '12px',
                                        fontWeight: 600
                                    }}>
                                        ⚠️ {error}
                                    </div>
                                )}

                                {loading ? (
                                    <div style={{ textAlign: 'center', padding: '60px', color: '#888' }}>
                                        <div style={{ fontSize: '24px', marginBottom: '12px' }}>⏳</div>
                                        Loading products...
                                    </div>
                                ) : (
                                    <>
                                        {/* POS Interface */}
                                        <div className="pos-section">
                                            <div className="pos-grid">
                                                <div className="products-section">
                                                    <h3 className="section-subtitle">Products</h3>
                                                    <div className="search-box-pos">
                                                        <input
                                                            type="text"
                                                            className="search-input-pos"
                                                            placeholder="🔍 Search products..."
                                                            value={searchQuery}
                                                            onChange={(e) => setSearchQuery(e.target.value)}
                                                        />
                                                    </div>
                                                    <div className="category-filters">
                                                        <button
                                                            className={`category-btn ${selectedCategory === "all" ? "active" : ""}`}
                                                            onClick={() => setSelectedCategory("all")}
                                                        >
                                                            All Products
                                                        </button>
                                                        {categories.map(category => (
                                                            <button
                                                                key={category.categoryId}
                                                                className={`category-btn ${selectedCategory === category.categoryId ? "active" : ""}`}
                                                                onClick={() => setSelectedCategory(category.categoryId)}
                                                            >
                                                                {category.categoryName}
                                                            </button>
                                                        ))}
                                                    </div>
                                                    <div className="product-grid">
                                                        {filteredProducts.map(product => {
                                                            const isLowStock = product.shelfQuantity > 0 && product.shelfQuantity < 10;
                                                            const isOutOfStock = !product.shelfQuantity || product.shelfQuantity <= 0;
                                                            
                                                            // Check promotions array first
                                                            const activePromotion = promotions.find(promo => 
                                                                promo.productCode === product.productCode &&
                                                                isDiscountActive(promo.discountStartDate, promo.discountEndDate)
                                                            );
                                                            
                                                            // Check product's own discount data (fallback)
                                                            const productHasDiscount = product.discountPercentage > 0 && 
                                                                isDiscountActive(product.discountStartDate, product.discountEndDate);
                                                            
                                                            const discountPercentage = activePromotion?.discountPercentage || 
                                                                (productHasDiscount ? product.discountPercentage : 0);
                                                            
                                                            const hasActiveDiscount = discountPercentage > 0;
                                                            
                                                            const discountedPrice = hasActiveDiscount 
                                                                ? product.unitPrice - (product.unitPrice * discountPercentage / 100)
                                                                : null;
                                                            
                                                            return (
                                                                <div
                                                                    key={product.productCode}
                                                                    className={`product-card ${isOutOfStock ? 'out-of-stock' : ''} ${isLowStock ? 'low-stock' : ''}`}
                                                                    onClick={() => addToCart(product)}
                                                                    style={{ cursor: isOutOfStock ? 'not-allowed' : 'pointer' }}
                                                                >
                                                                    {hasActiveDiscount && (
                                                                        <div className="product-discount-badge">
                                                                            -{discountPercentage}% OFF
                                                                        </div>
                                                                    )}
                                                                    {isLowStock && (
                                                                        <div className="low-stock-badge">⚠️ Low Stock - Restock Soon</div>
                                                                    )}
                                                                    {isOutOfStock && (
                                                                        <div className="out-of-stock-badge">❌ Out of Stock</div>
                                                                    )}
                                                                    {product.imageUrl ? (
                                                                        <img 
                                                                            src={product.imageUrl} 
                                                                            alt={product.name}
                                                                            className="product-image"
                                                                            onError={(e) => {
                                                                                e.target.style.display = 'none';
                                                                                e.target.nextSibling.style.display = 'flex';
                                                                            }}
                                                                        />
                                                                    ) : null}
                                                                    <div className="product-icon" style={{ display: product.imageUrl ? 'none' : 'flex' }}>📦</div>
                                                                    <div className="product-name">{product.name}</div>
                                                                    <div className="product-price">
                                                                        {hasActiveDiscount ? (
                                                                            <>
                                                                                <span style={{textDecoration: 'line-through', color: '#999', fontSize: '14px', marginRight: '6px'}}>
                                                                                    Rs. {Number(product.unitPrice).toFixed(2)}
                                                                                </span>
                                                                                <span style={{color: '#52B788', fontWeight: '700'}}>
                                                                                    Rs. {discountedPrice.toFixed(2)}
                                                                                </span>
                                                                            </>
                                                                        ) : (
                                                                            `Rs. ${Number(product.unitPrice).toFixed(2)}`
                                                                        )}
                                                                    </div>
                                                                    <div className="product-stock">
                                                                        Shelf: {product.shelfQuantity || 0} | 
                                                                        Total: {product.totalQuantity || 0}
                                                                    </div>
                                                                </div>
                                                            );
                                                        })}
                                                    </div>
                                                    {filteredProducts.length === 0 && (
                                                        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
                                                            No products found
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="cart-section">
                                                    <h3 className="section-subtitle">Current Order</h3>
                                                    {cart.length === 0 ? (
                                                        <div className="cart-empty">
                                                            <p>🛒</p>
                                                            <p>No items in cart</p>
                                                        </div>
                                                    ) : (
                                                        <>
                                                            <div className="cart-items">
                                                                {cart.map(item => (
                                                                    <div key={item.productCode} className="cart-item">
                                                                        <div className="cart-item-info">
                                                                            <div className="cart-item-name">
                                                                                {item.name}
                                                                                {item.discountApplied > 0 && (
                                                                                    <span className="cart-discount-badge">-{item.discountApplied}% OFF</span>
                                                                                )}
                                                                            </div>
                                                                            <div className="cart-item-price">
                                                                                {item.discountApplied > 0 ? (
                                                                                    <>
                                                                                        <span style={{textDecoration: 'line-through', color: '#999', marginRight: '8px'}}>
                                                                                            Rs. {Number(item.originalPrice).toFixed(2)}
                                                                                        </span>
                                                                                        <span style={{color: '#52B788', fontWeight: '600'}}>
                                                                                            Rs. {Number(item.finalPrice).toFixed(2)} each
                                                                                        </span>
                                                                                    </>
                                                                                ) : (
                                                                                    `Rs. ${Number(item.unitPrice).toFixed(2)} each`
                                                                                )}
                                                                            </div>
                                                                        </div>
                                                                        <div className="cart-item-controls">
                                                                            <button
                                                                                className="qty-btn"
                                                                                onClick={() => updateQuantity(item.productCode, item.quantity - 1)}
                                                                            >
                                                                                -
                                                                            </button>
                                                                            <span className="qty-display">{item.quantity}</span>
                                                                            <button
                                                                                className="qty-btn"
                                                                                onClick={() => updateQuantity(item.productCode, item.quantity + 1)}
                                                                            >
                                                                                +
                                                                            </button>
                                                                            <button
                                                                                className="remove-btn"
                                                                                onClick={() => removeFromCart(item.productCode)}
                                                                            >
                                                                                🗑️
                                                                            </button>
                                                                        </div>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                            {getTotalSavings() > 0 && (
                                                                <div className="cart-savings">
                                                                    <span>💰 Total Savings:</span>
                                                                    <span className="savings-amount">Rs. {getTotalSavings().toFixed(2)}</span>
                                                                </div>
                                                            )}
                                                            <div className="cart-total">
                                                                <div className="cart-total-label">Total Amount:</div>
                                                                <div className="cart-total-value">₱{getCartTotal().toFixed(2)}</div>
                                                            </div>
                                                            <button className="checkout-button" onClick={handleCheckout}>
                                                                💳 Checkout
                                                            </button>
                                                            <button className="clear-cart-btn" onClick={clearCart}>
                                                                Clear Cart
                                                            </button>
                                                        </>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    </>
                                )}
                            </>
                        )}

                        {activeMenu === "transactions" && (
                            <>
                                <h1 className="section-title">My Transactions</h1>
                                <div className="transactions-table">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Bill Number</th>
                                                <th>Date & Time</th>
                                                <th>Total</th>
                                                <th>Change</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {bills.length > 0 ? (
                                                (() => {
                                                    // Pagination logic
                                                    const indexOfLastItem = currentPage * itemsPerPage;
                                                    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
                                                    const currentBills = bills.slice(indexOfFirstItem, indexOfLastItem);
                                                    
                                                    return currentBills.map(bill => (
                                                        <tr key={bill.billNumber}>
                                                            <td>{bill.billNumber}</td>
                                                            <td>{bill.billDate || 'N/A'}</td>
                                                            <td>Rs. {Number(bill.totalAmount || 0).toFixed(2)}</td>
                                                            <td>Rs. {Number(bill.changeAmount || 0).toFixed(2)}</td>
                                                            <td>
                                                                <span className="badge success">
                                                                    {bill.status || 'Completed'}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <button 
                                                                    className="view-bill-btn"
                                                                    onClick={() => viewBillDetails(bill.billNumber)}
                                                                >
                                                                    📄 View
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    ));
                                                })()
                                            ) : (
                                                <tr>
                                                    <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
                                                        No transactions yet
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </div>
                                
                                {/* Pagination Controls */}
                                {bills.length > 0 && (
                                    <div className="pagination">
                                        <button
                                            className="pagination-btn"
                                            onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                            disabled={currentPage === 1}
                                        >
                                            ← Previous
                                        </button>
                                        
                                        <span className="pagination-info">
                                            Page {currentPage} of {Math.ceil(bills.length / itemsPerPage)} 
                                            ({bills.length} total)
                                        </span>
                                        
                                        <button
                                            className="pagination-btn"
                                            onClick={() => setCurrentPage(prev => Math.min(Math.ceil(bills.length / itemsPerPage), prev + 1))}
                                            disabled={currentPage === Math.ceil(bills.length / itemsPerPage)}
                                        >
                                            Next →
                                        </button>
                                    </div>
                                )}
                            </>
                        )}

                        {activeMenu === "promotions" && (
                            <PromotionsView />
                        )}

                        {activeMenu !== "pos" && activeMenu !== "transactions" && activeMenu !== "promotions" && (
                            <div>
                                <h1 className="section-title">{menuItems.find(m => m.id === activeMenu)?.label}</h1>
                                <p style={{color: '#666'}}>Content for {activeMenu} section coming soon...</p>
                            </div>
                        )}
                    </div>
                </main>

                {showCheckoutModal && (
                    <CheckoutModal
                        cart={cart}
                        total={getCartTotal()}
                        onClose={() => setShowCheckoutModal(false)}
                        onSuccess={(billNumber, change) => {
                            setShowCheckoutModal(false);
                            setCart([]);  // Clear cart without restoring quantities
                            loadBills();
                            loadProducts();  // Reload products to get updated quantities from backend
                            showNotification(`Bill created successfully! Bill #${billNumber || 'N/A'} | Change: Rs. ${change.toFixed(2)}`, 'success');
                        }}
                    />
                )}

                {showBillModal && selectedBill && (
                    <BillDetailsModal
                        bill={selectedBill}
                        onClose={() => {
                            setShowBillModal(false);
                            setSelectedBill(null);
                        }}
                        onDownload={downloadBillReceipt}
                    />
                )}
            </div>
        </>
    );
}

function CheckoutModal({ cart, total, onClose, onSuccess }) {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("cash");
    const [amountPaid, setAmountPaid] = useState("");

    console.log('🧾 Checkout Modal - Cart Items:', cart);
    cart.forEach((item, index) => {
        console.log(`   Item ${index + 1}: ${item.name}`);
        console.log(`      Original Price: ${item.originalPrice}`);
        console.log(`      Final Price: ${item.finalPrice}`);
        console.log(`      Discount Applied: ${item.discountApplied}%`);
    });

    const change = amountPaid ? Number(amountPaid) - total : 0;
    
    const getTotalSavings = () => {
        return cart.reduce((savings, item) => {
            if (item.discountApplied > 0 && item.originalPrice) {
                const originalTotal = item.originalPrice * item.quantity;
                const discountedTotal = item.finalPrice * item.quantity;
                return savings + (originalTotal - discountedTotal);
            }
            return savings;
        }, 0);
    };
    
    const totalSavings = getTotalSavings();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (paymentMethod === "cash" && Number(amountPaid) < total) {
            setError("Amount paid must be greater than or equal to total");
            return;
        }

        const billData = {
            items: cart.map(item => ({
                productCode: item.productCode,
                quantity: item.quantity,
                price: item.finalPrice || item.unitPrice // Use discounted price if available
            })),
            totalAmount: total,
            paymentMethod: paymentMethod,
            amountPaid: paymentMethod === "cash" ? Number(amountPaid) : total,
            change: paymentMethod === "cash" ? change : 0
        };

        try {
            setLoading(true);
            const response = await apiCreateBill(billData);

            if (response.success) {
                onSuccess(response.data?.billNumber, change);
            } else {
                setError(response.message || "Failed to create bill");
            }
        } catch (err) {
            setError("Error creating bill: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <style>{`
                .modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.7);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 9999;
                }

                .checkout-modal {
                    background: white;
                    border-radius: 16px;
                    padding: 32px;
                    max-width: 500px;
                    width: 90%;
                    max-height: 80vh;
                    overflow-y: auto;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                }

                .modal-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 24px;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .order-summary {
                    background: #f9f9f9;
                    border-radius: 12px;
                    padding: 16px;
                    margin-bottom: 24px;
                }

                .order-item {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    border-bottom: 1px solid #e0e0e0;
                }

                .order-item:last-child {
                    border-bottom: none;
                }

                .order-total {
                    font-size: 20px;
                    font-weight: 700;
                    color: #52B788;
                    margin-top: 16px;
                    padding-top: 16px;
                    border-top: 2px solid #52B788;
                    display: flex;
                    justify-content: space-between;
                }

                .form-group-checkout {
                    margin-bottom: 20px;
                }

                .form-label-checkout {
                    display: block;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 8px;
                }

                .payment-methods {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 20px;
                }

                .payment-method-btn {
                    flex: 1;
                    padding: 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 8px;
                    cursor: pointer;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .payment-method-btn.active {
                    border-color: #52B788;
                    background: linear-gradient(135deg, #e8f5ed 0%, #d4ede1 100%);
                    color: #52B788;
                }

                .input-checkout {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 16px;
                }

                .input-checkout:focus {
                    outline: none;
                    border-color: #52B788;
                }

                .change-display {
                    padding: 16px;
                    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                    border-radius: 8px;
                    text-align: center;
                    margin-bottom: 20px;
                }

                .change-label {
                    font-size: 14px;
                    color: #065f46;
                    margin-bottom: 4px;
                }

                .change-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #065f46;
                }

                .modal-actions {
                    display: flex;
                    gap: 12px;
                }

                .btn-cancel-modal {
                    flex: 1;
                    padding: 14px;
                    background: white;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-cancel-modal:hover {
                    border-color: #f44336;
                    color: #f44336;
                }

                .btn-confirm-modal {
                    flex: 1;
                    padding: 14px;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .btn-confirm-modal:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .btn-confirm-modal:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .error-message {
                    padding: 12px;
                    background: #fee2e2;
                    color: #991b1b;
                    border-radius: 8px;
                    margin-bottom: 16px;
                    font-weight: 600;
                }
            `}</style>

            <div className="modal-overlay" onClick={onClose}>
                <div className="checkout-modal" onClick={(e) => e.stopPropagation()}>
                    <h2 className="modal-title">💳 Checkout</h2>

                    {error && <div className="error-message">⚠️ {error}</div>}

                    <div className="order-summary">
                        <h3 style={{ marginBottom: '12px', color: '#666' }}>Order Summary</h3>
                        {cart.map(item => {
                            const price = item.finalPrice || item.unitPrice;
                            const itemTotal = price * item.quantity;
                            const hasDiscount = item.discountApplied > 0;
                            const originalTotal = hasDiscount ? (item.originalPrice * item.quantity) : null;
                            
                            return (
                                <div key={item.productCode} className="order-item">
                                    <div style={{ flex: 1 }}>
                                        <div style={{ marginBottom: hasDiscount ? '4px' : '0' }}>
                                            {item.name} x{item.quantity}
                                            {hasDiscount && (
                                                <span style={{
                                                    background: '#FF6B6B',
                                                    color: 'white',
                                                    fontSize: '10px',
                                                    padding: '2px 6px',
                                                    borderRadius: '4px',
                                                    marginLeft: '6px',
                                                    fontWeight: '700'
                                                }}>
                                                    -{item.discountApplied}% OFF
                                                </span>
                                            )}
                                        </div>
                                        {hasDiscount && (
                                            <div style={{ fontSize: '12px', color: '#999' }}>
                                                <span style={{ textDecoration: 'line-through' }}>
                                                    Rs. {originalTotal.toFixed(2)}
                                                </span>
                                                <span style={{ color: '#52B788', marginLeft: '6px', fontWeight: '600' }}>
                                                    Rs. {itemTotal.toFixed(2)}
                                                </span>
                                            </div>
                                        )}
                                    </div>
                                    {!hasDiscount && (
                                        <span>Rs. {itemTotal.toFixed(2)}</span>
                                    )}
                                </div>
                            );
                        })}
                        {totalSavings > 0 && (
                            <div style={{
                                padding: '8px 12px',
                                background: 'linear-gradient(135deg, #FFEAA7 0%, #FFD700 100%)',
                                borderRadius: '6px',
                                marginTop: '8px',
                                marginBottom: '8px',
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                fontSize: '14px',
                                fontWeight: '600',
                                color: '#333'
                            }}>
                                <span>💰 Total Savings:</span>
                                <span style={{ fontSize: '16px', fontWeight: '700', color: '#E67E22' }}>
                                    Rs. {totalSavings.toFixed(2)}
                                </span>
                            </div>
                        )}
                        <div className="order-total">
                            <span>Total:</span>
                            <span>Rs. {total.toFixed(2)}</span>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group-checkout">
                            <label className="form-label-checkout">Payment Method</label>
                            <div className="payment-methods">
                                <button
                                    type="button"
                                    className={`payment-method-btn ${paymentMethod === 'cash' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('cash')}
                                >
                                    💵 Cash
                                </button>
                                <button
                                    type="button"
                                    className={`payment-method-btn ${paymentMethod === 'card' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('card')}
                                >
                                    💳 Card
                                </button>
                            </div>
                        </div>

                        {paymentMethod === 'cash' && (
                            <>
                                <div className="form-group-checkout">
                                    <label className="form-label-checkout">Amount Paid</label>
                                    <input
                                        type="number"
                                        className="input-checkout"
                                        placeholder="Enter amount"
                                        value={amountPaid}
                                        onChange={(e) => setAmountPaid(e.target.value)}
                                        step="0.01"
                                        required
                                    />
                                </div>

                                {amountPaid && change >= 0 && (
                                    <div className="change-display">
                                        <div className="change-label">Change</div>
                                        <div className="change-value">Rs. {change.toFixed(2)}</div>
                                    </div>
                                )}
                            </>
                        )}

                        <div className="modal-actions">
                            <button type="button" className="btn-cancel-modal" onClick={onClose}>
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className="btn-confirm-modal"
                                disabled={loading || (paymentMethod === 'cash' && !amountPaid)}
                            >
                                {loading ? "Processing..." : "Confirm Payment"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </>
    );
}

// Bill Details Modal Component
function BillDetailsModal({ bill, onClose, onDownload }) {
    return (
        <>
            <style>{`
                .bill-modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.7);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 9999;
                }

                .bill-modal {
                    background: white;
                    border-radius: 16px;
                    padding: 32px;
                    max-width: 600px;
                    width: 90%;
                    max-height: 85vh;
                    overflow-y: auto;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                }

                .bill-header {
                    text-align: center;
                    border-bottom: 2px solid #52B788;
                    padding-bottom: 16px;
                    margin-bottom: 24px;
                }

                .bill-header h2 {
                    font-size: 24px;
                    color: #333;
                    margin-bottom: 8px;
                }

                .bill-number {
                    font-size: 18px;
                    color: #52B788;
                    font-weight: 700;
                }

                .bill-info {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 12px;
                    margin-bottom: 24px;
                    padding: 16px;
                    background: #f9f9f9;
                    border-radius: 8px;
                }

                .bill-info-item {
                    display: flex;
                    flex-direction: column;
                }

                .bill-info-label {
                    font-size: 12px;
                    color: #666;
                    text-transform: uppercase;
                    margin-bottom: 4px;
                }

                .bill-info-value {
                    font-size: 14px;
                    color: #333;
                    font-weight: 600;
                }

                .bill-items {
                    margin-bottom: 24px;
                }

                .bill-items h3 {
                    font-size: 16px;
                    color: #333;
                    margin-bottom: 12px;
                }

                .bill-item {
                    display: flex;
                    justify-content: space-between;
                    padding: 12px;
                    background: white;
                    border: 1px solid #e0e0e0;
                    border-radius: 8px;
                    margin-bottom: 8px;
                }

                .bill-item-name {
                    font-weight: 600;
                    color: #333;
                }

                .bill-item-details {
                    font-size: 13px;
                    color: #666;
                    margin-top: 4px;
                }

                .bill-item-price {
                    font-weight: 700;
                    color: #52B788;
                }

                .bill-totals {
                    border-top: 2px solid #e0e0e0;
                    padding-top: 16px;
                    margin-bottom: 24px;
                }

                .bill-total-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    font-size: 14px;
                }

                .bill-total-row.grand-total {
                    font-size: 20px;
                    font-weight: 700;
                    color: #52B788;
                    border-top: 2px solid #52B788;
                    padding-top: 12px;
                    margin-top: 8px;
                }

                .bill-modal-actions {
                    display: flex;
                    gap: 12px;
                }

                .btn-download-bill {
                    flex: 1;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 14px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 15px;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-download-bill:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .btn-close-bill {
                    flex: 1;
                    background: white;
                    color: #666;
                    border: 2px solid #e0e0e0;
                    padding: 14px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 15px;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-close-bill:hover {
                    border-color: #52B788;
                    color: #52B788;
                }
            `}</style>

            <div className="bill-modal-overlay" onClick={onClose}>
                <div className="bill-modal" onClick={(e) => e.stopPropagation()}>
                    <div className="bill-header">
                        <h2>📄 Receipt</h2>
                        <div className="bill-number">{bill.billNumber}</div>
                    </div>

                    <div className="bill-info">
                        <div className="bill-info-item">
                            <div className="bill-info-label">Date & Time</div>
                            <div className="bill-info-value">{bill.billDate || bill.createdAt || 'N/A'}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Cashier</div>
                            <div className="bill-info-value">{bill.cashierName || 'N/A'}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Payment Method</div>
                            <div className="bill-info-value">{(bill.paymentMethod || 'Cash').toUpperCase()}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Status</div>
                            <div className="bill-info-value">{bill.status || 'Completed'}</div>
                        </div>
                    </div>

                    <div className="bill-items">
                        <h3>Items Purchased</h3>
                        {bill.items && bill.items.length > 0 ? (
                            bill.items.map((item, index) => (
                                <div key={index} className="bill-item">
                                    <div>
                                        <div className="bill-item-name">{item.productName || 'Product'}</div>
                                        <div className="bill-item-details">
                                            Qty: {item.quantity} × Rs. {Number(item.unitPrice || 0).toFixed(2)}
                                        </div>
                                    </div>
                                    <div className="bill-item-price">
                                        Rs. {Number(item.totalPrice || (item.quantity * item.unitPrice) || 0).toFixed(2)}
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p style={{ color: '#888', textAlign: 'center', padding: '20px' }}>No items found</p>
                        )}
                    </div>

                    <div className="bill-totals">
                        <div className="bill-total-row">
                            <span>Subtotal:</span>
                            <span>Rs. {Number(bill.subtotal || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row">
                            <span>Amount Paid:</span>
                            <span>Rs. {Number(bill.amountPaid || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row">
                            <span>Change:</span>
                            <span>Rs. {Number(bill.changeAmount || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row grand-total">
                            <span>Total Amount:</span>
                            <span>Rs. {Number(bill.totalAmount || 0).toFixed(2)}</span>
                        </div>
                    </div>

                    <div className="bill-modal-actions">
                        <button className="btn-download-bill" onClick={onDownload}>
                            ⬇️ Download Receipt
                        </button>
                        <button className="btn-close-bill" onClick={onClose}>
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}

function PromotionsView() {
    const [promotions, setPromotions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadPromotions();
    }, []);

    const loadPromotions = async () => {
        try {
            setLoading(true);
            const response = await apiGetCashierPromotions();
            console.log('🎯 Promotions API Response:', response);
            if (response.success) {
                const promotionsData = response.data || [];
                console.log('📊 Promotions Data:', promotionsData);
                console.log('🔢 Total promotions:', promotionsData.length);
                console.log('📦 Product discounts:', promotionsData.filter(p => p.type === 'PRODUCT').length);
                console.log('⚡ Batch discounts:', promotionsData.filter(p => p.type === 'BATCH').length);
                setPromotions(promotionsData);
            }
        } catch (err) {
            console.error("Error loading promotions:", err);
        } finally {
            setLoading(false);
        }
    };

    const calculateDaysUntilExpiry = (expiryDate) => {
        const now = new Date();
        const expiry = new Date(expiryDate);
        const diffTime = expiry - now;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    };

    const calculateDiscountedPrice = (price, discount) => {
        return price - (price * discount / 100);
    };

    return (
        <>
            <style>{`
                .promotions-container {
                    padding: 20px;
                }

                .promotions-header {
                    margin-bottom: 30px;
                }

                .promotions-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 8px;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .promotions-subtitle {
                    font-size: 16px;
                    color: #666;
                }

                .discount-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
                    gap: 20px;
                }

                .discount-card {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 3px 12px rgba(0,0,0,0.12);
                    position: relative;
                    overflow: hidden;
                    transition: transform 0.2s, box-shadow 0.2s;
                }

                .discount-card:hover {
                    transform: translateY(-4px);
                    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
                }

                .discount-card.product-discount {
                    border-left: 5px solid #3498db;
                }

                .discount-card.batch-discount {
                    border-left: 5px solid #e74c3c;
                }

                .discount-type-badge {
                    display: inline-block;
                    padding: 6px 12px;
                    border-radius: 20px;
                    font-size: 11px;
                    font-weight: 700;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    margin-bottom: 12px;
                }

                .discount-type-badge.product {
                    background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
                    color: white;
                }

                .discount-type-badge.batch {
                    background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
                    color: white;
                }

                .discount-percentage {
                    position: absolute;
                    top: 20px;
                    right: 20px;
                    background: #FF6B6B;
                    color: white;
                    padding: 10px 18px;
                    border-radius: 25px;
                    font-weight: 700;
                    font-size: 18px;
                    box-shadow: 0 3px 12px rgba(255, 107, 107, 0.4);
                }

                .product-info {
                    margin-bottom: 16px;
                }

                .product-name {
                    font-size: 20px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 8px;
                    padding-right: 90px;
                }

                .product-code {
                    font-size: 13px;
                    color: #999;
                    font-family: 'Courier New', monospace;
                }

                .batch-info {
                    background: #fff3cd;
                    padding: 8px 12px;
                    border-radius: 6px;
                    margin-bottom: 16px;
                    border-left: 3px solid #ffc107;
                }

                .batch-info-text {
                    font-size: 13px;
                    color: #856404;
                    font-weight: 600;
                }

                .expiry-warning {
                    background: #f8d7da;
                    padding: 10px 14px;
                    border-radius: 8px;
                    margin-bottom: 16px;
                    border-left: 3px solid #dc3545;
                }

                .expiry-warning-text {
                    font-size: 14px;
                    color: #721c24;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .price-section {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    margin-bottom: 16px;
                }

                .original-price {
                    font-size: 18px;
                    color: #999;
                    text-decoration: line-through;
                }

                .discounted-price {
                    font-size: 26px;
                    font-weight: 700;
                    color: #27ae60;
                }

                .discount-period {
                    background: #f8f9fa;
                    padding: 12px;
                    border-radius: 8px;
                    font-size: 13px;
                    color: #666;
                }

                .discount-period-label {
                    font-weight: 600;
                    margin-bottom: 4px;
                    color: #333;
                }

                .no-promotions {
                    text-align: center;
                    padding: 60px 20px;
                    background: white;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                }

                .no-promotions-icon {
                    font-size: 64px;
                    margin-bottom: 16px;
                }

                .no-promotions-text {
                    font-size: 20px;
                    color: #666;
                }

                .loading-container {
                    text-align: center;
                    padding: 60px 20px;
                }

                .loading-spinner {
                    font-size: 48px;
                    animation: spin 1s linear infinite;
                }

                @keyframes spin {
                    from { transform: rotate(0deg); }
                    to { transform: rotate(360deg); }
                }
            `}</style>

            <div className="promotions-container">
                <div className="promotions-header">
                    <h1 className="promotions-title">
                        🏷️ Active Promotions
                    </h1>
                    <p className="promotions-subtitle">
                        {loading 
                            ? 'Loading promotions...'
                            : promotions.length > 0 
                                ? `${promotions.length} active ${promotions.length === 1 ? 'promotion' : 'promotions'}`
                                : 'No active promotions at the moment'}
                    </p>
                </div>

                {loading ? (
                    <div className="loading-container">
                        <div className="loading-spinner">⏳</div>
                    </div>
                ) : promotions.length > 0 ? (
                    <div className="discount-grid">
                        {promotions.map((promo, index) => {
                            const isProductDiscount = promo.type === "PRODUCT";
                            const isBatchDiscount = promo.type === "BATCH";
                            const daysUntilExpiry = isBatchDiscount && promo.expiryDate 
                                ? calculateDaysUntilExpiry(promo.expiryDate) 
                                : null;

                            return (
                                <div 
                                    key={index} 
                                    className={`discount-card ${isProductDiscount ? 'product-discount' : 'batch-discount'}`}
                                >
                                    <div className="discount-percentage">
                                        -{promo.discountPercentage}%
                                    </div>

                                    <div className={`discount-type-badge ${isProductDiscount ? 'product' : 'batch'}`}>
                                        {isProductDiscount ? '📦 Product Promotion' : '⚡ Clearance Sale'}
                                    </div>

                                    <div className="product-info">
                                        <div className="product-name">{promo.productName || 'Product'}</div>
                                        <div className="product-code">Code: {promo.productCode}</div>
                                    </div>

                                    {isBatchDiscount && promo.batchId && (
                                        <div className="batch-info">
                                            <div className="batch-info-text">
                                                🏷️ Batch ID: {promo.batchId}
                                            </div>
                                        </div>
                                    )}

                                    {isBatchDiscount && daysUntilExpiry !== null && (
                                        <div className="expiry-warning">
                                            <div className="expiry-warning-text">
                                                <span>⏰</span>
                                                <span>
                                                    {daysUntilExpiry > 0 
                                                        ? `Expires in ${daysUntilExpiry} ${daysUntilExpiry === 1 ? 'day' : 'days'}!`
                                                        : daysUntilExpiry === 0
                                                            ? 'Expires TODAY!'
                                                            : 'Expired'}
                                                </span>
                                            </div>
                                        </div>
                                    )}

                                    {promo.unitPrice && (
                                        <div className="price-section">
                                            <span className="original-price">
                                                Rs. {Number(promo.unitPrice).toFixed(2)}
                                            </span>
                                            <span className="discounted-price">
                                                Rs. {calculateDiscountedPrice(promo.unitPrice, promo.discountPercentage).toFixed(2)}
                                            </span>
                                        </div>
                                    )}

                                    {promo.discountStartDate && promo.discountEndDate && (
                                        <div className="discount-period">
                                            <div className="discount-period-label">Valid Period:</div>
                                            <div>
                                                {new Date(promo.discountStartDate).toLocaleDateString()} - {new Date(promo.discountEndDate).toLocaleDateString()}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <div className="no-promotions">
                        <div className="no-promotions-icon">🛍️</div>
                        <div className="no-promotions-text">No active promotions at the moment</div>
                    </div>
                )}
            </div>
        </>
    );
}
