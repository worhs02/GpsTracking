const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const User = require('../models/userModel');


// 사용자 등록 처리
const register = async (req, res) => {
    const { username, password, email } = req.body;

    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        console.log('hashpPP:', hashedPassword);
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
    const { email, password } = req.body;

    try {
        // 이메일로 사용자 찾기
        const users = await User.findByUseremail(email);


        console.log('Users found:', users);

        // 사용자가 존재하지 않으면 오류 반환
        if (users.length === 0) {
            return res.status(400).json({ message: 'Invalid email' });
        }

        // 비밀번호가 일치하지 않으면 오류 반환
        const isPasswordValid = await bcrypt.compare(password, users.password);
        if (!isPasswordValid) {
            console.log('informatin confirmed:', users.password);
            return res.status(400).json({ message: 'Invalid password' });
        }


        // JWT 토큰 생성
        const token = jwt.sign(
            { id: users.id, email: users.email },
            process.env.JWT_SECRET, // 환경 변수로 비밀 키 읽기
            { expiresIn: '1h' }
        );
        console.log('Generated token:', token);

        // 토큰을 응답으로 보내기
        res.json({ token });
        console.log('Login successful');

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

//일정 생성
const calendarCreate = async (req, res) => {
    const { userId, selectedDate, content } = req.body; // 생성할 필드

    try {
        // 새로운 이벤트 삽입 쿼리
        const [result] = await pool.query(
            `INSERT INTO events (user_id, selectedDate, content)
             VALUES (?, ?, ?)`,
            [userId, selectedDate,content]
        );

        // 성공적으로 삽입된 경우
        res.status(201).json({ message: 'Event created successfully', eventId: result.insertId });
    } catch (error) {
        console.error('Error creating event:', error);
        res.status(500).json({ message: 'Failed to create event' });
    }
};




module.exports = { register, login, updateTag };
