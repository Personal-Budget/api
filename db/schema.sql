CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       supabase_id UUID NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       UNIQUE (supabase_id)
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            name TEXT NOT NULL,
                            goal NUMERIC(10, 2) NOT NULL CHECK (goal > 0),
                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            UNIQUE(user_id, name)
);

CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                          category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
                          amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
                          description TEXT,
                          date DATE NOT NULL,
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE budgets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                         month VARCHAR(7) NOT NULL,
                         total_budget NUMERIC(10, 2) NOT NULL CHECK (total_budget > 0),
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         UNIQUE(user_id, month)
);

CREATE TABLE budget_categories (
                                  id BIGSERIAL PRIMARY KEY,
                                  budget_id BIGINT REFERENCES budgets(id) ON DELETE CASCADE,
                                  category_id BIGINT REFERENCES categories(id) ON DELETE CASCADE,
                                  budgeted_amount NUMERIC(10, 2) NOT NULL CHECK (budgeted_amount > 0),
                                  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  UNIQUE(budget_id, category_id)
);

-- Function to check if the sum of budgeted_amounts <= the total_budget
CREATE OR REPLACE FUNCTION validate_budgeted_amounts()
    RETURNS TRIGGER AS $$
DECLARE
    budgeted_amounts_sum NUMERIC(10, 2);
    expected_budget NUMERIC(10, 2);
    b_id BIGINT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        b_id := OLD.budget_id;
    ELSE
        b_id := NEW.budget_id;
    END IF;

    SELECT COALESCE(SUM(budgeted_amount), 0)
    INTO budgeted_amounts_sum
    FROM budget_categories
    WHERE budget_id = b_id;

    SELECT total_budget
    INTO expected_budget
    FROM budgets
    WHERE id = b_id;

    IF budgeted_amounts_sum > expected_budget THEN
        RAISE EXCEPTION 'Sum of budgeted_amounts (%.2f) is greater than total_budget (%.2f)', budgeted_amounts_sum, expected_budget;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- AFTER INSERT/UPDATE/DELETE trigger on budget_categories
CREATE TRIGGER trg_validate_budgeted_amounts
    AFTER INSERT OR UPDATE OR DELETE ON budget_categories
    FOR EACH ROW
EXECUTE FUNCTION validate_budgeted_amounts();

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

CREATE TRIGGER trg_update_budget_categories_updated_at
    BEFORE UPDATE ON budget_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Indexes on user_id fields
CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budget_categories_budget_id ON budget_categories(budget_id);

-- Unique indexes for specified fields
CREATE UNIQUE INDEX idx_users_supabase_id ON users(supabase_id);
CREATE UNIQUE INDEX idx_categories_user_id_name ON categories(user_id, name);
CREATE UNIQUE INDEX idx_budgets_user_id_month ON budgets(user_id, month);
CREATE UNIQUE INDEX idx_budget_categories_budget_id_category_id ON budget_categories(budget_id, category_id);


