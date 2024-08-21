const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/authRoutes');
require('dotenv').config();

const app = express();

// CORS 미들웨어 추가
app.use(cors()); // 모든 도메인에서의 요청을 허용

app.use(express.json());



// 인증 관련 라우트 사용
app.use('/api/auth', authRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
