-- Sample users (password for all users is 'password123')
INSERT INTO users (id, name, email, password, created_at) VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', 'John Doe', 'john.doe@example.com', '$2a$10$ltxy8Y95LYOu07NLrj24Y.enpT8Oj9O.ZNeErLdSROYB6iBo7KZAO', CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'Jane Smith', 'jane.smith@example.com', '$2a$10$ltxy8Y95LYOu07NLrj24Y.enpT8Oj9O.ZNeErLdSROYB6iBo7KZAO', CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440002', 'Bob Johnson', 'bob.johnson@example.com', '$2a$10$ltxy8Y95LYOu07NLrj24Y.enpT8Oj9O.ZNeErLdSROYB6iBo7KZAO', CURRENT_TIMESTAMP);

-- Sample products
INSERT INTO products (id, name, description, price, stock, category, image_url) VALUES
    -- Electronics
    ('650e8400-e29b-41d4-a716-446655440001', 'MacBook Pro 14"', 'Apple M3 Pro chip, 16GB RAM, 512GB SSD', 1999.00, 15, 'Electronics', 'https://via.placeholder.com/300x200/007bff/ffffff?text=MacBook+Pro'),
    ('650e8400-e29b-41d4-a716-446655440002', 'iPad Pro 11"', 'M2 chip, 128GB, Wi-Fi, Space Gray', 799.00, 25, 'Electronics', 'https://via.placeholder.com/300x200/007bff/ffffff?text=iPad+Pro'),
    ('650e8400-e29b-41d4-a716-446655440003', 'iPhone 15 Pro', '256GB, Titanium, A17 Pro chip', 1199.00, 30, 'Electronics', 'https://via.placeholder.com/300x200/007bff/ffffff?text=iPhone+15'),
    ('650e8400-e29b-41d4-a716-446655440004', 'AirPods Pro', 'Active Noise Cancellation, USB-C', 249.00, 50, 'Electronics', 'https://via.placeholder.com/300x200/007bff/ffffff?text=AirPods+Pro'),
    ('650e8400-e29b-41d4-a716-446655440005', 'Apple Watch Ultra', 'GPS + Cellular, Titanium Case', 799.00, 20, 'Electronics', 'https://via.placeholder.com/300x200/007bff/ffffff?text=Apple+Watch'),
    
    -- Accessories
    ('650e8400-e29b-41d4-a716-446655440006', 'Wireless Mouse', 'Logitech MX Master 3S, Ergonomic', 99.99, 100, 'Accessories', 'https://via.placeholder.com/300x200/28a745/ffffff?text=Wireless+Mouse'),
    ('650e8400-e29b-41d4-a716-446655440007', 'Mechanical Keyboard', 'RGB Backlit, Cherry MX Switches', 149.99, 75, 'Accessories', 'https://via.placeholder.com/300x200/28a745/ffffff?text=Keyboard'),
    ('650e8400-e29b-41d4-a716-446655440008', 'USB-C Hub', '7-in-1, HDMI, USB 3.0, SD Card Reader', 49.99, 150, 'Accessories', 'https://via.placeholder.com/300x200/28a745/ffffff?text=USB-C+Hub'),
    ('650e8400-e29b-41d4-a716-446655440009', 'Monitor 27"', '4K UHD, IPS, HDR, USB-C', 449.00, 40, 'Accessories', 'https://via.placeholder.com/300x200/28a745/ffffff?text=Monitor'),
    ('650e8400-e29b-41d4-a716-446655440010', 'Webcam HD', '1080p, Auto Focus, Dual Microphones', 79.99, 60, 'Accessories', 'https://via.placeholder.com/300x200/28a745/ffffff?text=Webcam'),
    
    -- Gaming
    ('650e8400-e29b-41d4-a716-446655440011', 'Gaming Console', 'PlayStation 5, 1TB SSD', 499.00, 35, 'Gaming', 'https://via.placeholder.com/300x200/dc3545/ffffff?text=PS5'),
    ('650e8400-e29b-41d4-a716-446655440012', 'Gaming Headset', '7.1 Surround Sound, RGB Lighting', 129.99, 80, 'Gaming', 'https://via.placeholder.com/300x200/dc3545/ffffff?text=Headset'),
    ('650e8400-e29b-41d4-a716-446655440013', 'Game Controller', 'Wireless, Rechargeable Battery', 69.99, 120, 'Gaming', 'https://via.placeholder.com/300x200/dc3545/ffffff?text=Controller'),
    ('650e8400-e29b-41d4-a716-446655440014', 'Gaming Chair', 'Ergonomic, Lumbar Support, Adjustable', 299.00, 25, 'Gaming', 'https://via.placeholder.com/300x200/dc3545/ffffff?text=Gaming+Chair'),
    
    -- Smart Home
    ('650e8400-e29b-41d4-a716-446655440015', 'Smart Speaker', 'Voice Assistant, Premium Sound', 99.00, 90, 'Smart Home', 'https://via.placeholder.com/300x200/ffc107/ffffff?text=Smart+Speaker'),
    ('650e8400-e29b-41d4-a716-446655440016', 'Smart Light Bulbs', 'Color Changing, 4-Pack, WiFi', 59.99, 200, 'Smart Home', 'https://via.placeholder.com/300x200/ffc107/ffffff?text=Smart+Lights'),
    ('650e8400-e29b-41d4-a716-446655440017', 'Video Doorbell', 'HD Video, Motion Detection, Two-Way Audio', 179.00, 45, 'Smart Home', 'https://via.placeholder.com/300x200/ffc107/ffffff?text=Doorbell'),
    ('650e8400-e29b-41d4-a716-446655440018', 'Smart Thermostat', 'Energy Saving, Voice Control', 249.00, 30, 'Smart Home', 'https://via.placeholder.com/300x200/ffc107/ffffff?text=Thermostat'),
    
    -- Mobile Accessories
    ('650e8400-e29b-41d4-a716-446655440019', 'Phone Case', 'Protective, Clear, MagSafe Compatible', 29.99, 300, 'Mobile', 'https://via.placeholder.com/300x200/17a2b8/ffffff?text=Phone+Case'),
    ('650e8400-e29b-41d4-a716-446655440020', 'Screen Protector', 'Tempered Glass, 3-Pack', 19.99, 250, 'Mobile', 'https://via.placeholder.com/300x200/17a2b8/ffffff?text=Screen+Protector'),
    ('650e8400-e29b-41d4-a716-446655440021', 'Charging Cable', 'USB-C to Lightning, 6ft, Braided', 24.99, 400, 'Mobile', 'https://via.placeholder.com/300x200/17a2b8/ffffff?text=Cable'),
    ('650e8400-e29b-41d4-a716-446655440022', 'Portable Charger', '20000mAh, Fast Charging, Dual USB', 49.99, 150, 'Mobile', 'https://via.placeholder.com/300x200/17a2b8/ffffff?text=Power+Bank');

