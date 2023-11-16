import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:alikit4/alikit4.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final aliKit = Alikit4();
  String result = "";

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              Container(
                margin: const EdgeInsets.all(20),
                child: Text("result:$result"),
              ),
              button("初始化", () {
                aliKit.init().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("登录", () {
                aliKit.login().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("退出登录", () {
                aliKit.logout().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("获取用户信息", () {
                aliKit.getUserInfo().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("打开商品详情", () {
                Map<String,Object> params = {
                  "itemId":"659874359015",
                };
                aliKit.openByBizCode(params).then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("打开店铺", () {
                Map<String,Object> params = {
                  "shopId":"113669907",
                  "sellerId":"2241885069",
                };
                aliKit.openByBizCode(params).then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("打开购物车", () {
                Map<String,Object> params = {};
                aliKit.openCart(params).then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("打开Url", () {
                Map<String,Object> params = {
                  "url":"https://s.click.taobao.com/zt0x4Vu"
                };
                aliKit.openByUrl(params).then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("检查登录状态", () {
                aliKit.checkSession().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
              button("获取utdid", () {
                aliKit.getUtdid().then((map) {
                  setState(() {
                    result = map.toString();
                  });
                });
              }),
            ],
          ),
        ),
      ),
    );
  }

  button(text, Function function) {
    return InkWell(
      onTap: () {
        function();
      },
      child: Container(
        margin: const EdgeInsets.only(left: 20, right: 20, bottom: 20),
        width: double.infinity,
        height: 40,
        color: Colors.blueGrey,
        child: Center(child: Text(text)),
      ),
    );
  }
}
