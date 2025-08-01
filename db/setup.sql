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

-- Function to check if category amounts match the total_budget
CREATE OR REPLACE FUNCTION validate_category_budgets()
    RETURNS TRIGGER AS $$
DECLARE
    category_sum NUMERIC(10, 2);
    expected_budget NUMERIC(10, 2);
    b_id INTEGER;
BEGIN
    -- Determine the affected budget_id depending on the operation
    IF TG_OP = 'DELETE' THEN
        b_id := OLD.budget_id;
    ELSE
        b_id := NEW.budget_id;
    END IF;

    -- Sum all category amounts for the budget
    SELECT COALESCE(SUM(amount), 0)
    INTO category_sum
    FROM category_budgets
    WHERE budget_id = b_id;

    -- Get the expected total budget
    SELECT total_budget
    INTO expected_budget
    FROM budgets
    WHERE id = b_id;

    -- Validate
    IF category_sum != expected_budget THEN
        RAISE EXCEPTION 'Sum of category budgets (%.2f) does not match total_budget (%.2f)', category_sum, expected_budget;
    END IF;

    RETURN NULL; -- Not used for AFTER triggers
END;
$$ LANGUAGE plpgsql;

-- AFTER INSERT/UPDATE/DELETE trigger on category_budgets
CREATE TRIGGER trg_validate_category_budgets
    AFTER INSERT OR UPDATE OR DELETE ON category_budgets
    FOR EACH ROW
EXECUTE FUNCTION validate_category_budgets();

-- Indexes
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);
CREATE INDEX idx_category_budgets_budget_category ON category_budgets(budget_id, category_id);
