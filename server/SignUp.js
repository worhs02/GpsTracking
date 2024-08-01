const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const app = express();
const port = 3306;

app.use(bodyParser.json());

const db = mysql.createConnection({
    host: 'semper16paratus06.iptime.org',
    user: 'song',
    password: '1q2w3e4r@S',
    database: 'signupDB'
});

db.connect((err) => {
    if (err) throw err;
    console.log('MySQL Connected...');
});


app.listen(port, () => {
    console.log(`Server started on port ${port}`);
});
