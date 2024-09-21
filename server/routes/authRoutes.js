const express = require('express');
const router = express.Router();
const { register, login, updateTag, locationUpdate } = require('../controllers/authController');

// 사용자 등록 라우트
router.post('/register', register);

// 사용자 로그인 라우트
router.post('/login', login);

// 태그 업데이트 라우트
router.put('/update-tag', updateTag);

router.post('/locationUpdate', locationUpdate)

module.exports = router;
