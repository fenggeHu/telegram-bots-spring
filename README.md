# 数据链路
- 使用ConsumeHandler消费telegram的Update
- 执行链路如下：
```
telegram Update
    -> Bot.consume
        -> ConsumeHandler.matched == true
            -> ConsumeHandler.execute == true   
                -> next ConsumeHandler
```

# Telegram机器人的功能 -- for study & tests
https://core.telegram.org/bots/features
- 实现自定义键盘
- 处理回调查询
- 管理群组中的消息
- 集成外部API服务
  - 扩展功能: 通过集成外部API，如天气服务、金融信息、翻译服务等，机器人可以提供比基本聊天更多的功能。
  - 数据交互: 机器人可以发送请求到外部API，并将获得的数据格式化后呈现给用户，提供实时的数据服务和互动。
  - 自动化任务: 结合外部API，机器人可以执行自动化任务，比如预订、订单管理、或者数据分析等，大大提升效率和用户体验。

- 定义命令Command - 通过@BotFather or API method。  command命名使用小写英文字母和数字

# Update/Message概念
 - chatId：a Telegram chat with a user or a group。 同一个聊天室的ID不变

# API 
https://core.telegram.org/bots/api


# 常见问题
 - Telegram Bot 收不到普通群聊消息的问题。 https://www.hiczp.com/telegram/telegram-bot-shou-bu-dao-pu-tong-qun-liao-xiao-xi-de-wen-ti.html
   BotFather命令：/setprivacy ，选择自己的机器人, 然后选择 Disable

# cmd问题