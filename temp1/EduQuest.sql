-- 1. ENUM cho vai trò người dùng
CREATE TYPE user_role AS ENUM ('ADMIN', 'INSTRUCTOR', 'STUDENT');

-- 2. Hàm cập nhật updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Bảng USERS (chỉ lưu thông tin chung, dùng username để đăng nhập)
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username TEXT UNIQUE NOT NULL,          -- Đăng nhập bằng username, duy nhất
  name TEXT NOT NULL,
  email TEXT UNIQUE,                      -- Không bắt buộc, có thể dùng cho reset password, thông báo
  password TEXT NOT NULL,
  role user_role NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  avatar_url TEXT,
  last_login_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger updated_at cho users
CREATE TRIGGER trigger_update_users
  BEFORE UPDATE ON users
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 4. Bảng STUDENTS (chỉ dành cho user có role = 'STUDENT')
CREATE TABLE students (
  id SERIAL PRIMARY KEY,
  user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  student_code TEXT UNIQUE NOT NULL,      -- Mã số sinh viên, duy nhất
  faculty TEXT,                           -- Ví dụ: Khoa CNTT
  class_code TEXT,                        -- Mã lớp, nếu cần
  enrolled_year INT,                      -- Năm nhập học
  birth_date DATE
);

-- 5. Bảng SUBJECTS
CREATE TABLE subjects (
  id SMALLSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_subjects
  BEFORE UPDATE ON subjects
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 6. Bảng CLASSES
CREATE TABLE classes (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  subject_id SMALLINT REFERENCES subjects(id) ON DELETE SET NULL,
  instructor_id INT REFERENCES users(id) ON DELETE SET NULL,
  semester TEXT,                           -- Học kỳ (nếu cần)
  year INT,                                -- Năm học (nếu cần)
  class_code TEXT,                         -- Mã lớp (nếu cần)
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_classes
  BEFORE UPDATE ON classes
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 7. Bảng ENROLLMENTS (đăng ký lớp học)
CREATE TABLE enrollments (
  id SERIAL PRIMARY KEY,
  class_id INT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
  student_id INT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
  status TEXT DEFAULT 'ENROLLED',         -- Trạng thái (ENROLLED, CANCELLED, ...)
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (class_id, student_id)           -- Không cho trùng một sinh viên trong cùng một lớp
);

-- 8. Bảng EXERCISE_TYPES
CREATE TABLE exercise_types (
  id SMALLSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  admin_id INT REFERENCES users(id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_exercise_types
  BEFORE UPDATE ON exercise_types
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 9. Bảng EXERCISES
CREATE TABLE exercises (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  instructor_id INT REFERENCES users(id) ON DELETE SET NULL,
  subject_id SMALLINT REFERENCES subjects(id) ON DELETE SET NULL,
  exercise_type_id SMALLINT REFERENCES exercise_types(id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_exercises
  BEFORE UPDATE ON exercises
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 10. Bảng EXERCISE_DETAILS (mỗi bài có thể có nhiều phần nhỏ)
CREATE TABLE exercise_details (
  id SERIAL PRIMARY KEY,
  exercise_id INT NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
  content TEXT,
  "order" INT,                            -- Thứ tự hiển thị trong bài (optional)
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_exercise_details
  BEFORE UPDATE ON exercise_details
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 11. Bảng QUESTION_STRUCTURES
CREATE TABLE question_structures (
  id SMALLSERIAL PRIMARY KEY,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_question_structures
  BEFORE UPDATE ON question_structures
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 12. Bảng QUESTIONS
CREATE TABLE questions (
  id SERIAL PRIMARY KEY,
  exercise_detail_id INT REFERENCES exercise_details(id) ON DELETE CASCADE,
  content TEXT,
  structure_id SMALLINT REFERENCES question_structures(id) ON DELETE SET NULL,
  "order" INT,                           -- Thứ tự câu hỏi
  point NUMERIC(5,2) DEFAULT 1.00,       -- Điểm cho mỗi câu hỏi (có thể thay đổi)
  type TEXT,                             -- Loại câu hỏi: MCQ, Essay...
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_questions
  BEFORE UPDATE ON questions
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 13. Bảng ANSWERS (các đáp án cho câu hỏi)
CREATE TABLE answers (
  id SERIAL PRIMARY KEY,
  question_id INT REFERENCES questions(id) ON DELETE CASCADE,
  content TEXT,
  is_correct BOOLEAN DEFAULT FALSE,       -- Đáp án đúng/sai
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_answers
  BEFORE UPDATE ON answers
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 14. Bảng ANSWER_DETAILS (thông tin chi tiết đáp án, nếu có)
CREATE TABLE answer_details (
  id SERIAL PRIMARY KEY,
  answer_id INT NOT NULL REFERENCES answers(id) ON DELETE CASCADE,
  content TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_answer_details
  BEFORE UPDATE ON answer_details
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 15. Bảng PARTICIPATIONS (mỗi lượt sinh viên làm bài)
CREATE TABLE participations (
  id SERIAL PRIMARY KEY,
  student_id INT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
  exercise_id INT NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
  status TEXT DEFAULT 'IN_PROGRESS',      -- Trạng thái: IN_PROGRESS, SUBMITTED, ...
  submitted_at TIMESTAMP,
  start_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 16. Bảng SUBMISSION_RESULTS (kết quả nộp bài)
CREATE TABLE submission_results (
  id SERIAL PRIMARY KEY,
  participation_id INT NOT NULL REFERENCES participations(id) ON DELETE CASCADE,
  score NUMERIC(5,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. Bảng SUBMISSION_ANSWERS (kết quả từng câu hỏi của mỗi lượt nộp)
CREATE TABLE submission_answers (
  id SERIAL PRIMARY KEY,
  submission_result_id INT NOT NULL REFERENCES submission_results(id) ON DELETE CASCADE,
  question_id INT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
  answer_id INT REFERENCES answers(id) ON DELETE SET NULL,
  content TEXT,                              -- Đáp án tự luận
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (submission_result_id, question_id) -- Một câu hỏi chỉ có 1 đáp án cho 1 lần nộp
);

-- 18. Bảng DISCUSSIONS (thảo luận cho mỗi bài tập)
CREATE TABLE discussions (
  id SERIAL PRIMARY KEY,
  exercise_id INT REFERENCES exercises(id) ON DELETE CASCADE,
  content TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_discussions
  BEFORE UPDATE ON discussions
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 19. Bảng DISCUSSION_COMMENTS (bình luận trong mỗi thảo luận)
CREATE TABLE discussion_comments (
  id SERIAL PRIMARY KEY,
  discussion_id INT NOT NULL REFERENCES discussions(id) ON DELETE CASCADE,
  student_id INT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
  content TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trigger_update_discussion_comments
  BEFORE UPDATE ON discussion_comments
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 20. Chỉ mục quan trọng
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_students_code ON students(student_code);
CREATE INDEX idx_enrollments_class_student ON enrollments(class_id, student_id);
CREATE INDEX idx_questions_exercise_detail ON questions(exercise_detail_id);

