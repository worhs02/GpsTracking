const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const User = require('../models/userModel');

// 사용자 등록 처리
const register = async (req, res) => {
    const { username, password, email } = req.body;

    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        const newUser = await User.create({
            username,
            password: hashedPassword,
            email
        });

        // BigInt를 문자열로 변환
        const userId = newUser.insertId.toString();

        res.status(201).json({ message: 'User created successfully', userId });
    } catch (error) {
        console.error('Error during registration:', error);
        res.status(500).json({ message: 'Registration failed' });
    }
};




// 사용자 로그인 처리
const login = async (req, res) => {
    const { username, password } = req.body;

    try {
        const user = await User.findByUsername(username);
        if (user.length === 0 || !await bcrypt.compare(password, user[0].password)) {
            return res.status(400).json({ message: 'Invalid username or password' });
        }

        const token = jwt.sign({ id: user[0].id, username: user[0].username }, 'your_jwt_secret', { expiresIn: '1h' });
        res.json({ token });
    } catch (err) {
        console.error('Error during login:', err);
        res.status(500).json({ message: 'Login failed' });
    }
};

// 태그 업데이트 처리
const updateTag = async (req, res) => {
    const { userId, tagNum } = req.body;

    try {
        await User.updateTag(userId, tagNum);
        res.status(200).json({ message: 'Tag number updated successfully' });
    } catch (err) {
        console.error('Error updating tag:', err);
        res.status(500).json({ message: 'Failed to update tag' });
    }
};

module.exports = { register, login, updateTag };
