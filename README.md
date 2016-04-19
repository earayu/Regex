##实现了正则表达式的连接、或、闭包操作,可以使用括号和转义元符号: . | * ( ) \

###主方法在TEST.java
###步骤:
###1. 处理转义符号,然后把正则表达式转换成逆波兰式
###2. 将表达式转换成NFA
###3. 将表达式转换成DFA
###4. 模拟DFA运行

###下一步是实现扩展的正则表达式.

#测试:
RE :(d(a|b)*c)|a   
input:dc   
true   
input:dac   
true   
input:dbbbc   
true   
input:a   
true   

RE :\\\.\*\(\)   
input:\.*()   
true   
