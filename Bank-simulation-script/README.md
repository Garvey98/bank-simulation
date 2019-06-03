# 高并发测试脚本

`src\main\java` 下存储的源代码

`AccountTransaction.java` 是对注册销户的模拟

通过对其中的main函数下的子函数调用 `Myregister(myUser[id], myPassword[id], myType[0]);` 中 `myType[0]`（注册）`myType[1]`（销户）来批量模拟

`MyBank.java` 是对存取款的模拟，通过对其 `main` 函数中循环体的循环次数控制线程个数。调用 `QueryTransaction.java` 和 `Transaction.java` 中的函数进行查询和存取款控制。