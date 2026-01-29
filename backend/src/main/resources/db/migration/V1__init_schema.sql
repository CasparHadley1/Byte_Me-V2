-- Byte Me Database Schema


-- Enums
CREATE TYPE user_role AS ENUM ('SELLER', 'ORG_ADMIN', 'EMPLOYEE', 'MAINTAINER');
CREATE TYPE posting_status AS ENUM ('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED');
CREATE TYPE reservation_status AS ENUM ('RESERVED', 'COLLECTED', 'NO_SHOW', 'EXPIRED', 'CANCELLED');
CREATE TYPE issue_type AS ENUM ('UNAVAILABLE', 'QUALITY', 'OTHER');
CREATE TYPE issue_status AS ENUM ('OPEN', 'RESPONDED', 'RESOLVED');


-- Users & Auth
CREATE TABLE user_account (
   user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   email VARCHAR(255) NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,
   role user_role NOT NULL,
   created_at TIMESTAMPTZ DEFAULT NOW()
);


CREATE TABLE organisation (
   org_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   name VARCHAR(255) NOT NULL,
   location_text VARCHAR(500),
   billing_stub VARCHAR(255),
   created_at TIMESTAMPTZ DEFAULT NOW()
);


CREATE TABLE seller (
   seller_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   user_id UUID NOT NULL UNIQUE REFERENCES user_account(user_id),
   name VARCHAR(255) NOT NULL,
   location_text VARCHAR(500),
   opening_hours_text VARCHAR(500),
   contact_stub VARCHAR(255),
   created_at TIMESTAMPTZ DEFAULT NOW()
);


CREATE TABLE employee (
   employee_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   org_id UUID REFERENCES organisation(org_id),
   user_id UUID UNIQUE REFERENCES user_account(user_id),
   display_name VARCHAR(255),
   current_streak_weeks INT DEFAULT 0,
   best_streak_weeks INT DEFAULT 0,
   last_rescue_week_start DATE,
   created_at TIMESTAMPTZ DEFAULT NOW()
);


-- Bundles & Categories
CREATE TABLE category (
   category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   name VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE bundle_posting (
   posting_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   seller_id UUID NOT NULL REFERENCES seller(seller_id),
   category_id UUID REFERENCES category(category_id),
   pickup_start_at TIMESTAMPTZ NOT NULL,
   pickup_end_at TIMESTAMPTZ NOT NULL,
   quantity_total INT DEFAULT 1,
   quantity_reserved INT DEFAULT 0,
   price_cents INT NOT NULL,
   discount_pct INT DEFAULT 0,
   contents_text TEXT,
   allergens_text VARCHAR(500),
   status posting_status DEFAULT 'DRAFT',
   estimated_weight_grams INT,
   created_at TIMESTAMPTZ DEFAULT NOW()
);


-- Reservations & Rescues
CREATE TABLE reservation (
   reservation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   posting_id UUID NOT NULL REFERENCES bundle_posting(posting_id),
   org_id UUID REFERENCES organisation(org_id),
   employee_id UUID REFERENCES employee(employee_id),
   reserved_at TIMESTAMPTZ DEFAULT NOW(),
   status reservation_status DEFAULT 'RESERVED',
   claim_code_hash VARCHAR(255),
   claim_code_last4 VARCHAR(4),
   collected_at TIMESTAMPTZ,
   no_show_marked_at TIMESTAMPTZ,
   expired_marked_at TIMESTAMPTZ
);


CREATE TABLE rescue_event (
   event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   employee_id UUID NOT NULL REFERENCES employee(employee_id),
   reservation_id UUID NOT NULL UNIQUE REFERENCES reservation(reservation_id),
   collected_at TIMESTAMPTZ DEFAULT NOW(),
   meals_estimate INT,
   co2e_estimate_grams INT
);


-- Issues
CREATE TABLE issue_report (
   issue_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   posting_id UUID REFERENCES bundle_posting(posting_id),
   reservation_id UUID REFERENCES reservation(reservation_id),
   employee_id UUID REFERENCES employee(employee_id),
   type issue_type NOT NULL,
   description TEXT NOT NULL,
   status issue_status DEFAULT 'OPEN',
   seller_response TEXT,
   created_at TIMESTAMPTZ DEFAULT NOW(),
   resolved_at TIMESTAMPTZ
);


-- Gamification
CREATE TABLE badge (
   badge_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   code VARCHAR(50) NOT NULL UNIQUE,
   name VARCHAR(100) NOT NULL,
   description TEXT
);


CREATE TABLE employee_badge (
   employee_id UUID REFERENCES employee(employee_id),
   badge_id UUID REFERENCES badge(badge_id),
   awarded_at TIMESTAMPTZ DEFAULT NOW(),
   PRIMARY KEY (employee_id, badge_id)
);


-- Seed Data
INSERT INTO category (name) VALUES
   ('Bakery'), ('Produce'), ('Dairy'), ('Prepared Meals'), ('Groceries'), ('Beverages'), ('Mixed');


INSERT INTO badge (code, name, description) VALUES
   ('FIRST_RESCUE', 'First Rescue', 'Completed your first food rescue'),
   ('STREAK_4', '4-Week Streak', 'Rescued food for 4 consecutive weeks'),
   ('STREAK_12', '12-Week Streak', 'Rescued food for 12 consecutive weeks'),
   ('RESCUES_10', '10 Rescues', 'Completed 10 food rescues'),
   ('RESCUES_50', '50 Rescues', 'Completed 50 food rescues');