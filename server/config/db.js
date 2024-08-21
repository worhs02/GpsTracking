const mariadb = require('mariadb');

// MariaDB 연결 설정
const pool = mariadb.createPool({
    host: 'semper16paratus06.iptime.org',      // MariaDB 서버 URL 또는 IP
    user: 'song',        // MariaDB 사용자 이름
    password: '1q2w3e4r@S',    // MariaDB 비밀번호
    database: 'GpsTracking', // 사용할 데이터베이스 이름
    connectionLimit: 10 ,            // 최대 연결 수
    port: 3306   
});

module.exports = pool;