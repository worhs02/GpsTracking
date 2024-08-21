const mariadb = require('mariadb');

// MariaDB 연결 설정
const pool = mariadb.createPool({
    host: 'semper16paratus06.iptime.org', // MariaDB 서버 URL 또는 IP
    user: 'song',                        // MariaDB 사용자 이름
    password: '1q2w3e4r@S',              // MariaDB 비밀번호
    connectionLimit: 10,                 // 최대 연결 수
    port: 3306                           // MariaDB 기본 포트
});

// 데이터베이스 및 테이블 생성 함수
async function initializeDatabase() {
    let conn;
    try {
        conn = await pool.getConnection();
        
        // 'GpsTracking' 데이터베이스가 있는지 확인
        await conn.query(`CREATE DATABASE IF NOT EXISTS GpsTracking`);
        
        // GpsTracking 데이터베이스 선택
        await conn.query(`USE GpsTracking`);
        
        // users 테이블 생성 (필요한 경우)
        await conn.query(`
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            )
        `);

        console.log("Database and tables initialized successfully!");
    } catch (err) {
        console.error("Error during database initialization:", err);
    } finally {
        if (conn) conn.release(); // 연결 종료
    }
}

// 데이터베이스 초기화 함수 호출
initializeDatabase();

module.exports = pool;
