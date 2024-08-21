const db = require('../config/db');

// 사용자 찾기
const findByUsername = async (username) => {
    const [rows] = await db.query('SELECT * FROM users WHERE username = ?', [username]);
    return rows;
};

// 사용자 생성
const create = async (username, password) => {
    const [result] = await db.query('INSERT INTO users (username, password) VALUES (?, ?)', [username, password]);
    return result.insertId;
};

// 사용자 업데이트
const updateTag = async (userId, tagNum) => {
    await db.query('UPDATE users SET tag = ? WHERE id = ?', [tagNum, userId]);
};

module.exports = { findByUsername, create, updateTag };
