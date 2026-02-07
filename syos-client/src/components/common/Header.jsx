import React from 'react';

export default function Header({ user, onToggleSidebar, onLogout, showNotifications = true }) {
    const handleLogout = () => {
        console.log("🔴 LOGOUT BUTTON CLICKED!");
        console.log("🔍 onLogout function exists?", !!onLogout);
        console.log("🔍 onLogout type:", typeof onLogout);

        if (typeof onLogout === 'function') {
            console.log("✅ Executing logout...");
            try {
                onLogout();
            } catch (error) {
                console.error("❌ Error calling onLogout:", error);
            }
        } else {
            console.error("❌ onLogout is not a function! Value:", onLogout);
        }
    };

    return (
        <>
            <style>{`
                .header {
                    background: white;
                    padding: 16px 32px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    border-bottom: 1px solid #e0e0e0;
                    position: relative;
                    z-index: 10;
                }
                
                .header-left {
                    display: flex;
                    align-items: center;
                    gap: 16px;
                }
                
                .menu-toggle {
                    font-size: 24px;
                    cursor: pointer;
                    padding: 8px;
                    background: none;
                    border: none;
                    color: #666;
                    z-index: 11;
                    position: relative;
                }
                
                .menu-toggle:hover {
                    color: #333;
                }
                
                .header-right {
                    display: flex;
                    align-items: center;
                    gap: 20px;
                    position: relative;
                    z-index: 11;
                }
                
                .notification-icon {
                    position: relative;
                    font-size: 24px;
                    cursor: pointer;
                    color: #666;
                }
                
                .notification-badge {
                    position: absolute;
                    top: -4px;
                    right: -4px;
                    background: #e91e63;
                    color: white;
                    border-radius: 50%;
                    width: 18px;
                    height: 18px;
                    font-size: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: bold;
                }
                
                .user-profile {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    cursor: pointer;
                }
                
                .user-avatar {
                    width: 36px;
                    height: 36px;
                    border-radius: 50%;
                    background: #bdbdbd;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    font-weight: 600;
                }
                
                .user-name {
                    font-weight: 600;
                    color: #333;
                }
                
                .logout-button {
                    background: #f44336;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-weight: 600;
                    font-size: 14px;
                    transition: all 0.2s ease;
                    position: relative;
                    z-index: 10002;
                    pointer-events: auto;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                }
                
                .logout-button:hover {
                    background: #d32f2f;
                    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                    transform: translateY(-1px);
                }
                
                .logout-button:active {
                    transform: translateY(0);
                    box-shadow: 0 1px 2px rgba(0,0,0,0.1);
                }
            `}</style>

            <header className="header">
                <div className="header-left">
                    <button className="menu-toggle" onClick={onToggleSidebar}>
                        ☰
                    </button>
                </div>
                <div className="header-right">
                    {showNotifications && (
                        <div className="notification-icon">
                            🔔
                            <span className="notification-badge">4</span>
                        </div>
                    )}
                    <div className="user-profile">
                        <div className="user-avatar">
                            {user?.username?.charAt(0).toUpperCase() || 'U'}
                        </div>
                        {user?.username && <span className="user-name">{user.username}</span>}
                    </div>
                    <button className="logout-button" onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </header>
        </>
    );
}

