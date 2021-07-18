# sumk-apollo
sumk和apollo的整合包:

## sumk
https://github.com/youtongluan/sumk

## apollo
https://github.com/ctripcorp/apollo

## 功能
- 所有配置都可生效
- 配置生效顺序：`system属性 -> app.properties -> apollo`，system属性 、app.properties一般存储固定配置
- 支持AppInfo、@Value、@ApolloJsonValue支持属性或方法级别动态注入
    - 如果使用方法级别注入，可以在方法内编写回调逻辑
    - @Value、@ApolloJsonValue目前注入属性类型仅支持string


