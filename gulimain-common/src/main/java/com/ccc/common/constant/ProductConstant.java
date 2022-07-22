package com.ccc.common.constant;


public class ProductConstant {
    public enum  AttrEnum{
        TTR_ENUM_BASE(1,"基本属性"),ATTR_ENUM_SALE(0,"销售属性");
        private int code;
        private String message;


        AttrEnum(int code,String message){
            this.code=code;
            this.message=message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum  StatusEnum{
        NEW_SPU(0,"未上架"),SPU_UP(1,"上架");
        private int code;
        private String message;


        StatusEnum(int code,String message){
            this.code=code;
            this.message=message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
