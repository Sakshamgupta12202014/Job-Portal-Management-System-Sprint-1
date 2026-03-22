-- ============================================================
--  Job Portal Management System — Database Setup Script
--  Run this ONCE before starting any service.
--  MySQL 8.x compatible
-- ============================================================

-- 1. Auth Service database (users table)
CREATE DATABASE IF NOT EXISTS jobportal_auth_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Job Service database (jobs table)
CREATE DATABASE IF NOT EXISTS jobportal_job_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Application Service database (applications table)
CREATE DATABASE IF NOT EXISTS jobportal_app_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- NOTE: Tables are created automatically by Hibernate
--       (ddl-auto=update in each service's application.yml).
--       Only the databases need to be created manually.
-- ============================================================

-- ============================================================
-- OPTIONAL: Manually create tables (if you prefer ddl-auto=validate)
-- ============================================================

USE jobportal_auth_db;

CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    name                VARCHAR(100)    NOT NULL,
    email               VARCHAR(150)    NOT NULL UNIQUE,
    password            VARCHAR(255)    NOT NULL,          -- BCrypt hashed ONLY
    role                ENUM('JOB_SEEKER','RECRUITER','ADMIN') NOT NULL,
    phone               VARCHAR(20),
    status              ENUM('ACTIVE','INACTIVE','BANNED') NOT NULL DEFAULT 'ACTIVE',
    profile_picture_url TEXT,
    resume_url          TEXT,
    refresh_token       TEXT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE jobportal_job_db;

CREATE TABLE IF NOT EXISTS jobs (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    title            VARCHAR(200)    NOT NULL,
    company_name     VARCHAR(150)    NOT NULL,
    location         VARCHAR(150)    NOT NULL,
    salary_min       DECIMAL(12,2),
    salary_max       DECIMAL(12,2),
    experience_years INT,
    job_type         ENUM('FULL_TIME','PART_TIME','REMOTE','CONTRACT') NOT NULL,
    skills_required  TEXT,
    description      TEXT            NOT NULL,
    status           ENUM('ACTIVE','CLOSED','DRAFT','DELETED') NOT NULL DEFAULT 'ACTIVE',
    deadline         DATE,
    posted_by        BIGINT          NOT NULL,              -- FK → users.id (recruiter)
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE jobportal_app_db;

CREATE TABLE IF NOT EXISTS applications (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    user_id        BIGINT   NOT NULL,                      -- FK → users.id (seeker)
    job_id         BIGINT   NOT NULL,                      -- FK → jobs.id
    resume_url     TEXT     NOT NULL,
    cover_letter   TEXT,
    status         ENUM('APPLIED','UNDER_REVIEW','SHORTLISTED','REJECTED') NOT NULL DEFAULT 'APPLIED',
    recruiter_note TEXT,                                   -- internal only, never shown to seeker
    applied_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_job UNIQUE (user_id, job_id)        -- one application per job per user
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
