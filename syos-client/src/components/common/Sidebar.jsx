import React from 'react';

export default function Sidebar({ logo, menuItems, activeMenu, onMenuClick, isOpen, accentColor = "#52B788", expandedMenus = [] }) {
    return (
        <>
            <style>{`
                .sidebar {
                    width: 240px;
                    background: white;
                    border-right: 1px solid #e0e0e0;
                    display: flex;
                    flex-direction: column;
                    transition: all 0.3s ease;
                    overflow: hidden;
                }
                
                .sidebar.closed {
                    width: 0;
                    border-right: none;
                }
                
                .logo-section {
                    padding: 24px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-bottom: 1px solid #e0e0e0;
                }
                
                .logo-section img {
                    height: 60px;
                    width: auto;
                    max-width: 90%;
                }
                
                .menu-list {
                    flex: 1;
                    padding: 16px 0;
                    overflow-y: auto;
                }
                
                .menu-item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
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
                    color: white;
                    font-weight: 600;
                    border-left: 3px solid #40916C;
                    box-shadow: 0 2px 8px rgba(82, 183, 136, 0.2);
                }

                .menu-item-content {
                    display: flex;
                    align-items: center;
                    flex: 1;
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

                .menu-item-expand {
                    font-size: 12px;
                    transition: transform 0.2s;
                }

                .menu-item-expand.expanded {
                    transform: rotate(90deg);
                }

                .submenu {
                    background: #f9f9f9;
                    overflow: hidden;
                    transition: max-height 0.3s ease;
                }

                .submenu.collapsed {
                    max-height: 0;
                }

                .submenu.expanded {
                    max-height: 500px;
                }

                .submenu-item {
                    display: flex;
                    align-items: center;
                    padding: 12px 24px 12px 56px;
                    cursor: pointer;
                    transition: all 0.2s ease;
                    color: #666;
                    font-size: 14px;
                    border-left: 3px solid transparent;
                }

                .submenu-item:hover {
                    background: ${accentColor}15;
                    color: #333;
                }

                .submenu-item.active {
                    background: ${accentColor}30;
                    color: #1b4332;
                    font-weight: 600;
                    border-left: 3px solid ${accentColor};
                }

                .submenu-item-icon {
                    font-size: 16px;
                    margin-right: 10px;
                }
            `}</style>

            <aside className={`sidebar ${isOpen ? '' : 'closed'}`}>
                <div className="logo-section">
                    <img src={logo} alt="SYOS" />
                </div>
                <nav className="menu-list">
                    {menuItems.map((item) => (
                        <div key={item.id}>
                            <div
                                className={`menu-item ${!item.submenu && activeMenu === item.id ? 'active' : ''}`}
                                onClick={() => onMenuClick(item.id)}
                            >
                                <div className="menu-item-content">
                                    <span className="menu-item-icon">{item.icon}</span>
                                    <span className="menu-item-label">{item.label}</span>
                                </div>
                                {item.submenu && (
                                    <span className={`menu-item-expand ${expandedMenus.includes(item.id) ? 'expanded' : ''}`}>
                                        ▶
                                    </span>
                                )}
                            </div>
                            {item.submenu && (
                                <div className={`submenu ${expandedMenus.includes(item.id) ? 'expanded' : 'collapsed'}`}>
                                    {item.submenu.map((subItem) => (
                                        <div
                                            key={subItem.id}
                                            className={`submenu-item ${activeMenu === subItem.id ? 'active' : ''}`}
                                            onClick={() => onMenuClick(subItem.id)}
                                        >
                                            <span className="submenu-item-icon">{subItem.icon}</span>
                                            <span>{subItem.label}</span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    ))}
                </nav>
            </aside>
        </>
    );
}

