# CocoDoc 接口文档IDEA导出插件 

主要用于提高团队开发中后端与前端之间的沟通效率，一键生成对应的Rest接口文档，达到快速交付的目的。

## 功能说明

### 使用场景

团队中前端与后端沟通所需文档，无需编译和启动，安装轻松，无需费时编写文档

![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/content1.jpg)

![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/page.png)

## 操作流程
直接下载代码根目录中的插件包CocoDoc.zip 或者 下载代码自己build插件包

打开IDEA，选择【File】--- 【Settings】---【Plugins】
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/01.png)
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/02.png)
点击齿轮图标，选择【Install Plugin from Disk】
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/03.png)
选择刚才下载的CocoDoc.zip插件包
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/04.png)
安装之后 重启IDEA
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/05.png)

多粒度使用
多种粒度可以导出Rest文档

单个方法导出
例如选择【soms-tenant-rest】模块-【AllotRest类】--【queryPage方法】
注意：导出单个方法一定要选中方法名
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/06.png)
然后选择菜单栏中【Tools】--【CocoDoc文档生成工具】
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/07.png)
弹出的窗口中点击【生成文档】按钮
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/08.png)
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/09.png)
生成的文档会放在桌面，名称为 CocoDoc接口文档.doc
我们打开word文档查看导出结果：
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/10.png)
文件中可以点击左侧的目录进入寻址查看
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/11.png)

单个类导出
单个类导出需要选中rest类
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/12.png)
后续的步骤相同
效果如下图：
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/13.png)

整个包或整个模块导出
这个如果选定的目录内文件太多，可能写文件太费时间。
选中整个模块，比如【soms-tenant-rest】模块
![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/14.png)
后续步骤相同
