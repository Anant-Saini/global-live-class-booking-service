-- 1. Users Table (Shared Entity)
CREATE TABLE IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       timezone_id VARCHAR(50) NOT NULL
);

-- 2. Courses Table
CREATE TABLE IF NOT EXISTS courses  (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT
);

-- 3. Offerings Table
CREATE TABLE IF NOT EXISTS offerings (
                           id BIGSERIAL PRIMARY KEY,
                           course_id BIGINT NOT NULL,
                           teacher_id BIGINT NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
                           currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                           total_seats INTEGER NOT NULL,
                           booked_seats INTEGER DEFAULT 0,
                           version BIGINT DEFAULT 0,
                           CONSTRAINT fk_course_offering FOREIGN KEY (course_id) REFERENCES courses(id),
                           CONSTRAINT fk_teacher_offering FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- 4. Sessions Table
CREATE TABLE IF NOT EXISTS sessions (
                          id BIGSERIAL PRIMARY KEY,
                          offering_id BIGINT NOT NULL,
                          start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                          end_time TIMESTAMP WITH TIME ZONE NOT NULL,
                          CONSTRAINT fk_offering_session FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
    );

-- 5. Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          offering_id BIGINT NOT NULL,
                          booked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          booking_price DECIMAL(12, 2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          CONSTRAINT fk_user_booking FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT fk_offering_booking FOREIGN KEY (offering_id) REFERENCES offerings(id),
                          CONSTRAINT unique_user_offering UNIQUE(user_id, offering_id)
);