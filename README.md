# CocoDoc 接口文档IDEA导出插件 

主要用于提高团队开发中后端与前端之间的沟通效率，一键生成对应的Rest接口文档，达到快速交付的目的。

## 功能说明

### 使用场景

团队中前端与后端沟通所需文档，无需编译和启动，安装轻松，无需费时编写文档

![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/content1.jpg)

![Image text](https://github.com/zhangjunwu123/CocoDoc/blob/main/img/page.png)

### 操作流程
直接下载代码根目录中的插件包CocoDoc.zip 或者 下载代码自己build插件包

打开IDEA，选择【File】--- 【Settings】---【Plugins】
worddav116a41e5233fc5e7cc14f758727a5f19.png
 worddav12b7017121ccc170252e36aca97e9b7a.png
点击齿轮图标，选择【Install Plugin from Disk】
 worddav0bbc84025ae2deb86d04ef808f768434.png
选择刚才下载的CocoDoc.zip插件包
 worddave3a3b576f5a11c33c14141934b913b81.png
安装之后 重启IDEA
 worddav3149e17531f19ec8f3940b27a38d651b.png

多粒度使用
多种粒度可以导出Rest文档

单个方法导出
例如选择【soms-tenant-rest】模块-【AllotRest类】--【queryPage方法】
注意：导出单个方法一定要选中方法名
 worddav0bb0e1a156dd3d183daafeeb558aceda.png
然后选择菜单栏中【Tools】--【CocoDoc文档生成工具】
 worddav63562c221c9a7b251c9bf8fe00ed9faf.png
弹出的窗口中点击【生成文档】按钮
 worddav2dd81305d272bcc376ee66ac4f26b3b9.png
 worddav205f0ebe31663710bb3e19cb15166785.png
生成的文档会放在桌面，名称为 CocoDoc接口文档.doc
我们打开word文档查看导出结果：
 worddav6e17103acbf45084571581e1bfb5dc67.png
文件中可以点击左侧的目录进入寻址查看
 worddavaa3b2c765962fb4187c9d7b558c7f067.png

单个类导出
单个类导出需要选中rest类
 worddav493fd3160c227a18a25b5e225733d93f.png
后续的步骤相同
效果如下图：
 worddav87560559d93d340903dcd703e3b81914.png

整个包或整个模块导出
这个如果选定的目录内文件太多，可能写文件太费时间。
选中整个模块，比如【soms-tenant-rest】模块
 worddav2d948168112395e627684e633b2ddd8c.png
后续步骤相同
