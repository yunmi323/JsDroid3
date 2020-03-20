### 欢迎使用JsDroid3.0！
#### 介绍
JsDroid3去除了JsDroid2多余的功能，追求以最简单化的方式开发最需要的功能。
#### 文档(感谢痞老板贡献文档~)
http://showdoc.jsdroid.com/web/#/20
#### 软件下载
- 电脑端
JsDroid: http://oss.beijing.jsdroid.com/jsdroid3/jsdroid-qt-windows-x86.exe
JSDK: http://oss.beijing.jsdroid.com/jsdroid3/jsdk.exe
IDEA: https://download-cf.jetbrains.com/idea/ideaIC-2019.3.4.exe

- 手机端
JsDroid: http://oss.beijing.jsdroid.com/jsdroid3/JsDroid3.0.apk

#### 软件截图

#### 常用命令

- print 打印日志

```groovy
print "hello"
```

- toast 弹出消息

```groovy
toast "hello"
```

- click 点击坐标

```groovy
click 100,100
```

- swipe 滑动

```groovy
//从点(100,100)滑动到点(500,500)
swipe 100,100,500,500
//从点(100,100)滑动到点(500,500),补间20,每补间耗时5毫秒
swipe 100,100,500,500,20

```

- sleep 休眠

```groovy
//休眠1秒
sleep 1000
```

- time 时间戳

```groovy
t = time()
print t
```

- findNode 查找单个节点

```groovy
//正则查找界面上的节点，注意下面的符号"~"，它不是打错的字符，而是将"JsDroid.*"字符串编程正则表达式
def node = findNode ~"JsDroid.*"
if(node){
    print node.text    
}

```

- findNodeAll 查找多个节点

```groovy
//正则查找界面上的所有文字类型节点，注意下面的符号"~"，它不是打错的字符，而是将".*Text.*"字符串编程正则表达式
def nodes = findNodeAll ~".*TextView.*"
if(nodes){
    for(node in nodes){
        if(node.text){
            print node.text
        }    
    }

}
```
- GNode遍历节点

```groovy
//定义数组保存遍历结果
def textArray=[]
//遍历所有文字
GNode.eachNode{
node->
    //获取节点的文字
    def text = node.text
    if(text){
        //将文字保存到数组textArray
        textArray.add(text)
    }
}
//输出数组，将每项用换行符"\n"隔开
print textArray.join("\n")
```

- inputText 输入文字

```groovy
//在输入框输入文字"JsDroid"，注意要先点击输入框
inputText "JsDroid"
```

- clearText 清除文字

```groovy
//清除光标前后各1000文字
clearText 1000,1000
```

- findPic 找图

```groovy
//参数分别为：图片路径(放到res文件夹里面)、左、上、右、下、色差、相似度
def ret = findPic "find.png",0,0,0,0,0x050505,0.9f
if(ret){
    click ret.x,ret.y
}
```

- findImg 多分辨率找图

```groovy
//参数分别为：图片路径(放到res文件夹里面)、左、上、右、下、色差、相似度
def ret = findImg "find.png",0,0,0,0,0x050505,0.9f
if(ret){
    click ret.left,ret.top
}
```

- readConfig 读取配置参数

```groovy
def value = readConfig("key","默认值")
print value
```


#### 许可
个人随意，商业授权找who(qq980008027)，很便宜，可定制。

#### 小广告

接安卓软件定制、脚本定制、云控定制、中控定制，带上需求文档，联系who(qq980008027)。

JsDroid魔盒招代理，联系who(qq980008027)。

JsDroid群招管理，要求：会管理||会推广||会java||会groovy||会JsDroid3。
