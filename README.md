本例子是展示如何利用类加载器进行隔离。

## Architecture
`main module`
运行插件

`plugin module`
含有一个demo-plugin作为示例


## Test
运行`main module`下的测试用例`DemoTest`。
>- `testJar`用例。演示对于插件为jar的加载。插件路径在`try-plugin/main/jar-plugin/`。
分别演示自定义类加载器、ServiceLoader方式加载Jar的类、类加载器方式直接加载Jar的类
>- `testClass`用例。演示对于插件为class的加载。插件路径在`try-plugin/main/class-plugin/demo/`