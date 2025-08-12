package com.biancodavide3.budgeting.util;

import com.biancodavide3.budgeting.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

public class Users {
    public static Long extractUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getUser().getId();
    }
}
