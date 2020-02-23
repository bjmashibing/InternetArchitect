pragma solidity ^0.5.12;


contract MyCoin{
    
    // 管理员 
    address public operator;
    
    
    // 所有的账户余额 
    mapping(address => uint) balance;
    
    // 动态语言  
    constructor() public {
        
        operator = msg.sender;
        
    }
    
    // 发币  0xdD870fA1b7C4700F2BD7f44238821C26f7392148
    function create(address recevier,uint amount) public{
        // 只能由管理员调起 
       require(msg.sender == operator);
        
        // 给一个账户 加币
       balance[recevier] += amount;
        
    }
    
    // 转账 0xdD870fA1b7C4700F2BD7f44238821C26f7392148  4,0xdD870fA1b7C4700F2BD7f44238821C26f7392148 1,0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c
    
    function transfer(address recevier,uint amount)public {
        
        require(balance[msg.sender] >= amount);
        
        balance[msg.sender] -= amount;
        
        balance[recevier] += amount;
        
        
    }
    
    
    
    
    // 查看余额 
    
    function viewBalance(address addr) public view returns(uint) {
        
        return balance[addr];
        
    }
    
}