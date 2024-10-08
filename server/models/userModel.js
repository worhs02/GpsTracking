const db = require('../config/db');

const findByUseremail = async (email) => {
    let conn;
    try {
        conn = await db.getConnection(); // 커넥션 가져오기
        await conn.query('USE GpsTracking'); // 데이터베이스 선택 확인
        const [rows] = await conn.query('SELECT * FROM users WHERE email = ?', [email]);
        return rows
    } catch (err) {
        console.error('Error querying database:', err);
        throw err; // 에러를 호출자에게 전달
    } finally {
        if (conn) conn.release(); // 커넥션 반환
    }
};
// 사용자 찾기
const findByUsername = async (username) => {
    const [rows] = await db.query('SELECT * FROM users WHERE username= ?', [username]);
    return rows;
};

async function create(user) {
    let conn;
    try {
        conn = await db.getConnection(); // pool 대신 db 사용
        await conn.query('USE GpsTracking'); // 데이터베이스 선택 확인
        // 이메일 추가
        const result = await conn.query('INSERT INTO users (username, password, email) VALUES (?, ?, ?)', 
            [user.username, user.password, user.email]); // 이메일 필드 추가
        return result;
    } catch (err) {
        throw err;
    } finally {
        if (conn) conn.release(); // 연결 반환 (end 대신 release 사용)
    }
}

// 사용자 업데이트
const updateTag = async (userId, tagNum) => {
    await db.query('UPDATE users SET tag = ? WHERE id = ?', [tagNum, userId]);
};

module.exports = { findByUseremail, findByUsername, create, updateTag };
