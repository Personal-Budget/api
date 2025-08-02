package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.db.entities.UserEntity;

public interface UserService {
    UserEntity getUserFromSupabaseId(String supabaseId);
}
