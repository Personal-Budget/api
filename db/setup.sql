-- Users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       supabase_id UUID NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Categories table
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            name VARCHAR(100) NOT NULL,
                            goal NUMERIC(10, 2) DEFAULT 0,
                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            UNIQUE(user_id, name)
);

-- Expenses table
CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                          category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
                          amount NUMERIC(10, 2) NOT NULL,
                          description TEXT,
                          date DATE NOT NULL,
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Budgets table
CREATE TABLE budgets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                         month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
                         total_budget NUMERIC(10, 2) NOT NULL,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         UNIQUE(user_id, month)
);

-- Category Budgets table
CREATE TABLE category_budgets (
                                  id BIGSERIAL PRIMARY KEY,
                                  budget_id BIGINT REFERENCES budgets(id) ON DELETE CASCADE,
                                  category_id BIGINT REFERENCES categories(id) ON DELETE CASCADE,
                                  amount NUMERIC(10, 2) NOT NULL,
                                  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  UNIQUE(budget_id, category_id)
);

-- Function to check if category amounts match the total_budget
CREATE OR REPLACE FUNCTION validate_category_budgets()
    RETURNS TRIGGER AS $$
DECLARE
    category_sum NUMERIC(10, 2);
    expected_budget NUMERIC(10, 2);
    b_id BIGINT;
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

-- Function to update the "updated_at" field automatically in every table
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach trigger to each table for UPDATEs
CREATE TRIGGER trg_update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_update_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_update_expenses_updated_at
    BEFORE UPDATE ON expenses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_update_budgets_updated_at
    BEFORE UPDATE ON budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_update_category_budgets_updated_at
    BEFORE UPDATE ON category_budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Indexes
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);
CREATE INDEX idx_category_budgets_budget_category ON category_budgets(budget_id, category_id);
