package github.xunolan.rpcproject.utils;

import cn.hutool.core.lang.Snowflake;

public class SnowflakeUtil {
    private static final Snowflake snowflake = new Snowflake();
    public static String getSnowflakeId(){
        return snowflake.nextIdStr();
    }
}
