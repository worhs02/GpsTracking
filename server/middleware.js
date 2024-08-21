const express = require('express');
const app = express();

// JSON 요청 본문을 파싱하는 미들웨어 설정
app.use(express.json());

module.exports = app;
