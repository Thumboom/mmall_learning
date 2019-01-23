package com.mmall.common;

import com.google.common.collect.Sets;

import javax.swing.*;
import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public interface Role {
        int ROLE_ADMIN = 1;
        int ROLE_CUSTOMER = 0;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }


    public enum ProductStatusEnum{

        ON_SALE(1, "在线");
        private String value;
        private int code;

        ProductStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

}
