import { useState, useEffect } from 'react';
import { apiGetProducts, apiGetInventory, apiGetCategories } from '../../services/api';

export default function AnalyticsDashboard() {
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        totalProducts: 0,
        lowStockProducts: 0,
        outOfStockProducts: 0,
        activeDiscounts: 0,
        totalInventoryValue: 0,
        categories: []
    });

    const [productsByCategory, setProductsByCategory] = useState([]);
    const [stockLevels, setStockLevels] = useState([]);
    const [topProducts, setTopProducts] = useState([]);

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            
            // Fetch all data in parallel
            const [productsRes, inventoryRes, categoriesRes] = await Promise.all([
                apiGetProducts(),
                apiGetInventory(),
                apiGetCategories()
            ]);

            if (productsRes.success) {
                const products = productsRes.data?.products || [];
                
                // Calculate stats
                const lowStock = products.filter(p => p.status === 'Low Stock').length;
                const outOfStock = products.filter(p => p.status === 'Out of Stock').length;
                const activeDiscounts = products.filter(p => p.discountPercentage > 0).length;
                
                // Calculate total inventory value
                const totalValue = products.reduce((sum, p) => {
                    return sum + (p.unitPrice * p.totalQuantity);
                }, 0);

                // Group products by category
                const categoryMap = {};
                products.forEach(p => {
                    const catName = p.categoryName || 'Uncategorized';
                    if (!categoryMap[catName]) {
                        categoryMap[catName] = { name: catName, count: 0, value: 0 };
                    }
                    categoryMap[catName].count++;
                    categoryMap[catName].value += p.unitPrice * p.totalQuantity;
                });

                // Stock level distribution
                const stockDist = {
                    inStock: products.filter(p => p.status === 'In Stock').length,
                    lowStock: lowStock,
                    outOfStock: outOfStock
                };

                // Top 10 products by inventory value
                const topProds = products
                    .map(p => ({
                        name: p.name,
                        code: p.productCode,
                        value: p.unitPrice * p.totalQuantity,
                        quantity: p.totalQuantity,
                        price: p.unitPrice
                    }))
                    .sort((a, b) => b.value - a.value)
                    .slice(0, 10);

                setStats({
                    totalProducts: products.length,
                    lowStockProducts: lowStock,
                    outOfStockProducts: outOfStock,
                    activeDiscounts: activeDiscounts,
                    totalInventoryValue: totalValue,
                    categories: Object.values(categoryMap)
                });

                setProductsByCategory(Object.values(categoryMap));
                setStockLevels([
                    { name: 'In Stock', value: stockDist.inStock, color: '#4caf50' },
                    { name: 'Low Stock', value: stockDist.lowStock, color: '#ff9800' },
                    { name: 'Out of Stock', value: stockDist.outOfStock, color: '#f44336' }
                ]);
                setTopProducts(topProds);
            }
        } catch (error) {
            console.error('Error loading dashboard data:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div style={{padding: '40px', textAlign: 'center', fontSize: '18px', color: '#666'}}>
                Loading analytics...
            </div>
        );
    }

    return (
        <>
            <style>{`
                .analytics-container { padding: 24px; background: #f5f5f5; min-height: 100vh; }
                .analytics-header { margin-bottom: 32px; }
                .analytics-title { font-size: 28px; font-weight: 700; color: #333; margin-bottom: 8px; }
                .analytics-subtitle { color: #666; font-size: 14px; }
                
                .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 32px; }
                .stat-card { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.2s; }
                .stat-card:hover { transform: translateY(-4px); box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
                .stat-icon { font-size: 32px; margin-bottom: 12px; }
                .stat-value { font-size: 32px; font-weight: 700; color: #333; margin-bottom: 4px; }
                .stat-label { color: #666; font-size: 14px; font-weight: 500; }
                .stat-change { font-size: 12px; margin-top: 8px; }
                .stat-change.positive { color: #4caf50; }
                .stat-change.negative { color: #f44336; }
                
                .charts-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-bottom: 32px; }
                .chart-card { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
                .chart-title { font-size: 18px; font-weight: 600; color: #333; margin-bottom: 20px; }
                
                .full-width-card { grid-column: 1 / -1; }
                
                .category-list { display: flex; flex-direction: column; gap: 12px; }
                .category-item { display: flex; justify-content: space-between; align-items: center; padding: 12px; background: #f8f8f8; border-radius: 8px; }
                .category-bar { height: 8px; background: #e0e0e0; border-radius: 4px; margin-top: 8px; overflow: hidden; }
                .category-bar-fill { height: 100%; background: linear-gradient(90deg, #52B788 0%, #40916C 100%); transition: width 0.3s; }
                
                .stock-chart { display: flex; gap: 12px; height: 200px; align-items: flex-end; }
                .stock-bar { flex: 1; border-radius: 8px 8px 0 0; position: relative; transition: all 0.3s; cursor: pointer; }
                .stock-bar:hover { opacity: 0.8; }
                .stock-bar-label { position: absolute; bottom: -30px; left: 0; right: 0; text-align: center; font-size: 12px; color: #666; }
                .stock-bar-value { position: absolute; top: -30px; left: 0; right: 0; text-align: center; font-weight: 600; color: #333; }
                
                .top-products-table { width: 100%; }
                .top-products-table th { text-align: left; padding: 12px; background: #f8f8f8; font-size: 13px; font-weight: 600; color: #666; border-bottom: 2px solid #e0e0e0; }
                .top-products-table td { padding: 12px; border-bottom: 1px solid #e0e0e0; }
                .top-products-table tr:hover { background: #f8f8f8; }
                .product-rank { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, #52B788 0%, #40916C 100%); color: white; display: flex; align-items: center; justify-content: center; font-weight: 700; }
                .product-rank.gold { background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%); color: #333; }
                .product-rank.silver { background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%); color: #333; }
                .product-rank.bronze { background: linear-gradient(135deg, #cd7f32 0%, #d4a574 100%); color: white; }
                
                @media (max-width: 1024px) {
                    .charts-grid { grid-template-columns: 1fr; }
                }
            `}</style>

            <div className="analytics-container">
                <div className="analytics-header">
                    <h1 className="analytics-title">📊 Business Analytics Dashboard</h1>
                    <p className="analytics-subtitle">Real-time insights and performance metrics</p>
                </div>

                {/* Key Statistics */}
                <div className="stats-grid">
                    <div className="stat-card">
                        <div className="stat-icon">📦</div>
                        <div className="stat-value">{stats.totalProducts}</div>
                        <div className="stat-label">Total Products</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">💰</div>
                        <div className="stat-value">Rs. {stats.totalInventoryValue.toLocaleString()}</div>
                        <div className="stat-label">Total Inventory Value</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">⚠️</div>
                        <div className="stat-value">{stats.lowStockProducts}</div>
                        <div className="stat-label">Low Stock Alerts</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">❌</div>
                        <div className="stat-value">{stats.outOfStockProducts}</div>
                        <div className="stat-label">Out of Stock</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">🏷️</div>
                        <div className="stat-value">{stats.activeDiscounts}</div>
                        <div className="stat-label">Active Discounts</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon">📁</div>
                        <div className="stat-value">{stats.categories.length}</div>
                        <div className="stat-label">Product Categories</div>
                    </div>
                </div>

                {/* Charts Grid */}
                <div className="charts-grid">
                    {/* Stock Levels Chart */}
                    <div className="chart-card">
                        <h3 className="chart-title">Stock Status Distribution</h3>
                        <div className="stock-chart">
                            {stockLevels.map((item, idx) => (
                                <div key={idx} className="stock-bar" style={{
                                    background: item.color,
                                    height: `${(item.value / stats.totalProducts) * 100}%`
                                }}>
                                    <div className="stock-bar-value">{item.value}</div>
                                    <div className="stock-bar-label">{item.name}</div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Products by Category */}
                    <div className="chart-card">
                        <h3 className="chart-title">Products by Category</h3>
                        <div className="category-list">
                            {productsByCategory.slice(0, 8).map((cat, idx) => {
                                const maxCount = Math.max(...productsByCategory.map(c => c.count));
                                const percentage = (cat.count / maxCount) * 100;
                                return (
                                    <div key={idx}>
                                        <div className="category-item">
                                            <span style={{fontWeight: 500}}>{cat.name}</span>
                                            <span style={{color: '#52B788', fontWeight: 600}}>{cat.count} products</span>
                                        </div>
                                        <div className="category-bar">
                                            <div className="category-bar-fill" style={{width: `${percentage}%`}}></div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </div>

                {/* Top Products Table */}
                <div className="chart-card full-width-card">
                    <h3 className="chart-title">🏆 Top 10 Products by Inventory Value</h3>
                    <table className="top-products-table">
                        <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Product Code</th>
                                <th>Product Name</th>
                                <th>Quantity</th>
                                <th>Unit Price</th>
                                <th>Total Value</th>
                            </tr>
                        </thead>
                        <tbody>
                            {topProducts.map((product, idx) => {
                                let rankClass = '';
                                if (idx === 0) rankClass = 'gold';
                                else if (idx === 1) rankClass = 'silver';
                                else if (idx === 2) rankClass = 'bronze';
                                
                                return (
                                    <tr key={idx}>
                                        <td>
                                            <div className={`product-rank ${rankClass}`}>
                                                {idx + 1}
                                            </div>
                                        </td>
                                        <td style={{fontWeight: 600, color: '#52B788'}}>{product.code}</td>
                                        <td>{product.name}</td>
                                        <td>{product.quantity} units</td>
                                        <td>Rs. {product.price.toFixed(2)}</td>
                                        <td style={{fontWeight: 600}}>Rs. {product.value.toLocaleString()}</td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    );
}
