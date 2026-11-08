
const express = require('express');
const cors = require('cors');
const db = require('./config/database');
require('dotenv').config();

const app = express();
app.use((req, res, next) => {
    console.log("REQUEST:", req.method, req.url);
    next();
});

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
    res.send('E-commerce API is running');
});

app.get('/api/test-db', async (req, res) => {
    try {
        const [rows] = await db.query('SELECT 1 AS test');

        res.status(200).json({
            success: true,
            message: 'Connexion à la base de données réussie',
            data: rows
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Erreur de connexion à la base de données',
            error: error.message
        });
    }
});




app.get('/api/products', async (req, res) => {
    try {
        const [products] = await db.query(`
            SELECT 
                p.id,
                p.name,
                p.description,
                p.price,
                p.stock,
                p.image_url,
                p.is_active,
                c.name AS category
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.is_active = 1
        `);

        res.json(products);

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


app.get('/api/categories', async (req, res) => {
    try {
        const [categories] = await db.query(
            'SELECT * FROM categories'
        );

        res.json(categories);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


app.get('/api/products/:id', async (req, res) => {
    try {
        const [products] = await db.query(
            'SELECT * FROM products WHERE id = ?',
            [req.params.id]
        );

        if (products.length === 0) {
            return res.status(404).json({
                message: 'Product not found'
            });
        }

        res.json(products[0]);

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


const bcrypt = require('bcrypt');

app.post('/api/auth/register', async (req, res) => {
    try {
        const { full_name, email, password, phone, address } = req.body;

        if (!full_name || !email || !password) {
            return res.status(400).json({
                message: 'Tous les champs obligatoires sont requis'
            });
        }

        const [existing] = await db.query(
            'SELECT id FROM users WHERE email = ?',
            [email]
        );

        if (existing.length > 0) {
            return res.status(409).json({
                message: 'Email déjà utilisé'
            });
        }

        const passwordHash = await bcrypt.hash(password, 10);

        const [result] = await db.query(
            `INSERT INTO users
            (full_name, email, password_hash, phone, address, role)
            VALUES (?, ?, ?, ?, ?, 'CLIENT')`,
            [full_name, email, passwordHash, phone || null, address || null]
        );

        res.status(201).json({
            message: 'Compte créé avec succès',
            userId: result.insertId
        });

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});



const jwt = require('jsonwebtoken');

app.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const [users] = await db.query(
            'SELECT * FROM users WHERE email = ?',
            [email]
        );

        if (users.length === 0) {
            return res.status(401).json({
                message: 'Email ou mot de passe incorrect'
            });
        }

        const user = users[0];

        const validPassword = await bcrypt.compare(
            password,
            user.password_hash
        );

        if (!validPassword) {
            return res.status(401).json({
                message: 'Email ou mot de passe incorrect'
            });
        }

        const token = jwt.sign(
            { id: user.id, role: user.role },
            process.env.JWT_SECRET,
            { expiresIn: '1d' }
        );

        res.json({
            message: 'Login réussi',
            token,
            user: {
                id: user.id,
                full_name: user.full_name,
                email: user.email,
                role: user.role
            }
        });

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});




const { verifyToken, verifyAdmin } = require('./middleware/auth');app.get('/api/profile', verifyToken, async (req, res) => {
    res.json({
        message: 'Accès autorisé',
        user: req.user
    });
});


app.post('/api/products', verifyToken, verifyAdmin, async (req, res) => {
    try {
        const { name, description, price, stock, image_url, category_id } = req.body;

        if (!name || price == null || stock == null || !category_id) {
            return res.status(400).json({ message: 'Champs obligatoires manquants' });
        }

        const [result] = await db.query(
            `INSERT INTO products
            (name, description, price, stock, image_url, category_id)
            VALUES (?, ?, ?, ?, ?, ?)`,
            [name, description || null, price, stock, image_url || null, category_id]
        );

        res.status(201).json({
            message: 'Produit ajouté',
            productId: result.insertId
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.put('/api/products/:id', verifyToken, verifyAdmin, async (req, res) => {
    try {
        const { name, description, price, stock, image_url, category_id } = req.body;

        const [result] = await db.query(
            `UPDATE products
             SET name = ?, description = ?, price = ?,
                 stock = ?, image_url = ?, category_id = ?
             WHERE id = ?`,
            [name, description || null, price, stock,
             image_url || null, category_id, req.params.id]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: 'Produit introuvable' });
        }

        res.json({ message: 'Produit modifié avec succès' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.delete('/api/products/:id', verifyToken, verifyAdmin, async (req, res) => {
    try {
        const [result] = await db.query(
            'UPDATE products SET is_active = 0 WHERE id = ?',
            [req.params.id]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: 'Produit introuvable' });
        }

        res.json({ message: 'Produit désactivé avec succès' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});



const PORT = process.env.PORT || 3000;

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});