package com.biancodavide3.budgeting.security.user;

import com.biancodavide3.budgeting.db.entities.UserEntity;

public interface UserService {
    UserEntity getUserFromSupabaseId(String supabaseId);
}
