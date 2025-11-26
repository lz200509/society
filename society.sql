CREATE TABLE students(
	student_id INT(11) PRIMARY KEY AUTO_INCREMENT,
	student_num VARCHAR(20) NOT NULL UNIQUE,
	student_name VARCHAR(20) NOT NULL,
	password VARCHAR(11) NULL,
	phone VARCHAR(11) NULL,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP
	);

CREATE TABLE club_leader(
	leader_id INT(11) PRIMARY KEY AUTO_INCREMENT,
	leader_num VARCHAR(20) NOT NULL UNIQUE,
	leader_name VARCHAR(20) NOT NULL,
	password VARCHAR(11) NULL,
	phone VARCHAR(11) NULL,
	club_id INT(11),
	FOREIGN KEY (club_id) REFERENCES clubs(club_id)
	);
	
	CREATE TABLE clubs(
		club_id INT(11) PRIMARY KEY AUTO_INCREMENT,
		club_name VARCHAR(50) NOT NULL UNIQUE,
		club_type VARCHAR(20) NOT NULL,
		club_intro TEXT NULL,
		total_quota INT(11) NOT NULL,
		remaining_quota INT(11) NOT NULL,
		logo_url VARCHAR(200) NULL
		);
		
		CREATE TABLE club_applications(
			record_id INT(11) PRIMARY KEY AUTO_INCREMENT,
			student_id INT(11) NOT NULL,
			club_id INT(11) NOT NULL,
			apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
			audit_status varchar(20) NOT NULL,
			FOREIGN KEY (student_id) REFERENCES students(student_id),
			FOREIGN KEY (club_id) REFERENCES clubs(club_id),
			UNIQUE KEY unique_student_club (student_id, club_id)
			);
			
INSERT INTO students (student_id, student_num, student_name, password, phone, create_time) VALUES
	(1, '2024001', '张三', ('123456'), '13800138001', '2024-09-01 08:30:00'),
	(2, '2024002', '李四', ('123456'), '13800138002', '2024-09-01 09:15:00'),
	(3, '2024003', '王五', ('123456'), '13800138003', '2024-09-01 10:20:00'),
	(4, '2024004', '赵六', ('123456'), '13800138004', '2024-09-01 11:05:00'),
	(5, '2024005', '钱七', ('123456'), '13800138005', '2024-09-01 14:30:00'),
	(6, '2024006', '孙八', ('123456'), '13800138006', '2024-09-01 15:45:00'),
	(7, '2024007', '周九', ('123456'), '13800138007', '2024-09-02 08:20:00'),
	(8, '2024008', '吴十', ('123456'), '13800138008', '2024-09-02 09:30:00'),
	(9, '2024009', '郑十一', ('123456'), NULL, '2024-09-02 10:40:00'),
	(10, '2024010', '王十二', ('123456'), '13800138010', '2024-09-02 11:50:00');
	
	SELECT * FROM students WHERE student_id = 1;
	
INSERT INTO clubs (club_id, club_name, club_type, club_intro, total_quota, remaining_quota, logo_url) VALUES
	(1, '篮球社', '体育', '热爱篮球运动的同学聚集地，定期组织训练和比赛', 50, 45, '/images/basketball.png'),
	(2, '文学社', '文艺', '文学创作与交流，定期举办读书分享会', 30, 25, '/images/literature.png'),
	(3, '编程社', '学术', '学习编程技术，参与项目开发', 40, 35, '/images/programming.png'),
	(4, '舞蹈社', '文艺', '各类舞蹈教学和表演', 35, 30, '/images/dance.png'),
	(5, '音乐社', '文艺', '乐器演奏和声乐培训', 25, 20, '/images/music.png'),
	(6, '足球社', '体育', '足球爱好者组织，每周训练', 45, 40, '/images/football.png'),
	(7, '英语社', '学术', '英语学习和口语练习', 20, 15, '/images/english.png'),
	(8, '摄影社', '文艺', '摄影技巧学习和外拍活动', 15, 10, '/images/photography.png'),
	(9, '围棋社', '学术', '围棋对弈和棋艺交流', 10, 8, '/images/go.png'),
	(10, '话剧社', '文艺', '话剧排练和演出', 20, 18, '/images/drama.png');
	
INSERT INTO club_leader (leader_id, leader_num, leader_name, password, phone, club_id) VALUES
	(1, 'L2024001', '王大锤', ('leader123'), '13900139001', 1),
	(2, 'L2024002', '李晓明', ('leader123'), '13900139002', 2),
	(3, 'L2024003', '张伟', ('leader123'), '13900139003', 3),
	(4, 'L2024004', '刘美丽', ('leader123'), '13900139004', 4),
	(5, 'L2024005', '陈小春', ('leader123'), '13900139005', 5),
	(6, 'L2024006', '杨过', ('leader123'), '13900139006', 6),
	(7, 'L2024007', '黄蓉', ('leader123'), '13900139007', 7),
	(8, 'L2024008', '郭靖', ('leader123'), '13900139008', 8),
	(9, 'L2024009', '小龙女', ('leader123'), '13900139009', 9),
	(10, 'L2024010', '令狐冲', ('leader123'), '13900139010', 10);
	
INSERT INTO club_applications (record_id, student_id, club_id, apply_time, audit_status) VALUES
	(1, 1, 1, '2024-09-03 10:00:00', '已通过'),
	(2, 1, 3, '2024-09-03 10:05:00', '已通过'),
	(3, 2, 1, '2024-09-03 11:20:00', '已通过'),
	(4, 2, 2, '2024-09-03 11:25:00', '待审核'),
	(5, 3, 4, '2024-09-03 14:30:00', '已通过'),
	(6, 3, 5, '2024-09-03 14:35:00', '已拒绝'),
	(7, 4, 6, '2024-09-04 09:15:00', '已通过'),
	(8, 4, 7, '2024-09-04 09:20:00', '待审核'),
	(9, 5, 8, '2024-09-04 10:45:00', '已通过'),
	(10, 5, 9, '2024-09-04 10:50:00', '待审核'),
	(11, 6, 1, '2024-09-04 11:30:00', '已通过'),
	(12, 6, 10, '2024-09-04 11:35:00', '已通过'),
	(13, 7, 2, '2024-09-04 14:20:00', '已通过'),
	(14, 7, 3, '2024-09-04 14:25:00', '待审核'),
	(15, 8, 4, '2024-09-05 08:40:00', '已通过'),
	(16, 8, 6, '2024-09-05 08:45:00', '已拒绝'),
	(17, 9, 5, '2024-09-05 10:10:00', '已通过'),
	(18, 9, 7, '2024-09-05 10:15:00', '待审核'),
	(19, 10, 8, '2024-09-05 11:25:00', '已通过'),
	(20, 10, 9, '2024-09-05 11:30:00', '已通过');