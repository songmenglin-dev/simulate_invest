## ADDED Requirements

### Requirement: 个人资料展示
系统 SHALL 展示当前登录用户的个人资料信息，包括用户名、手机号、邮箱。

#### Scenario: 查看个人信息
- **WHEN** 用户点击导航栏用户菜单进入个人信息页面
- **THEN** 系统显示用户名、手机号、邮箱等资料

### Requirement: 头像上传
系统 SHALL 支持用户上传头像图片到 MinIO 存储，上传成功后更新头像显示。

#### Scenario: 上传头像成功
- **WHEN** 用户选择图片文件并上传
- **THEN** 系统将图片上传到 MinIO，返回头像 URL，页面显示新头像

#### Scenario: 未登录上传被拦截
- **WHEN** 用户未登录状态下尝试上传头像
- **THEN** 系统返回 401 未登录错误，提示用户先登录
