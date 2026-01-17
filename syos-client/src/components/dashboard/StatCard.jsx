import React from 'react';

export default function StatCard({ label, value, buttonText, onButtonClick, children }) {
    return (
        <>
            <style>{`
                .stat-card {
                    background: white;
                    padding: 24px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    transition: transform 0.2s ease, box-shadow 0.2s ease;
                }
                
                .stat-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0,0,0,0.12);
                }
                
                .stat-label {
                    color: #888;
                    font-size: 14px;
                    margin-bottom: 8px;
                }
                
                .stat-value {
                    font-size: 48px;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 16px;
                }
                
                .stat-button {
                    background: #ffd54f;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    font-size: 13px;
                    transition: all 0.2s ease;
                }
                
                .stat-button:hover {
                    background: #ffc107;
                    transform: scale(1.05);
                }
            `}</style>

            <div className="stat-card">
                {label && <div className="stat-label">{label}</div>}
                {value && <div className="stat-value">{value}</div>}
                {children}
                {buttonText && (
                    <button className="stat-button" onClick={onButtonClick}>
                        {buttonText}
                    </button>
                )}
            </div>
        </>
    );
}

