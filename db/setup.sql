-- Users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                            name VARCHAR(100) NOT NULL,
                            goal NUMERIC(10, 2) DEFAULT 0
);

-- Expenses table
CREATE TABLE expenses (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                          category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
                          amount NUMERIC(10, 2) NOT NULL,
                          description TEXT,
                          date DATE NOT NULL
);

-- Budgets table
CREATE TABLE budgets (
                         id SERIAL PRIMARY KEY,
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
                         total_budget NUMERIC(10, 2) NOT NULL,
                         UNIQUE(user_id, month)
);

-- Category Budgets table
CREATE TABLE category_budgets (
                                  id SERIAL PRIMARY KEY,
                                  budget_id INTEGER REFERENCES budgets(id) ON DELETE CASCADE,
                                  category_id INTEGER REFERENCES categories(id) ON DELETE CASCADE,
                                  amount NUMERIC(10, 2) NOT NULL,
                                  UNIQUE(budget_id, category_id)
);

-- Optional: Revoked Tokens (for JWT blacklist, if needed)
-- CREATE TABLE revoked_tokens (
--   id SERIAL PRIMARY KEY,
--   token TEXT NOT NULL,
--   revoked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Indexes
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);
CREATE INDEX idx_category_budgets_budget_category ON category_budgets(budget_id, category_id);
