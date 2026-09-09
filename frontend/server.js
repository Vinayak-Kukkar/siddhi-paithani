const express = require('express');
const path = require('path');
const app = express();

const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8084';

// Serve static images and assets
app.use('/images', express.static(path.join(__dirname, 'images')));
app.use(express.static(path.join(__dirname, 'public')));

// HTML Page Routes mapping
const pages = [
    { route: '/', file: 'index.html' },
    { route: '/shop', file: 'shop.html' },
    { route: '/cart', file: 'cart.html' },
    { route: '/checkout', file: 'checkout.html' },
    { route: '/about', file: 'about.html' },
    { route: '/login', file: 'login.html' },
    { route: '/forgot-password', file: 'forgot-password.html' },
    { route: '/reset-password', file: 'reset-password.html' },
    { route: '/my-orders', file: 'my-orders.html' },
    { route: '/track-order', file: 'track-order.html' },
    { route: '/wishlist', file: 'wishlist.html' },
    { route: '/wallet', file: 'wallet.html' },
    { route: '/gallery', file: 'gallery.html' },
    { route: '/custom-order', file: 'custom-order.html' },
    { route: '/referral', file: 'referral-dashboard.html' },
    { route: '/order-success', file: 'order-success.html' },
    { route: '/invoice', file: 'invoice.html' },
    { route: '/gst-invoice', file: 'gst-invoice.html' },
    { route: '/outfit-assistant', file: 'outfit-assistant.html' },
    { route: '/care-guide/certificate', file: 'care-certificate.html' },
    { route: '/product-details', file: 'product-details.html' },

    // Admin Routes
    { route: '/admin', file: 'admin-dashboard.html' },
    { route: '/admin/login', file: 'admin-login.html' },
    { route: '/admin/products', file: 'admin-products.html' },
    { route: '/admin/product/add', file: 'admin-product-form.html' },
    { route: '/admin/orders', file: 'admin-orders.html' },
    { route: '/admin/coupons', file: 'admin-coupons.html' },
    { route: '/admin/customers', file: 'customers.html' },
    { route: '/admin/custom-requests', file: 'admin-custom-requests.html' },
    { route: '/admin/questions', file: 'admin-questions.html' },
    { route: '/admin/reports', file: 'admin-reports.html' }
];

pages.forEach(p => {
    app.get(p.route, (req, res) => {
        res.sendFile(path.join(__dirname, 'public', p.file));
    });
});

// Fallback for html pages requested directly
app.get('*.html', (req, res) => {
    const filename = path.basename(req.path);
    res.sendFile(path.join(__dirname, 'public', filename));
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({ status: 'UP', service: 'Siddhi Paithani Frontend', backendUrl: BACKEND_URL });
});

app.listen(PORT, () => {
    console.log(`Frontend server running on http://localhost:${PORT}`);
    console.log(`Backend API target configured at: ${BACKEND_URL}`);
});
