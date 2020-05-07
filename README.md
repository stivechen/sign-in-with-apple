## sign-in-with-apple
1. appleid授权登录，web服务端请求及验证
2. 使用循环匹配publicKey方式应对Apple更换publickey
3. 使用httpClient请求获取publicKey，保持与官网一致
4. 使用guavaCache缓存publicKey，并定期刷新
5. 基于spring boot
4. 前端计划用apple官网的js demo（待完成）
