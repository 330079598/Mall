package com.stone.common.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author: stone
 * @Title: ProductConstant
 * @date: 2022/1/20 16:19
 * @Description:
 */
public class ProductConstant {

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }
}
