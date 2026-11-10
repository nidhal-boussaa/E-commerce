
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
            `SELECT 
                p.*,
                c.name AS category
             FROM products p
             LEFT JOIN categories c
             ON p.category_id = c.id
             WHERE p.id = ?`,
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





const { verifyToken, verifyAdmin } = require('./middleware/auth');

app.get('/api/profile', verifyToken, async (req, res) => {
    try {
        const [users] = await db.query(
            `SELECT id, full_name, email, phone, address, created_at, role
             FROM users
             WHERE id = ?`,
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                message: 'Utilisateur introuvable'
            });
        }

        res.json({
            message: 'Profil récupéré avec succès',
            user: users[0]
        });

    } catch (error) {
        res.status(500).json({
            message: error.message
        });
    }
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

app.put('/api/profile', verifyToken, async (req, res) => {
    try {
        const { full_name, email, phone } = req.body;

        if (!full_name || !email) {
            return res.status(400).json({
                message: 'Le nom et l’email sont obligatoires'
            });
        }

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        const [existingUsers] = await db.query(
            'SELECT id FROM users WHERE email = ? AND id != ?',
            [email, req.user.id]
        );

        if (existingUsers.length > 0) {
            return res.status(409).json({
                message: 'Cette adresse email est déjà utilisée'
            });
        }

        await db.query(
            `UPDATE users
             SET full_name = ?, email = ?, phone = ?
             WHERE id = ?`,
            [full_name.trim(), email.trim(), phone || null, req.user.id]
        );

        res.json({
            message: 'Profil modifié avec succès'
        });

    } catch (error) {
        res.status(500).json({
            message: error.message
        });
    }
});
// Récupérer les adresses du client connecté
app.get('/api/profile/addresses', verifyToken, async (req, res) => {
    try {
        const userId = req.user.id;

        // Adresse principale
        const [users] = await db.query(
            'SELECT address FROM users WHERE id = ?',
            [userId]
        );

        if (users.length === 0) {
            return res.status(404).json({
                message: 'Utilisateur introuvable'
            });
        }

        const addresses = [];
        const seen = new Set();

        // Ajouter l'adresse principale
        const mainAddress = users[0].address?.trim();

        if (mainAddress) {
            addresses.push({
                address: mainAddress,
                is_default: true
            });

            seen.add(mainAddress.toLowerCase());
        }

        // Récupérer les adresses des commandes
        const [orders] = await db.query(
            `SELECT DISTINCT delivery_address
             FROM orders
             WHERE user_id = ?
             AND delivery_address IS NOT NULL
             AND TRIM(delivery_address) <> ''`,
            [userId]
        );

        // Éviter les doublons
        for (const order of orders) {
            const address = order.delivery_address.trim();
            const key = address.toLowerCase();

            if (!seen.has(key)) {
                addresses.push({
                    address: address,
                    is_default: false
                });

                seen.add(key);
            }
        }

        res.json({
            message: 'Adresses récupérées avec succès',
            addresses: addresses
        });

    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: 'Erreur lors de la récupération des adresses'
        });
    }
});

// Ajouter une adresse principale
app.post('/api/profile/address', verifyToken, async (req, res) => {
    try {
        const { address, city, postal_code, country } = req.body;

        if (
            typeof address !== 'string' || !address.trim() ||
            typeof city !== 'string' || !city.trim() ||
            typeof postal_code !== 'string' || !postal_code.trim() ||
            typeof country !== 'string' || !country.trim()
        ) {
            return res.status(400).json({
                message: 'Tous les champs sont obligatoires'
            });
        }

        const [users] = await db.query(
            'SELECT address FROM users WHERE id = ?',
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                message: 'Utilisateur introuvable'
            });
        }

        if (users[0].address && users[0].address.trim()) {
            return res.status(409).json({
                message: 'Une adresse principale existe déjà'
            });
        }

        const fullAddress = [
            address.trim(),
            city.trim(),
            postal_code.trim(),
            country.trim()
        ].join(', ');

        await db.query(
            'UPDATE users SET address = ? WHERE id = ?',
            [fullAddress, req.user.id]
        );

        res.status(201).json({
            message: 'Adresse principale ajoutée avec succès',
            address: fullAddress
        });

    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: 'Erreur lors de l’ajout de l’adresse'
        });
    }
});

// Modifier l'adresse principale
app.put('/api/profile/address', verifyToken, async (req, res) => {
    try {
        const { address, city, postal_code, country } = req.body;

        if (
            typeof address !== 'string' || !address.trim() ||
            typeof city !== 'string' || !city.trim() ||
            typeof postal_code !== 'string' || !postal_code.trim() ||
            typeof country !== 'string' || !country.trim()
        ) {
            return res.status(400).json({
                message: 'Tous les champs sont obligatoires'
            });
        }

        const [users] = await db.query(
            'SELECT address FROM users WHERE id = ?',
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                message: 'Utilisateur introuvable'
            });
        }

        if (!users[0].address || !users[0].address.trim()) {
            return res.status(404).json({
                message: 'Aucune adresse principale à modifier'
            });
        }

        const fullAddress = [
            address.trim(),
            city.trim(),
            postal_code.trim(),
            country.trim()
        ].join(', ');

        await db.query(
            'UPDATE users SET address = ? WHERE id = ?',
            [fullAddress, req.user.id]
        );

        res.json({
            message: 'Adresse principale modifiée avec succès',
            address: fullAddress
        });

    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: 'Erreur lors de la modification de l’adresse'
        });
    }
});



// Récupérer les commandes du client connecté
app.get('/api/orders/my-orders', verifyToken, async (req, res) => {
    try {
        const userId = req.user.id;

        // Récupérer les commandes
        const [orders] = await db.query(
            `SELECT id, total_amount, status, delivery_address,
                    payment_method, created_at
             FROM orders
             WHERE user_id = ?
             ORDER BY created_at DESC`,
            [userId]
        );

        // Récupérer les produits de chaque commande
        for (const order of orders) {
            const [items] = await db.query(
                `SELECT id, quantity, unit_price, product_name,
                        product_id
                 FROM order_items
                 WHERE order_id = ?`,
                [order.id]
            );

            order.items = items;
        }

        res.json({
            message: 'Commandes récupérées avec succès',
            orders: orders
        });

    } catch (error) {
        console.error('Erreur commandes :', error);

        res.status(500).json({
            message: 'Erreur lors de la récupération des commandes'
        });
    }
});



const PORT = process.env.PORT || 3000;

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});