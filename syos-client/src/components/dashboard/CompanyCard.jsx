import React from 'react';

export default function CompanyCard({ name, colorScheme = "blue", onClick }) {
    const gradients = {
        blue: "linear-gradient(135deg, #667eea 0%, #4f5bd5 100%)",
        gray: "linear-gradient(135deg, #4a5568 0%, #2d3748 100%)",
        cyan: "linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)",
        pink: "linear-gradient(135deg, #ec4899 0%, #db2777 100%)",
        purple: "linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)",
    };

    return (
        <>
            <style>{`
                .company-card {
                    padding: 40px;
                    border-radius: 12px;
                    color: white;
                    text-align: center;
                    font-size: 20px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: transform 0.2s ease, box-shadow 0.2s ease;
                    min-height: 150px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                
                .company-card:hover {
                    transform: translateY(-4px);
                    box-shadow: 0 8px 16px rgba(0,0,0,0.2);
                }
            `}</style>

            <div
                className="company-card"
                style={{ background: gradients[colorScheme] }}
                onClick={onClick}
            >
                {name}
            </div>
        </>
    );
}