-- Sample orders
INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at) VALUES
    -- John Doe's orders
    ('750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'DELIVERED', 2248.00, CURRENT_TIMESTAMP - 30, CURRENT_TIMESTAMP - 25),
    ('750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'SHIPPED', 328.99, CURRENT_TIMESTAMP - 5, CURRENT_TIMESTAMP - 4),
    
    -- Jane Smith's orders
    ('750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'PROCESSING', 1998.00, CURRENT_TIMESTAMP - 2, CURRENT_TIMESTAMP - 1),
    ('750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'PENDING', 149.98, CURRENT_TIMESTAMP - 1, CURRENT_TIMESTAMP - 1),
    
    -- Bob Johnson's orders
    ('750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440002', 'DELIVERED', 698.99, CURRENT_TIMESTAMP - 15, CURRENT_TIMESTAMP - 10),
    ('750e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440002', 'CANCELLED', 249.00, CURRENT_TIMESTAMP - 20, CURRENT_TIMESTAMP - 19);

-- Sample order items
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
    -- Order 1 items (John's delivered order - MacBook + AirPods)
    ('850e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', 1, 1999.00),
    ('850e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440004', 1, 249.00),
    
    -- Order 2 items (John's shipped order - Gaming items)
    ('850e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440012', 1, 129.99),
    ('850e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440013', 2, 69.99),
    ('850e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440015', 1, 99.00),
    
    -- Order 3 items (Jane's processing order - iPad)
    ('850e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440002', 2, 799.00),
    ('850e8400-e29b-41d4-a716-446655440007', '750e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440009', 1, 449.00),
    
    -- Order 4 items (Jane's pending order - Accessories)
    ('850e8400-e29b-41d4-a716-446655440008', '750e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440006', 1, 99.99),
    ('850e8400-e29b-41d4-a716-446655440009', '750e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440008', 1, 49.99),
    
    -- Order 5 items (Bob's delivered order - iPhone + Accessories)
    ('850e8400-e29b-41d4-a716-446655440010', '750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440003', 1, 1199.00),
    ('850e8400-e29b-41d4-a716-446655440011', '750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440019', 1, 29.99),
    ('850e8400-e29b-41d4-a716-446655440012', '750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440020', 2, 19.99),
    ('850e8400-e29b-41d4-a716-446655440013', '750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440021', 1, 24.99),
    
    -- Order 6 items (Bob's cancelled order - AirPods)
    ('850e8400-e29b-41d4-a716-446655440014', '750e8400-e29b-41d4-a716-446655440006', '650e8400-e29b-41d4-a716-446655440004', 1, 249.00);

