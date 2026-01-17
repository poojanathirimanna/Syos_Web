import React from 'react';

export default function Sidebar({ logo, menuItems, activeMenu, onMenuClick, isOpen, accentColor = "#ffd54f" }) {
    return (
        <>
            <style>{`
                .sidebar {
                    width: 240px;
                    background: white;
                    border-right: 1px solid #e0e0e0;
                    display: flex;
                    flex-direction: column;
                    transition: width 0.3s ease;
                }
                
                .sidebar.closed {
                    width: 70px;
                }
                
                .logo-section {
                    padding: 24px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-bottom: 1px solid #e0e0e0;
                }
                
                .logo-section img {
                    height: 40px;
                    width: auto;
                }
                
                .menu-list {
                    flex: 1;
                    padding: 16px 0;
                    overflow-y: auto;
                }
                
                .menu-item {
                    display: flex;
                    align-items: center;
                    padding: 14px 24px;
                    cursor: pointer;
                    transition: all 0.2s ease;
                    color: #666;
                    text-decoration: none;
                    border-left: 3px solid transparent;
                    margin: 4px 0;
                }
                
                .menu-item:hover {
                    background: ${accentColor}20;
                    color: #333;
                }
                
                .menu-item.active {
                    background: ${accentColor};
                    color: ${accentColor === '#ffd54f' ? '#000' : '#fff'};
                    font-weight: 600;
                    border-left: 3px solid ${accentColor === '#ffd54f' ? '#ffc107' : '#4caf50'};
                }
                
                .menu-item-icon {
                    font-size: 20px;
                    margin-right: 12px;
                    width: 24px;
                    text-align: center;
                }
                
                .menu-item-label {
                    font-size: 14px;
                    white-space: nowrap;
                }
            `}</style>

            <aside className={`sidebar ${isOpen ? '' : 'closed'}`}>
                <div className="logo-section">
                    <img src={logo} alt="SYOS" />
                </div>
                <nav className="menu-list">
                    {menuItems.map((item) => (
                        <div
                            key={item.id}
                            className={`menu-item ${activeMenu === item.id ? 'active' : ''}`}
                            onClick={() => onMenuClick(item.id)}
                        >
                            <span className="menu-item-icon">{item.icon}</span>
                            <span className="menu-item-label">{item.label}</span>
                        </div>
                    ))}
                </nav>
            </aside>
        </>
    );
}

