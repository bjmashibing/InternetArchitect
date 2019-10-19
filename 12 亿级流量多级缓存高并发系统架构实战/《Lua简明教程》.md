# 《Lua简明教程》

## Lua 程序设计

Lua 是由巴西里约热内卢天主教大学（Pontifical Catholic University of Rio de Janeiro）里的一个研究小组于1993年开发的一种轻量、小巧的脚本语言，用标准 C 语言编写，其设计目的是为了嵌入应用程序中，从而为应用程序提供灵活的扩展和定制功能。

官网：http://www.lua.org/

Redis 在 2.6 版本中推出了脚本功能，允许开发者将 Lua 语言编写的脚本传到 Redis 中执行。使用 Lua 脚本的优点有如下几点:

- 减少网络开销：本来需要多次请求的操作，可以一次请求完成，从而节约网络开销；

- 原子操作：Redis 会将整个脚本作为一个整体执行，中间不会执行其它命令；

- 复用：客户端发送的脚本会存储在 Redis 中，从而实现脚本的复用。

## IDE

### EmmyLua插件

https://github.com/EmmyLua/IntelliJ-EmmyLua

https://emmylua.github.io/zh_CN/

### LDT 基于eclipse

https://www.eclipse.org/ldt/

## Lua基础语法

参考

http://book.luaer.cn/

 

### hello world

```lua
print("hello world!")
```





### 保留关键字

`and`       `break`     `do   ` `else`      `elseif`      `end`       `false`    ` for`       `function  if`      `in`        `local`     `nil`      ` not `      `or`      `repeat`    `return`    `then`     ` true`      `until`    ` while`

### 注释

```lua
-- 两个减号是行注释

--[[

 这是块注释

 这是块注释.

 --]]
```





## 变量

### 数字类型

Lua的数字只有double型，64bits

你可以以如下的方式表示数字

 ```lua
num = 1024

num = 3.0

num = 3.1416

num = 314.16e-2

num = 0.31416E1

num = 0xff

num = 0x56
 ```





### 字符串

可以用单引号，也可以用双引号

也可以使用转义字符‘\n’ （换行）， ‘\r’ （回车）， ‘\t’ （横向制表）， ‘\v’ （纵向制表）， ‘\\’ （反斜杠）， ‘\”‘ （双引号）， 以及 ‘\” （单引号)等等

下面的四种方式定义了完全相同的字符串（其中的两个中括号可以用于定义有换行的字符串）

```
a = 'alo\n123"'

a = "alo\n123\""

a = '\97lo\10\04923"'

a = [[alo

123"]]
```





### 空值

C语言中的NULL在Lua中是nil，比如你访问一个没有声明过的变量，就是nil

### 布尔类型

只有nil和false是 false

数字0，‘’空字符串（’\0’）都是true

### 作用域

lua中的变量如果没有特殊说明，全是全局变量，那怕是语句块或是函数里。

变量前加local关键字的是局部变量。

## 控制语句

### while循环

```lua
local i = 0

local max = 10

while i <= max do

print(i)

i = i +1

end
```





### if-else



```lua
local function main()


local age = 140

local sex = 'Male'
 

  if age == 40 and sex =="Male" then
    print(" 男人四十一枝花 ")
  elseif age > 60 and sex ~="Female" then
   
    print("old man without country!")
  elseif age < 20 then
    io.write("too young, too naive!\n")
  
  else
  print("Your age is "..age)
  end

end


-- 调用
main()

```



### for循环

```lua
sum = 0

for i = 100, 1, -2 do

	sum = sum + i

end
```





### 函数

1.

```lua
function myPower(x,y)

  return      y+x

end

power2 = myPower(2,3)

 print(power2)
```





2.

```lua
function newCounter()

   local i = 0
   return function()     -- anonymous function

        i = i + 1

        return i

    end
end

 

c1 = newCounter()

print(c1())  --> 1

print(c1())  --> 2

print(c1())
```





#### 返回值

```lua
name, age,bGay = "yiming", 37, false, "yimingl@hotmail.com"

print(name,age,bGay)
```





 

 ```lua
function isMyGirl(name)
  return name == 'xiao6' , name
end

local bol,name = isMyGirl('xiao6')

print(name,bol)
 ```





## Table

key，value的键值对 类似 map

```lua
lucy = {name='xiao6',age=18,height=165.5}

xiao6.age=35

print(xiao6.name,xiao6.age,xiao6.height)

print(xiao6)
```





#### 数组

```lua
arr = {"string", 100, "xiao6",function() print("memeda") return 1 end}

print(arr[4]())
```





### 遍历

```lua
for k, v in pairs(arr) do

   print(k, v)
end
```



 

### 面向对象

#### 成员函数

```
person = {name='xiao6',age = 18}

  function  person.eat(food)

    print(person.name .." eating "..food)

  end
person.eat("xxoo")
```